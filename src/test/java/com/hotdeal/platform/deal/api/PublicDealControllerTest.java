package com.hotdeal.platform.deal.api;

import com.hotdeal.platform.common.api.ApiResponseFactory;
import com.hotdeal.platform.common.exception.ResourceNotFoundException;
import com.hotdeal.platform.common.pagination.PageResponse;
import com.hotdeal.platform.common.pagination.SortDirection;
import com.hotdeal.platform.deal.api.dto.PublicDealDetailResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealListItemResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealProductResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealSourceResponse;
import com.hotdeal.platform.deal.application.PublicDealQueryService;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicDealController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiResponseFactory.class)
class PublicDealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicDealQueryService publicDealQueryService;

    @Test
    void listDeals_shouldReturnPageResponse() throws Exception {
        PublicDealListItemResponse item = new PublicDealListItemResponse(
                101L,
                "Apple iPhone 15 Pro",
                "apple iphone 15 pro",
                "Flagship deal",
                "Apple",
                "electronics",
                "https://cdn.example.com/iphone.jpg",
                "https://shop.example.com/deals/101",
                "SAVE15",
                "USD",
                new BigDecimal("1199.00"),
                new BigDecimal("999.00"),
                new BigDecimal("16.68"),
                new BigDecimal("92.50"),
                DealStatus.ACTIVE,
                OffsetDateTime.parse("2026-04-01T00:00:00Z"),
                OffsetDateTime.parse("2026-04-30T23:59:00Z"),
                OffsetDateTime.parse("2026-03-30T09:00:00Z"),
                OffsetDateTime.parse("2026-04-15T10:00:00Z"),
                new PublicDealSourceResponse("mock_deals", "Mock Deals", "https://mock.example"),
                new PublicDealProductResponse(1L, "Apple iPhone 15 Pro", "Apple", "electronics")
        );
        PageResponse<PublicDealListItemResponse> pageResponse = new PageResponse<>(
                List.of(item),
                0,
                1,
                1,
                1L,
                1,
                true,
                true,
                "lastSeenAt",
                SortDirection.DESC
        );

        Mockito.when(publicDealQueryService.search(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/deals")
                        .queryParam("keyword", "iphone")
                        .queryParam("page", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(101))
                .andExpect(jsonPath("$.data.items[0].title").value("Apple iPhone 15 Pro"))
                .andExpect(jsonPath("$.data.items[0].source.code").value("mock_deals"));
    }

    @Test
    void listDeals_shouldReturnBadRequestWhenPriceRangeIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/deals")
                        .queryParam("minPrice", "500")
                        .queryParam("maxPrice", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(publicDealQueryService);
    }

    @Test
    void getDealDetail_shouldReturnNotFoundWhenDealDoesNotExist() throws Exception {
        Mockito.when(publicDealQueryService.getDetail(999L))
                .thenThrow(new ResourceNotFoundException("Deal not found with id: 999"));

        mockMvc.perform(get("/api/v1/deals/{dealId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void getDealDetail_shouldReturnDealDetail() throws Exception {
        PublicDealDetailResponse detail = new PublicDealDetailResponse(
                201L,
                "Sony WH-1000XM5",
                "sony wh1000xm5",
                "Wireless noise-cancelling headphones",
                "Sony",
                "audio",
                "https://cdn.example.com/sony.jpg",
                "https://shop.example.com/deals/201",
                null,
                "USD",
                new BigDecimal("399.00"),
                new BigDecimal("299.00"),
                new BigDecimal("25.06"),
                new BigDecimal("88.00"),
                DealStatus.ACTIVE,
                OffsetDateTime.parse("2026-04-10T00:00:00Z"),
                OffsetDateTime.parse("2026-04-20T23:59:00Z"),
                OffsetDateTime.parse("2026-04-10T00:00:00Z"),
                OffsetDateTime.parse("2026-04-15T08:00:00Z"),
                new PublicDealSourceResponse("mock_deals", "Mock Deals", "https://mock.example"),
                new PublicDealProductResponse(501L, "Sony WH-1000XM5", "Sony", "audio")
        );
        Mockito.when(publicDealQueryService.getDetail(201L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/deals/{dealId}", 201))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(201))
                .andExpect(jsonPath("$.data.product.id").value(501));
    }
}
