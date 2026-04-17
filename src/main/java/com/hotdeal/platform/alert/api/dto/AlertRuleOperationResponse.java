package com.hotdeal.platform.alert.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AlertRuleOperationResponse", description = "Response for alert rule state-changing operations.")
public record AlertRuleOperationResponse(
        Long alertRuleId,
        String operation,
        String message
) {
}
