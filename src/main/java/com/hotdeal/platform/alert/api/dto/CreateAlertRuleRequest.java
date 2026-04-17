package com.hotdeal.platform.alert.api.dto;

import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "CreateAlertRuleRequest", description = "Payload for creating a new alert rule.")
public class CreateAlertRuleRequest {

    @NotBlank(message = "{alert.rule.subscriber.required}")
    @Size(max = 128, message = "{alert.rule.subscriber.max}")
    @Schema(description = "Subscriber key or user reference for grouping alert rules.", example = "guest-demo")
    private String subscriberKey;

    @Size(max = 255, message = "{alert.rule.name.max}")
    @Schema(description = "Human-readable rule name.", example = "iPhone deals below 900")
    private String name;

    @Size(max = 255, message = "{alert.rule.keyword.max}")
    @Schema(description = "Keyword condition matched against deal text.", example = "iphone")
    private String keyword;

    @Size(max = 120, message = "{alert.rule.category.max}")
    @Schema(description = "Category condition.", example = "electronics")
    private String category;

    @Size(max = 64, message = "{alert.rule.source.max}")
    @Schema(description = "Source code condition.", example = "mock_deals")
    private String sourceCode;

    @DecimalMin(value = "0.0", message = "{alert.rule.maxPrice.min}")
    @Schema(description = "Maximum deal price condition.", example = "900.00")
    private BigDecimal maxPrice;

    @DecimalMin(value = "0.0", message = "{alert.rule.minDiscount.min}")
    @DecimalMax(value = "100.0", message = "{alert.rule.minDiscount.max}")
    @Schema(description = "Minimum discount percentage condition.", example = "15.0")
    private BigDecimal minDiscountPercent;

    @Schema(description = "Notification channel.", example = "INTERNAL_LOG")
    private NotificationChannel notificationChannel = NotificationChannel.INTERNAL_LOG;

    @Size(max = 255, message = "{alert.rule.target.max}")
    @Schema(description = "Notification target (email/topic) for future channels.", example = "demo@example.com")
    private String notificationTarget;

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

    @AssertTrue(message = "{alert.rule.condition.required}")
    public boolean hasAtLeastOneCondition() {
        return hasText(keyword)
                || hasText(category)
                || hasText(sourceCode)
                || maxPrice != null
                || minDiscountPercent != null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
