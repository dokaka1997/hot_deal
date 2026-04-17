package com.hotdeal.platform.admin.api.dto;

import com.hotdeal.platform.common.pagination.PageQuery;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "AdminFailedRawDealQueryRequest", description = "Query options for failed raw deals.")
public class AdminFailedRawDealQueryRequest extends PageQuery {

    @Schema(description = "Optional source code filter.", example = "mock_deals")
    @Size(max = 64, message = "{admin.query.source.max}")
    private String sourceCode;

    @Schema(description = "Optional failed status filter (ERROR or REJECTED).", example = "ERROR")
    private RawDealStatus status;

    public AdminFailedRawDealQueryRequest() {
        setSortBy("ingestedAt");
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public RawDealStatus getStatus() {
        return status;
    }

    public void setStatus(RawDealStatus status) {
        this.status = status;
    }
}
