package com.hotdeal.platform.alert.api.dto;

import com.hotdeal.platform.alert.persistence.entity.AlertRuleStatus;
import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

@Schema(name = "AlertRuleResponse", description = "Alert rule API response.")
public record AlertRuleResponse(
        Long id,
        String subscriberKey,
        String name,
        String keyword,
        String category,
        String sourceCode,
        BigDecimal maxPrice,
        BigDecimal minDiscountPercent,
        NotificationChannel notificationChannel,
        String notificationTarget,
        AlertRuleStatus status,
        Instant lastTriggeredAt,
        Instant createdAt,
        Instant updatedAt
) {
}
