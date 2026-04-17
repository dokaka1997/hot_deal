package com.hotdeal.platform.alert.api.mapper;

import com.hotdeal.platform.alert.api.dto.AlertRuleResponse;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import org.springframework.stereotype.Component;

@Component
public class AlertRuleMapper {

    public AlertRuleResponse toResponse(AlertRuleEntity entity) {
        return new AlertRuleResponse(
                entity.getId(),
                entity.getSubscriberKey(),
                entity.getName(),
                entity.getKeyword(),
                entity.getCategory(),
                entity.getSourceCode(),
                entity.getMaxPrice(),
                entity.getMinDiscountPercent(),
                entity.getNotificationChannel(),
                entity.getNotificationTarget(),
                entity.getStatus(),
                entity.getLastTriggeredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
