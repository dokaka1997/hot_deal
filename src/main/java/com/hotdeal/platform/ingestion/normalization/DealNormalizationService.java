package com.hotdeal.platform.ingestion.normalization;

import com.hotdeal.platform.alert.application.AlertMatchingService;
import com.hotdeal.platform.deal.dedup.DealDeduplicationService;
import com.hotdeal.platform.deal.dedup.model.DeduplicationDecision;
import com.hotdeal.platform.common.exception.ResourceNotFoundException;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.repository.DealRepository;
import com.hotdeal.platform.deal.scoring.DealPriceAndScoreService;
import com.hotdeal.platform.ingestion.normalization.mapper.RawDealMapper;
import com.hotdeal.platform.ingestion.normalization.mapper.RawDealMapperRegistry;
import com.hotdeal.platform.ingestion.normalization.model.NormalizedDealRecord;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import com.hotdeal.platform.ingestion.persistence.repository.RawDealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DealNormalizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DealNormalizationService.class);

    private final RawDealRepository rawDealRepository;
    private final DealRepository dealRepository;
    private final DealDeduplicationService dealDeduplicationService;
    private final DealPriceAndScoreService dealPriceAndScoreService;
    private final AlertMatchingService alertMatchingService;
    private final RawDealMapperRegistry rawDealMapperRegistry;
    private final NormalizationValidationService normalizationValidationService;
    private final Clock clock;

    public DealNormalizationService(RawDealRepository rawDealRepository,
                                    DealRepository dealRepository,
                                    DealDeduplicationService dealDeduplicationService,
                                    DealPriceAndScoreService dealPriceAndScoreService,
                                    AlertMatchingService alertMatchingService,
                                    RawDealMapperRegistry rawDealMapperRegistry,
                                    NormalizationValidationService normalizationValidationService,
                                    Clock clock) {
        this.rawDealRepository = rawDealRepository;
        this.dealRepository = dealRepository;
        this.dealDeduplicationService = dealDeduplicationService;
        this.dealPriceAndScoreService = dealPriceAndScoreService;
        this.alertMatchingService = alertMatchingService;
        this.rawDealMapperRegistry = rawDealMapperRegistry;
        this.normalizationValidationService = normalizationValidationService;
        this.clock = clock;
    }

    @Transactional
    public NormalizationBatchResult normalizePending(int batchSize) {
        int effectiveBatchSize = Math.max(1, batchSize);
        List<RawDealEntity> rawDeals = rawDealRepository
                .findByStatusOrderByIngestedAtAsc(RawDealStatus.NEW, PageRequest.of(0, effectiveBatchSize))
                .getContent();

        int normalized = 0;
        int rejected = 0;
        int failed = 0;

        for (RawDealEntity rawDeal : rawDeals) {
            ProcessingOutcome outcome = processRawDeal(rawDeal);
            switch (outcome) {
                case NORMALIZED -> normalized++;
                case REJECTED -> rejected++;
                case FAILED -> failed++;
            }
        }

        if (!rawDeals.isEmpty()) {
            LOGGER.info("normalization batch completed selected={} normalized={} rejected={} failed={}",
                    rawDeals.size(), normalized, rejected, failed);
        }

        return new NormalizationBatchResult(rawDeals.size(), normalized, rejected, failed);
    }

    @Transactional
    public NormalizationSingleResult normalizeRawDeal(Long rawDealId) {
        RawDealEntity rawDeal = rawDealRepository.findById(rawDealId)
                .orElseThrow(() -> new ResourceNotFoundException("Raw deal not found with id: " + rawDealId));
        ProcessingOutcome outcome = processRawDeal(rawDeal);
        return switch (outcome) {
            case NORMALIZED -> new NormalizationSingleResult(rawDeal.getId(), RawDealStatus.NORMALIZED, "normalized");
            case REJECTED -> new NormalizationSingleResult(rawDeal.getId(), RawDealStatus.REJECTED, rawDeal.getParseError());
            case FAILED -> new NormalizationSingleResult(rawDeal.getId(), RawDealStatus.ERROR, rawDeal.getParseError());
        };
    }

    private ProcessingOutcome processRawDeal(RawDealEntity rawDeal) {
        try {
            NormalizedDealRecord record = map(rawDeal);
            List<String> validationErrors = normalizationValidationService.validate(record);
            if (!validationErrors.isEmpty()) {
                markRejected(rawDeal, String.join("; ", validationErrors));
                return ProcessingOutcome.REJECTED;
            }

            upsertDeal(rawDeal, record);
            markNormalized(rawDeal);
            return ProcessingOutcome.NORMALIZED;
        } catch (Exception exception) {
            markError(rawDeal, safeErrorMessage(exception));
            LOGGER.error("normalization failed rawDealId={} sourceCode={} message={}",
                    rawDeal.getId(), rawDeal.getSource().getCode(), exception.getMessage(), exception);
            return ProcessingOutcome.FAILED;
        }
    }

    private NormalizedDealRecord map(RawDealEntity rawDeal) {
        RawDealMapper mapper = rawDealMapperRegistry.resolve(rawDeal.getSource().getCode());
        return mapper.map(rawDeal.getSource(), rawDeal);
    }

    private void upsertDeal(RawDealEntity rawDeal, NormalizedDealRecord normalized) {
        DeduplicationDecision deduplicationDecision = dealDeduplicationService.resolve(rawDeal, normalized);
        DealEntity dealEntity = findExistingDeal(rawDeal, normalized, deduplicationDecision).orElseGet(DealEntity::new);
        boolean isNew = dealEntity.getId() == null;
        OffsetDateTime now = now();

        dealEntity.setSource(rawDeal.getSource());
        dealEntity.setProduct(deduplicationDecision.product());
        dealEntity.setSourceDealId(normalized.sourceDealId());
        dealEntity.setFingerprint(rawDeal.getSourceRecordHash());
        dealEntity.setDedupeKey(deduplicationDecision.dedupeKey());
        dealEntity.setTitle(normalized.title());
        dealEntity.setNormalizedTitle(normalized.normalizedTitle());
        dealEntity.setDescription(normalized.description());
        dealEntity.setBrand(normalized.brand());
        dealEntity.setCategory(normalized.category());
        dealEntity.setExternalUrl(normalized.externalUrl());
        dealEntity.setImageUrl(normalized.imageUrl());
        dealEntity.setCurrency(normalized.currency());
        dealEntity.setOriginalPrice(normalized.originalPrice());
        dealEntity.setDealPrice(normalized.dealPrice());
        dealEntity.setDiscountPercent(calculateDiscountPercent(normalized.originalPrice(), normalized.dealPrice()));
        dealEntity.setCouponCode(normalized.couponCode());
        dealEntity.setStatus(normalized.status());
        dealEntity.setValidFrom(normalized.validFrom());
        dealEntity.setValidUntil(normalized.validUntil());
        dealEntity.setLastSeenAt(now);
        dealEntity.setFirstSeenAt(isNew ? now : dealEntity.getFirstSeenAt());
        dealEntity.setMetadata(mergeMetadata(rawDeal.getPayload(), normalized.metadata(), deduplicationDecision));

        DealEntity savedDeal = dealRepository.save(dealEntity);
        dealPriceAndScoreService.refreshForDeal(savedDeal, "NORMALIZATION");
        alertMatchingService.matchAndDispatchForDeal(savedDeal, "NORMALIZATION");
    }

    private Optional<DealEntity> findExistingDeal(RawDealEntity rawDeal,
                                                  NormalizedDealRecord normalized,
                                                  DeduplicationDecision deduplicationDecision) {
        if (StringUtils.hasText(normalized.sourceDealId())) {
            Optional<DealEntity> bySourceDealId = dealRepository
                    .findBySourceIdAndSourceDealId(rawDeal.getSource().getId(), normalized.sourceDealId());
            if (bySourceDealId.isPresent()) {
                return bySourceDealId;
            }
        }
        if (StringUtils.hasText(rawDeal.getSourceRecordHash())) {
            Optional<DealEntity> byFingerprint = dealRepository.findBySourceIdAndFingerprint(
                    rawDeal.getSource().getId(),
                    rawDeal.getSourceRecordHash()
            );
            if (byFingerprint.isPresent()) {
                return byFingerprint;
            }
        }
        if (StringUtils.hasText(deduplicationDecision.dedupeKey())) {
            return dealRepository.findTopBySourceIdAndDedupeKeyOrderByLastSeenAtDesc(
                    rawDeal.getSource().getId(),
                    deduplicationDecision.dedupeKey()
            );
        }
        return Optional.empty();
    }

    private void markNormalized(RawDealEntity rawDeal) {
        rawDeal.setStatus(RawDealStatus.NORMALIZED);
        rawDeal.setNormalizedAt(now());
        rawDeal.setParseError(null);
        rawDealRepository.save(rawDeal);
    }

    private void markRejected(RawDealEntity rawDeal, String reason) {
        rawDeal.setStatus(RawDealStatus.REJECTED);
        rawDeal.setNormalizedAt(now());
        rawDeal.setParseError(reason);
        rawDealRepository.save(rawDeal);
        LOGGER.warn("raw deal rejected rawDealId={} sourceCode={} reason={}",
                rawDeal.getId(), rawDeal.getSource().getCode(), reason);
    }

    private void markError(RawDealEntity rawDeal, String reason) {
        rawDeal.setStatus(RawDealStatus.ERROR);
        rawDeal.setNormalizedAt(now());
        rawDeal.setParseError(reason);
        rawDealRepository.save(rawDeal);
    }

    private BigDecimal calculateDiscountPercent(BigDecimal originalPrice, BigDecimal dealPrice) {
        if (originalPrice == null || dealPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal discount = originalPrice.subtract(dealPrice);
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return discount
                .divide(originalPrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> mergeMetadata(Map<String, Object> payload,
                                              Map<String, Object> metadata,
                                              DeduplicationDecision deduplicationDecision) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (metadata != null) {
            merged.putAll(metadata);
        }
        merged.put("deduplication", deduplicationDecision.decisionTrace());
        if (payload != null && !payload.isEmpty()) {
            merged.put("rawPayload", payload);
        }
        return merged;
    }

    private String safeErrorMessage(Exception exception) {
        String message = exception.getMessage();
        if (!StringUtils.hasText(message)) {
            return exception.getClass().getSimpleName();
        }
        return message.length() > 2000 ? message.substring(0, 2000) : message;
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }

    private enum ProcessingOutcome {
        NORMALIZED,
        REJECTED,
        FAILED
    }
}
