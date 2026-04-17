package com.hotdeal.platform.common.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageQuery {

    @Min(value = 0, message = "{pagination.page.min}")
    private int page = 0;

    @Min(value = 1, message = "{pagination.size.min}")
    @Max(value = 100, message = "{pagination.size.max}")
    private int size = 20;

    private String sortBy = "createdAt";

    private SortDirection direction = SortDirection.DESC;

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(toSpringDirection(), sortBy));
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        if (sortBy != null && !sortBy.isBlank()) {
            this.sortBy = sortBy;
        }
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        if (direction != null) {
            this.direction = direction;
        }
    }

    private Sort.Direction toSpringDirection() {
        return direction == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}
