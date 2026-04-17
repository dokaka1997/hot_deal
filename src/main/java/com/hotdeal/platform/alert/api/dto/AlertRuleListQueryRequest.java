package com.hotdeal.platform.alert.api.dto;

import com.hotdeal.platform.common.pagination.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "AlertRuleListQueryRequest", description = "Query params for listing alert rules.")
public class AlertRuleListQueryRequest extends PageQuery {

    @Size(max = 128, message = "{alert.rule.subscriber.max}")
    @Schema(description = "Optional subscriber filter.", example = "guest-demo")
    private String subscriberKey;

    public AlertRuleListQueryRequest() {
        setSortBy("createdAt");
    }

    public String getSubscriberKey() {
        return subscriberKey;
    }

    public void setSubscriberKey(String subscriberKey) {
        this.subscriberKey = subscriberKey;
    }
}
