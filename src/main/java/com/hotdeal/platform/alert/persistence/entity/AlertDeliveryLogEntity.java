package com.hotdeal.platform.alert.persistence.entity;

import com.hotdeal.platform.common.persistence.AuditableEntity;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
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
@Table(name = "alert_delivery_log")
public class AlertDeliveryLogEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alert_rule_id", nullable = false)
    private AlertRuleEntity alertRule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deal_id", nullable = false)
    private DealEntity deal;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_channel", nullable = false, length = 32)
    private NotificationChannel notificationChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 32)
    private AlertDeliveryStatus deliveryStatus;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Column(name = "error_message")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Map<String, Object> payload;

    public Long getId() {
        return id;
    }

    public AlertRuleEntity getAlertRule() {
        return alertRule;
    }

    public void setAlertRule(AlertRuleEntity alertRule) {
        this.alertRule = alertRule;
    }

    public DealEntity getDeal() {
        return deal;
    }

    public void setDeal(DealEntity deal) {
        this.deal = deal;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public AlertDeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(AlertDeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public OffsetDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
