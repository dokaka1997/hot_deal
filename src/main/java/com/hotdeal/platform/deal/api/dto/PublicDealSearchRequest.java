package com.hotdeal.platform.deal.api.dto;

import com.hotdeal.platform.common.pagination.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "PublicDealSearchRequest", description = "Search and filter options for public deals API.")
public class PublicDealSearchRequest extends PageQuery {

    @Schema(description = "Keyword to search in title, brand, and category.", example = "iphone")
    @Size(max = 120, message = "{deal.query.keyword.max}")
    private String keyword;

    @Schema(description = "Exact category filter.", example = "electronics")
    @Size(max = 120, message = "{deal.query.category.max}")
    private String category;

    @Schema(description = "Source code filter.", example = "mock_deals")
    @Size(max = 64, message = "{deal.query.source.max}")
    private String source;

    @Schema(description = "Minimum deal price inclusive.", example = "100.00")
    @DecimalMin(value = "0.0", message = "{deal.query.price.min}")
    private BigDecimal minPrice;

    @Schema(description = "Maximum deal price inclusive.", example = "1000.00")
    @DecimalMin(value = "0.0", message = "{deal.query.price.max}")
    private BigDecimal maxPrice;

    @Schema(description = "Return only active deals.", defaultValue = "true")
    private boolean activeOnly = true;

    @Schema(description = "Coupon filter. true = only deals with coupon code, false = only deals without coupon, null = all.")
    private Boolean hasCoupon;

    public PublicDealSearchRequest() {
        setSortBy("lastSeenAt");
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean isActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public Boolean getHasCoupon() {
        return hasCoupon;
    }

    public void setHasCoupon(Boolean hasCoupon) {
        this.hasCoupon = hasCoupon;
    }

    @AssertTrue(message = "{deal.query.price.range.invalid}")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return maxPrice.compareTo(minPrice) >= 0;
    }
}
