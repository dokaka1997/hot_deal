package com.hotdeal.platform.deal.api;

import com.hotdeal.platform.common.api.ApiResponse;
import com.hotdeal.platform.common.api.ApiResponseFactory;
import com.hotdeal.platform.common.pagination.PageResponse;
import com.hotdeal.platform.common.web.BaseApiController;
import com.hotdeal.platform.deal.api.dto.PublicDealDetailResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealListItemResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealSearchRequest;
import com.hotdeal.platform.deal.application.PublicDealQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/deals")
@Tag(name = "Public Deals", description = "Public read APIs for browsing and searching deals.")
public class PublicDealController extends BaseApiController {

    private final PublicDealQueryService publicDealQueryService;

    public PublicDealController(ApiResponseFactory apiResponseFactory,
                                PublicDealQueryService publicDealQueryService) {
        super(apiResponseFactory);
        this.publicDealQueryService = publicDealQueryService;
    }

    @GetMapping
    @Operation(
            summary = "List deals",
            description = """
                    Returns paginated deals for frontend consumption.
                    Supported sortBy values: lastSeenAt, dealPrice, discountPercent, dealScore, createdAt.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deals listed successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid query parameters",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<ApiResponse<PageResponse<PublicDealListItemResponse>>> listDeals(
            HttpServletRequest httpServletRequest,
            @ParameterObject @Valid @ModelAttribute PublicDealSearchRequest request) {
        PageResponse<PublicDealListItemResponse> data = publicDealQueryService.search(request);
        return ok(httpServletRequest, data);
    }

    @GetMapping("/{dealId}")
    @Operation(summary = "Get deal detail", description = "Returns full detail for a single deal by ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deal detail retrieved")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Deal not found",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<ApiResponse<PublicDealDetailResponse>> getDealDetail(
            HttpServletRequest httpServletRequest,
            @Parameter(description = "Deal identifier", example = "9021")
            @PathVariable @Positive(message = "{deal.query.id.positive}") Long dealId) {
        PublicDealDetailResponse data = publicDealQueryService.getDetail(dealId);
        return ok(httpServletRequest, data);
    }
}
