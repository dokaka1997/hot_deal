package com.hotdeal.platform.admin.api;

import com.hotdeal.platform.admin.api.dto.AdminCollectorJobExecutionResponse;
import com.hotdeal.platform.admin.api.dto.AdminCollectorJobQueryRequest;
import com.hotdeal.platform.admin.api.dto.AdminFailedRawDealQueryRequest;
import com.hotdeal.platform.admin.api.dto.AdminFailedRawDealResponse;
import com.hotdeal.platform.admin.api.dto.AdminRawDealReprocessResponse;
import com.hotdeal.platform.admin.api.dto.AdminSourceHealthResponse;
import com.hotdeal.platform.admin.application.AdminMonitoringService;
import com.hotdeal.platform.common.api.ApiResponse;
import com.hotdeal.platform.common.api.ApiResponseFactory;
import com.hotdeal.platform.common.pagination.PageResponse;
import com.hotdeal.platform.common.web.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Monitoring", description = "Operational monitoring APIs for ingestion and normalization pipelines.")
@SecurityRequirement(name = "basicAuth")
public class AdminMonitoringController extends BaseApiController {

    private final AdminMonitoringService adminMonitoringService;

    public AdminMonitoringController(ApiResponseFactory apiResponseFactory,
                                     AdminMonitoringService adminMonitoringService) {
        super(apiResponseFactory);
        this.adminMonitoringService = adminMonitoringService;
    }

    @GetMapping("/collector-jobs")
    @Operation(summary = "Collector job history", description = "Returns paginated collector job execution history.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job history retrieved")
    public ResponseEntity<ApiResponse<PageResponse<AdminCollectorJobExecutionResponse>>> getCollectorJobHistory(
            HttpServletRequest request,
            @ParameterObject @Valid @ModelAttribute AdminCollectorJobQueryRequest query) {
        PageResponse<AdminCollectorJobExecutionResponse> data = adminMonitoringService.getCollectorJobHistory(query);
        return ok(request, data);
    }

    @GetMapping("/sources/health")
    @Operation(summary = "Source health status", description = "Returns operational health overview per source.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Source health retrieved")
    public ResponseEntity<ApiResponse<List<AdminSourceHealthResponse>>> getSourceHealthStatus(
            HttpServletRequest request) {
        List<AdminSourceHealthResponse> data = adminMonitoringService.getSourceHealthStatus();
        return ok(request, data);
    }

    @GetMapping("/raw-deals/failed")
    @Operation(summary = "Failed raw records", description = "Returns failed raw/normalization records for operations.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Failed records retrieved")
    public ResponseEntity<ApiResponse<PageResponse<AdminFailedRawDealResponse>>> getFailedRawDeals(
            HttpServletRequest request,
            @ParameterObject @Valid @ModelAttribute AdminFailedRawDealQueryRequest query) {
        PageResponse<AdminFailedRawDealResponse> data = adminMonitoringService.getFailedRawDeals(query);
        return ok(request, data);
    }

    @PostMapping("/raw-deals/{rawDealId}/reprocess")
    @Operation(summary = "Reprocess failed raw record", description = "Requeues and normalizes one failed raw record.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reprocess completed")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid reprocess request",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<ApiResponse<AdminRawDealReprocessResponse>> reprocessFailedRawDeal(
            HttpServletRequest request,
            @PathVariable @Positive(message = "{admin.query.id.positive}") Long rawDealId) {
        AdminRawDealReprocessResponse data = adminMonitoringService.reprocessFailedRawDeal(rawDealId);
        return ok(request, data);
    }
}
