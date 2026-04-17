package com.hotdeal.platform.alert.persistence.entity;

import com.hotdeal.platform.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "alert_rule")
public class AlertRuleEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_key", nullable = false, length = 128)
    private String subscriberKey;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "keyword", length = 255)
    private String keyword;

    @Column(name = "category", length = 120)
    private String category;

    @Column(name = "source_code", length = 64)
    private String sourceCode;

    @Column(name = "max_price", precision = 18, scale = 2)
    private BigDecimal maxPrice;

    @Column(name = "min_discount_percent", precision = 5, scale = 2)
    private BigDecimal minDiscountPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_channel", nullable = false, length = 32)
    private NotificationChannel notificationChannel;

    @Column(name = "notification_target", length = 255)
    private String notificationTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AlertRuleStatus status;

    @Column(name = "last_triggered_at")
    private Instant lastTriggeredAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    public Long getId() {
        return id;
    }

    public String getSubscriberKey() {
        return subscriberKey;
    }

    public void setSubscriberKey(String subscriberKey) {
        this.subscriberKey = subscriberKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinDiscountPercent() {
        return minDiscountPercent;
    }

    public void setMinDiscountPercent(BigDecimal minDiscountPercent) {
        this.minDiscountPercent = minDiscountPercent;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public String getNotificationTarget() {
        return notificationTarget;
    }

    public void setNotificationTarget(String notificationTarget) {
        this.notificationTarget = notificationTarget;
    }

    public AlertRuleStatus getStatus() {
        return status;
    }

    public void setStatus(AlertRuleStatus status) {
        this.status = status;
    }

    public Instant getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public void setLastTriggeredAt(Instant lastTriggeredAt) {
        this.lastTriggeredAt = lastTriggeredAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
