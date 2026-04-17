package com.hotdeal.platform.deal.pricing;

import com.hotdeal.platform.deal.persistence.entity.AvailabilityStatus;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.entity.PriceHistoryEntity;
import com.hotdeal.platform.deal.persistence.repository.PriceHistoryRepository;
import com.hotdeal.platform.deal.pricing.model.PriceSnapshotResult;
import com.hotdeal.platform.deal.scoring.config.DealScoringProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@Service
public class PriceHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceHistoryService.class);

    private final PriceHistoryRepository priceHistoryRepository;
    private final DealScoringProperties dealScoringProperties;
    private final Clock clock;

    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository,
                               DealScoringProperties dealScoringProperties,
                               Clock clock) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.dealScoringProperties = dealScoringProperties;
        this.clock = clock;
    }

    @Transactional
    public PriceSnapshotResult captureSnapshotIfNeeded(DealEntity deal, String trigger) {
        if (deal.getId() == null) {
            throw new IllegalStateException("Cannot capture price snapshot for unsaved deal");
        }

        AvailabilityStatus availabilityStatus = toAvailabilityStatus(deal.getStatus());
        Optional<PriceHistoryEntity> latest = priceHistoryRepository.findTopByDealIdOrderByCapturedAtDesc(deal.getId());

        if (latest.isPresent() && !shouldCreateSnapshot(latest.get(), deal, availabilityStatus)) {
            return new PriceSnapshotResult(false, "unchanged");
        }

        PriceHistoryEntity snapshot = new PriceHistoryEntity();
        snapshot.setDeal(deal);
        snapshot.setProduct(deal.getProduct());
        snapshot.setSource(deal.getSource());
        snapshot.setCapturedAt(now());
        snapshot.setCurrency(deal.getCurrency());
        snapshot.setOriginalPrice(deal.getOriginalPrice());
        snapshot.setDealPrice(deal.getDealPrice());
        snapshot.setDiscountPercent(deal.getDiscountPercent());
        snapshot.setAvailabilityStatus(availabilityStatus);

        try {
            priceHistoryRepository.save(snapshot);
        } catch (DataIntegrityViolationException integrityViolationException) {
            LOGGER.warn("price snapshot skipped due to concurrent insert dealId={} message={}",
                    deal.getId(), integrityViolationException.getMessage());
            return new PriceSnapshotResult(false, "concurrent_duplicate");
        }
        LOGGER.info("price snapshot created dealId={} sourceId={} trigger={} price={} availability={}",
                deal.getId(), deal.getSource().getId(), trigger, deal.getDealPrice(), availabilityStatus);
        return new PriceSnapshotResult(true, trigger);
    }

    @Transactional(readOnly = true)
    public Optional<BigDecimal> findLowestObservedPrice(DealEntity deal, int lookbackDays) {
        if (deal.getId() == null) {
            return Optional.empty();
        }
        OffsetDateTime since = now().minusDays(Math.max(1, lookbackDays));

        BigDecimal lowestByProduct = null;
        if (deal.getProduct() != null && deal.getProduct().getId() != null) {
            lowestByProduct = priceHistoryRepository.findMinDealPriceByProductSince(deal.getProduct().getId(), since);
        }
        if (lowestByProduct != null) {
            return Optional.of(lowestByProduct);
        }

        BigDecimal lowestByDeal = priceHistoryRepository.findMinDealPriceByDealSince(deal.getId(), since);
        return Optional.ofNullable(lowestByDeal);
    }

    private boolean shouldCreateSnapshot(PriceHistoryEntity latest,
                                         DealEntity deal,
                                         AvailabilityStatus availabilityStatus) {
        if (!Objects.equals(latest.getDealPrice(), deal.getDealPrice())) {
            return true;
        }
        if (!Objects.equals(latest.getOriginalPrice(), deal.getOriginalPrice())) {
            return true;
        }
        if (!Objects.equals(latest.getCurrency(), deal.getCurrency())) {
            return true;
        }
        if (latest.getAvailabilityStatus() != availabilityStatus) {
            return true;
        }

        long minIntervalMinutes = Math.max(0L, dealScoringProperties.getSnapshot().getMinIntervalMinutes());
        if (minIntervalMinutes <= 0L) {
            return false;
        }
        OffsetDateTime threshold = now().minusMinutes(minIntervalMinutes);
        return latest.getCapturedAt().isBefore(threshold);
    }

    private AvailabilityStatus toAvailabilityStatus(DealStatus dealStatus) {
        if (dealStatus == null) {
            return AvailabilityStatus.UNKNOWN;
        }
        return switch (dealStatus) {
            case ACTIVE -> AvailabilityStatus.IN_STOCK;
            case INACTIVE, EXPIRED -> AvailabilityStatus.OUT_OF_STOCK;
        };
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
