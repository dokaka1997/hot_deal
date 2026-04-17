package com.hotdeal.platform.deal.scoring;

import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.repository.DealRepository;
import com.hotdeal.platform.deal.pricing.PriceHistoryService;
import com.hotdeal.platform.deal.scoring.config.DealScoringProperties;
import com.hotdeal.platform.deal.scoring.model.DealScoreBreakdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DealScoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DealScoringService.class);

    private static final String FORMULA_VERSION = "rule-v1";
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal DISCOUNT_CAP = BigDecimal.valueOf(80);
    private static final BigDecimal DISCOUNT_WEIGHT = new BigDecimal("0.45");
    private static final BigDecimal ACTIVE_SCORE = new BigDecimal("30.00");
    private static final BigDecimal INACTIVE_SCORE = new BigDecimal("5.00");
    private static final BigDecimal SOURCE_CONFIDENCE_WEIGHT = new BigDecimal("20.00");
    private static final BigDecimal LOWEST_PRICE_EXACT_SCORE = new BigDecimal("14.00");
    private static final BigDecimal LOWEST_PRICE_NEAR_SCORE = new BigDecimal("8.00");
    private static final BigDecimal LOWEST_PRICE_CLOSE_SCORE = new BigDecimal("3.00");

    private final DealRepository dealRepository;
    private final PriceHistoryService priceHistoryService;
    private final DealScoringProperties dealScoringProperties;

    public DealScoringService(DealRepository dealRepository,
                              PriceHistoryService priceHistoryService,
                              DealScoringProperties dealScoringProperties) {
        this.dealRepository = dealRepository;
        this.priceHistoryService = priceHistoryService;
        this.dealScoringProperties = dealScoringProperties;
    }

    @Transactional
    public DealScoreBreakdown scoreAndPersist(DealEntity deal) {
        int lookbackDays = Math.max(1, dealScoringProperties.getLookbackDays());
        BigDecimal lowestRecentPrice = priceHistoryService.findLowestObservedPrice(deal, lookbackDays)
                .orElse(deal.getDealPrice());

        double sourceConfidence = resolveSourceConfidence(deal);
        BigDecimal discountPercent = resolveDiscountPercent(deal);

        BigDecimal discountScore = cap(discountPercent, DISCOUNT_CAP)
                .multiply(DISCOUNT_WEIGHT)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal statusScore = statusScore(deal.getStatus());
        BigDecimal sourceConfidenceScore = BigDecimal.valueOf(sourceConfidence)
                .multiply(SOURCE_CONFIDENCE_WEIGHT)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePositionScore = pricePositionScore(deal.getDealPrice(), lowestRecentPrice);

        BigDecimal finalScore = discountScore
                .add(statusScore)
                .add(sourceConfidenceScore)
                .add(pricePositionScore);
        finalScore = clamp(finalScore, BigDecimal.ZERO, HUNDRED).setScale(2, RoundingMode.HALF_UP);

        DealScoreBreakdown breakdown = new DealScoreBreakdown(
                finalScore,
                discountScore,
                statusScore,
                sourceConfidenceScore,
                pricePositionScore,
                lowestRecentPrice,
                sourceConfidence,
                lookbackDays,
                FORMULA_VERSION
        );

        deal.setDealScore(finalScore);
        deal.setMetadata(mergeScoreMetadata(deal.getMetadata(), breakdown));
        dealRepository.save(deal);

        LOGGER.info("deal score updated dealId={} score={} discountScore={} statusScore={} sourceScore={} pricePositionScore={}",
                deal.getId(), finalScore, discountScore, statusScore, sourceConfidenceScore, pricePositionScore);

        return breakdown;
    }

    private BigDecimal resolveDiscountPercent(DealEntity deal) {
        if (deal.getDiscountPercent() != null) {
            return clamp(deal.getDiscountPercent(), BigDecimal.ZERO, HUNDRED);
        }
        if (deal.getOriginalPrice() == null || deal.getDealPrice() == null || deal.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = deal.getOriginalPrice().subtract(deal.getDealPrice());
        if (discount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return discount.divide(deal.getOriginalPrice(), 6, RoundingMode.HALF_UP)
                .multiply(HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal statusScore(DealStatus status) {
        if (status == null) {
            return BigDecimal.ZERO;
        }
        return switch (status) {
            case ACTIVE -> ACTIVE_SCORE;
            case INACTIVE -> INACTIVE_SCORE;
            case EXPIRED -> BigDecimal.ZERO;
        };
    }

    private BigDecimal pricePositionScore(BigDecimal currentPrice, BigDecimal lowestRecentPrice) {
        if (currentPrice == null || lowestRecentPrice == null || lowestRecentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (currentPrice.compareTo(lowestRecentPrice) <= 0) {
            return LOWEST_PRICE_EXACT_SCORE;
        }
        BigDecimal ratio = currentPrice.divide(lowestRecentPrice, 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal("1.05")) <= 0) {
            return LOWEST_PRICE_NEAR_SCORE;
        }
        if (ratio.compareTo(new BigDecimal("1.15")) <= 0) {
            return LOWEST_PRICE_CLOSE_SCORE;
        }
        return BigDecimal.ZERO;
    }

    private double resolveSourceConfidence(DealEntity deal) {
        double defaultConfidence = clampConfidence(dealScoringProperties.getDefaultSourceConfidence());
        if (deal.getSource() == null || deal.getSource().getMetadata() == null || deal.getSource().getMetadata().isEmpty()) {
            return defaultConfidence;
        }
        Object raw = Optional.ofNullable(deal.getSource().getMetadata().get("confidenceScore"))
                .orElse(deal.getSource().getMetadata().get("confidence"));
        if (raw == null) {
            return defaultConfidence;
        }
        if (raw instanceof Number number) {
            return clampConfidence(number.doubleValue());
        }
        try {
            return clampConfidence(Double.parseDouble(String.valueOf(raw).trim()));
        } catch (NumberFormatException ignored) {
            return defaultConfidence;
        }
    }

    private double clampConfidence(double value) {
        if (value < 0d) {
            return 0d;
        }
        if (value > 1d) {
            return 1d;
        }
        return value;
    }

    private BigDecimal cap(BigDecimal value, BigDecimal max) {
        BigDecimal safe = value == null ? BigDecimal.ZERO : value;
        if (safe.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (safe.compareTo(max) > 0) {
            return max;
        }
        return safe;
    }

    private BigDecimal clamp(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value.compareTo(min) < 0) {
            return min;
        }
        if (value.compareTo(max) > 0) {
            return max;
        }
        return value;
    }

    private Map<String, Object> mergeScoreMetadata(Map<String, Object> existing, DealScoreBreakdown breakdown) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        if (existing != null) {
            metadata.putAll(existing);
        }

        Map<String, Object> score = new LinkedHashMap<>();
        score.put("formulaVersion", breakdown.formulaVersion());
        score.put("lookbackDays", breakdown.lookbackDays());
        score.put("lowestRecentPrice", breakdown.lowestRecentPrice());
        score.put("sourceConfidence", breakdown.sourceConfidence());
        score.put("discountScore", breakdown.discountScore());
        score.put("statusScore", breakdown.statusScore());
        score.put("sourceConfidenceScore", breakdown.sourceConfidenceScore());
        score.put("pricePositionScore", breakdown.pricePositionScore());
        score.put("finalScore", breakdown.finalScore());

        metadata.put("scoreBreakdown", score);
        return metadata;
    }
}
