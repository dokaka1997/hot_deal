package com.hotdeal.platform.alert.api;

import com.hotdeal.platform.alert.api.dto.AlertRuleListQueryRequest;
import com.hotdeal.platform.alert.api.dto.AlertRuleOperationResponse;
import com.hotdeal.platform.alert.api.dto.AlertRuleResponse;
import com.hotdeal.platform.alert.api.dto.CreateAlertRuleRequest;
import com.hotdeal.platform.alert.application.AlertRuleService;
import com.hotdeal.platform.common.api.ApiResponse;
import com.hotdeal.platform.common.api.ApiResponseFactory;
import com.hotdeal.platform.common.pagination.PageResponse;
import com.hotdeal.platform.common.web.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/alerts/rules")
@Tag(name = "Alert Rules", description = "MVP alert rule management APIs.")
public class AlertRuleController extends BaseApiController {

    private final AlertRuleService alertRuleService;
    private final ApiResponseFactory apiResponseFactory;

    public AlertRuleController(ApiResponseFactory apiResponseFactory,
                               AlertRuleService alertRuleService) {
        super(apiResponseFactory);
        this.apiResponseFactory = apiResponseFactory;
        this.alertRuleService = alertRuleService;
    }

    @PostMapping
    @Operation(summary = "Create alert rule", description = "Creates a new alert rule with MVP matching conditions.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Alert rule created")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<ApiResponse<AlertRuleResponse>> createAlertRule(HttpServletRequest request,
                                                                           @Valid @RequestBody CreateAlertRuleRequest payload) {
        AlertRuleResponse data = alertRuleService.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseFactory.success(request, data));
    }

    @GetMapping
    @Operation(summary = "List alert rules", description = "Lists alert rules with pagination and optional subscriber filter.")
    public ResponseEntity<ApiResponse<PageResponse<AlertRuleResponse>>> listAlertRules(
            HttpServletRequest request,
            @ParameterObject @Valid @ModelAttribute AlertRuleListQueryRequest query) {
        PageResponse<AlertRuleResponse> data = alertRuleService.list(query);
        return ok(request, data);
    }

    @PostMapping("/{alertRuleId}/disable")
    @Operation(summary = "Disable alert rule", description = "Disables an alert rule without deleting it.")
    public ResponseEntity<ApiResponse<AlertRuleResponse>> disableAlertRule(
            HttpServletRequest request,
            @PathVariable @Positive(message = "{alert.rule.id.positive}") Long alertRuleId) {
        AlertRuleResponse data = alertRuleService.disable(alertRuleId);
        return ok(request, data);
    }

    @DeleteMapping("/{alertRuleId}")
    @Operation(summary = "Delete alert rule", description = "Deletes an alert rule and its delivery history.")
    public ResponseEntity<ApiResponse<AlertRuleOperationResponse>> deleteAlertRule(
            HttpServletRequest request,
            @PathVariable @Positive(message = "{alert.rule.id.positive}") Long alertRuleId) {
        alertRuleService.delete(alertRuleId);
        AlertRuleOperationResponse data = new AlertRuleOperationResponse(alertRuleId, "DELETE", "deleted");
        return ok(request, data);
    }
}
