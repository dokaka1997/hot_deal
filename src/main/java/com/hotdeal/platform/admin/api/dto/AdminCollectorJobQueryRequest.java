package com.hotdeal.platform.admin.api.dto;

import com.hotdeal.platform.common.pagination.PageQuery;
import com.hotdeal.platform.ingestion.persistence.entity.JobExecutionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "AdminCollectorJobQueryRequest", description = "Query options for collector job history.")
public class AdminCollectorJobQueryRequest extends PageQuery {

    @Schema(description = "Filter by source code.", example = "mock_deals")
    @Size(max = 64, message = "{admin.query.source.max}")
    private String sourceCode;

    @Schema(description = "Filter by job status.", example = "FAILED")
    private JobExecutionStatus status;

    public AdminCollectorJobQueryRequest() {
        setSortBy("startedAt");
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public JobExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(JobExecutionStatus status) {
        this.status = status;
    }
}
