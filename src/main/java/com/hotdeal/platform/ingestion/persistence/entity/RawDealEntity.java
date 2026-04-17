package com.hotdeal.platform.ingestion.persistence.entity;

import com.hotdeal.platform.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "raw_deal")
public class RawDealEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private SourceEntity source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id")
    private CollectorJobExecutionEntity jobExecution;

    @Column(name = "source_deal_id", length = 255)
    private String sourceDealId;

    @Column(name = "source_record_key", length = 255)
    private String sourceRecordKey;

    @Column(name = "source_record_hash", length = 128)
    private String sourceRecordHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(name = "payload_version", length = 32)
    private String payloadVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RawDealStatus status;

    @Column(name = "ingested_at", nullable = false)
    private OffsetDateTime ingestedAt;

    @Column(name = "normalized_at")
    private OffsetDateTime normalizedAt;

    @Column(name = "parse_error")
    private String parseError;

    public Long getId() {
        return id;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    public CollectorJobExecutionEntity getJobExecution() {
        return jobExecution;
    }

    public void setJobExecution(CollectorJobExecutionEntity jobExecution) {
        this.jobExecution = jobExecution;
    }

    public String getSourceDealId() {
        return sourceDealId;
    }

    public void setSourceDealId(String sourceDealId) {
        this.sourceDealId = sourceDealId;
    }

    public String getSourceRecordKey() {
        return sourceRecordKey;
    }

    public void setSourceRecordKey(String sourceRecordKey) {
        this.sourceRecordKey = sourceRecordKey;
    }

    public String getSourceRecordHash() {
        return sourceRecordHash;
    }

    public void setSourceRecordHash(String sourceRecordHash) {
        this.sourceRecordHash = sourceRecordHash;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public String getPayloadVersion() {
        return payloadVersion;
    }

    public void setPayloadVersion(String payloadVersion) {
        this.payloadVersion = payloadVersion;
    }

    public RawDealStatus getStatus() {
        return status;
    }

    public void setStatus(RawDealStatus status) {
        this.status = status;
    }

    public OffsetDateTime getIngestedAt() {
        return ingestedAt;
    }

    public void setIngestedAt(OffsetDateTime ingestedAt) {
        this.ingestedAt = ingestedAt;
    }

    public OffsetDateTime getNormalizedAt() {
        return normalizedAt;
    }

    public void setNormalizedAt(OffsetDateTime normalizedAt) {
        this.normalizedAt = normalizedAt;
    }

    public String getParseError() {
        return parseError;
    }

    public void setParseError(String parseError) {
        this.parseError = parseError;
    }
}
