package com.hotdeal.platform.alert.notification.model;

import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;

public record AlertNotificationRequest(
        AlertRuleEntity alertRule,
        DealEntity deal,
        String trigger
) {
}
