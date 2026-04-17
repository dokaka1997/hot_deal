package com.hotdeal.platform.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        int totalPages,
        long totalElements,
        int numberOfElements,
        boolean first,
        boolean last,
        String sortBy,
        SortDirection direction
) {
    public static <T> PageResponse<T> from(Page<T> pageData, String sortBy, SortDirection direction) {
        return new PageResponse<>(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalPages(),
                pageData.getTotalElements(),
                pageData.getNumberOfElements(),
                pageData.isFirst(),
                pageData.isLast(),
                sortBy,
                direction
        );
    }
}
