package com.hotdeal.platform.ingestion.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hotdeal.platform.ingestion.collector.CollectedRawDeal;
import com.hotdeal.platform.ingestion.persistence.entity.CollectorJobExecutionEntity;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.repository.RawDealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class RawDealPersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RawDealPersistenceService.class);

    private final RawDealRepository rawDealRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public RawDealPersistenceService(RawDealRepository rawDealRepository,
                                     ObjectMapper objectMapper,
                                     Clock clock) {
        this.rawDealRepository = rawDealRepository;
        this.objectMapper = objectMapper.copy().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        this.clock = clock;
    }

    @Transactional
    public RawPersistenceResult persist(SourceEntity source,
                                        CollectorJobExecutionEntity jobExecution,
                                        List<CollectedRawDeal> records) {
        int inserted = 0;
        int updated = 0;
        int failed = 0;

        for (CollectedRawDeal collected : records) {
            try {
                String sourceRecordKey = resolveRecordKey(collected);
                String sourceRecordHash = resolveRecordHash(collected);
                Optional<RawDealEntity> existing = findExisting(source.getId(), collected.sourceDealId(), sourceRecordKey, sourceRecordHash);

                if (existing.isPresent()) {
                    RawDealEntity rawDeal = existing.get();
                    updateExisting(rawDeal, collected, jobExecution, sourceRecordKey, sourceRecordHash);
                    rawDealRepository.save(rawDeal);
                    updated++;
                } else {
                    RawDealEntity rawDeal = createNew(source, jobExecution, collected, sourceRecordKey, sourceRecordHash);
                    rawDealRepository.save(rawDeal);
                    inserted++;
                }
            } catch (Exception exception) {
                failed++;
                LOGGER.warn("failed to persist raw deal sourceCode={} sourceDealId={} message={}",
                        source.getCode(), collected.sourceDealId(), exception.getMessage(), exception);
            }
        }

        RawPersistenceResult result = new RawPersistenceResult(records.size(), inserted, updated, failed);
        LOGGER.info("raw persistence completed sourceCode={} totalReceived={} inserted={} updated={} failed={}",
                source.getCode(), result.totalReceived(), result.inserted(), result.updated(), result.failed());
        return result;
    }

    private Optional<RawDealEntity> findExisting(Long sourceId,
                                                 String sourceDealId,
                                                 String sourceRecordKey,
                                                 String sourceRecordHash) {
        if (StringUtils.hasText(sourceRecordKey)) {
            Optional<RawDealEntity> byRecordKey = rawDealRepository.findBySourceIdAndSourceRecordKey(sourceId, sourceRecordKey);
            if (byRecordKey.isPresent()) {
                return byRecordKey;
            }
        }
        if (StringUtils.hasText(sourceRecordHash)) {
            Optional<RawDealEntity> byRecordHash = rawDealRepository.findBySourceIdAndSourceRecordHash(sourceId, sourceRecordHash);
            if (byRecordHash.isPresent()) {
                return byRecordHash;
            }
        }
        if (StringUtils.hasText(sourceDealId)) {
            return rawDealRepository.findBySourceIdAndSourceDealId(sourceId, sourceDealId);
        }
        return Optional.empty();
    }

    private RawDealEntity createNew(SourceEntity source,
                                    CollectorJobExecutionEntity jobExecution,
                                    CollectedRawDeal collected,
                                    String sourceRecordKey,
                                    String sourceRecordHash) {
        RawDealEntity rawDeal = new RawDealEntity();
        rawDeal.setSource(source);
        rawDeal.setJobExecution(jobExecution);
        rawDeal.setSourceDealId(collected.sourceDealId());
        rawDeal.setSourceRecordKey(sourceRecordKey);
        rawDeal.setSourceRecordHash(sourceRecordHash);
        rawDeal.setPayload(collected.payload());
        rawDeal.setPayloadVersion(collected.payloadVersion());
        rawDeal.setStatus(RawDealStatus.NEW);
        rawDeal.setIngestedAt(now());
        rawDeal.setNormalizedAt(null);
        rawDeal.setParseError(null);
        return rawDeal;
    }

    private void updateExisting(RawDealEntity rawDeal,
                                CollectedRawDeal collected,
                                CollectorJobExecutionEntity jobExecution,
                                String sourceRecordKey,
                                String sourceRecordHash) {
        rawDeal.setJobExecution(jobExecution);
        rawDeal.setSourceDealId(collected.sourceDealId());
        rawDeal.setSourceRecordKey(sourceRecordKey);
        rawDeal.setSourceRecordHash(sourceRecordHash);
        rawDeal.setPayload(collected.payload());
        rawDeal.setPayloadVersion(collected.payloadVersion());
        rawDeal.setStatus(RawDealStatus.NEW);
        rawDeal.setIngestedAt(now());
        rawDeal.setNormalizedAt(null);
        rawDeal.setParseError(null);
    }

    private String resolveRecordKey(CollectedRawDeal collected) {
        if (StringUtils.hasText(collected.sourceRecordKey())) {
            return collected.sourceRecordKey();
        }
        return collected.sourceDealId();
    }

    private String resolveRecordHash(CollectedRawDeal collected) {
        if (StringUtils.hasText(collected.sourceRecordHash())) {
            return collected.sourceRecordHash();
        }
        try {
            String payloadJson = objectMapper.writeValueAsString(collected.payload());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payloadJson.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (JsonProcessingException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Cannot build sourceRecordHash", exception);
        }
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
