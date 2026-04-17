package com.hotdeal.platform.admin.api;

import com.hotdeal.platform.analytics.api.dto.AnalyticsSummaryQueryRequest;
import com.hotdeal.platform.analytics.api.dto.AnalyticsSummaryResponse;
import com.hotdeal.platform.analytics.application.AnalyticsSummaryService;
import com.hotdeal.platform.common.api.ApiResponse;
import com.hotdeal.platform.common.api.ApiResponseFactory;
import com.hotdeal.platform.common.web.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/admin/analytics")
@Tag(name = "Admin Analytics", description = "Operational analytics APIs for internal dashboards.")
@SecurityRequirement(name = "basicAuth")
public class AdminAnalyticsController extends BaseApiController {

    private final AnalyticsSummaryService analyticsSummaryService;

    public AdminAnalyticsController(ApiResponseFactory apiResponseFactory,
                                    AnalyticsSummaryService analyticsSummaryService) {
        super(apiResponseFactory);
        this.analyticsSummaryService = analyticsSummaryService;
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get analytics summary (admin)",
            description = "Returns analytics summary for internal monitoring dashboards."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics summary retrieved")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid query parameters",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getSummary(
            HttpServletRequest request,
            @ParameterObject @Valid @ModelAttribute AnalyticsSummaryQueryRequest query) {
        AnalyticsSummaryResponse data = analyticsSummaryService.getSummary(query);
        return ok(request, data);
    }
}
