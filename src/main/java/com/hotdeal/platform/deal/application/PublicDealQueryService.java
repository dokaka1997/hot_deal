package com.hotdeal.platform.deal.application;

import com.hotdeal.platform.common.exception.InvalidInputException;
import com.hotdeal.platform.common.exception.ResourceNotFoundException;
import com.hotdeal.platform.common.pagination.PageResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealDetailResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealListItemResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealPriceHistoryRequest;
import com.hotdeal.platform.deal.api.dto.PublicDealPriceHistoryResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealSearchRequest;
import com.hotdeal.platform.deal.api.mapper.PublicDealMapper;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.PriceHistoryEntity;
import com.hotdeal.platform.deal.persistence.repository.DealRepository;
import com.hotdeal.platform.deal.persistence.repository.PriceHistoryRepository;
import com.hotdeal.platform.deal.persistence.spec.DealSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
public class PublicDealQueryService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "lastSeenAt", "lastSeenAt",
            "dealPrice", "dealPrice",
            "discountPercent", "discountPercent",
            "dealScore", "dealScore",
            "createdAt", "createdAt"
    );

    private static final Map<String, String> PRICE_HISTORY_SORT_FIELD_MAPPING = Map.of(
            "capturedAt", "capturedAt",
            "dealPrice", "dealPrice",
            "createdAt", "createdAt"
    );

    private final DealRepository dealRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PublicDealMapper publicDealMapper;
    private final Clock clock;

    public PublicDealQueryService(DealRepository dealRepository,
                                  PriceHistoryRepository priceHistoryRepository,
                                  PublicDealMapper publicDealMapper,
                                  Clock clock) {
        this.dealRepository = dealRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.publicDealMapper = publicDealMapper;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public PageResponse<PublicDealListItemResponse> search(PublicDealSearchRequest request) {
        Pageable pageable = buildPageable(request);
        Specification<DealEntity> specification = buildSpecification(request);

        Page<PublicDealListItemResponse> page = dealRepository.findAll(specification, pageable)
                .map(publicDealMapper::toListItemResponse);
        return PageResponse.from(page, request.getSortBy(), request.getDirection());
    }

    @Transactional(readOnly = true)
    public PublicDealDetailResponse getDetail(Long dealId) {
        DealEntity deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + dealId));
        return publicDealMapper.toDetailResponse(deal);
    }

    @Transactional(readOnly = true)
    public PublicDealPriceHistoryResponse getPriceHistory(Long dealId, PublicDealPriceHistoryRequest request) {
        dealRepository.findById(dealId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + dealId));

        Pageable pageable = buildPriceHistoryPageable(request);
        Page<PriceHistoryEntity> page = priceHistoryRepository.findByDealId(dealId, pageable);
        return publicDealMapper.toPriceHistoryResponse(dealId, page.getContent());
    }

    private Specification<DealEntity> buildSpecification(PublicDealSearchRequest request) {
        Specification<DealEntity> specification = null;

        specification = and(specification, DealSpecifications.keywordContains(request.getKeyword()));
        specification = and(specification, DealSpecifications.hasCategory(request.getCategory()));
        specification = and(specification, DealSpecifications.hasSourceCode(request.getSource()));
        specification = and(specification, DealSpecifications.priceGreaterThanOrEqual(request.getMinPrice()));
        specification = and(specification, DealSpecifications.priceLessThanOrEqual(request.getMaxPrice()));
        specification = and(specification, DealSpecifications.hasCoupon(request.getHasCoupon()));

        if (request.isActiveOnly()) {
            specification = and(specification, DealSpecifications.activeOnly(Instant.now()));
        }

        return specification;
    }

    private Specification<DealEntity> and(
            Specification<DealEntity> base,
            Specification<DealEntity> addition
    ) {
        if (base == null) {
            return addition;
        }
        if (addition == null) {
            return base;
        }
        return base.and(addition);
    }

    private Pageable buildPageable(PublicDealSearchRequest request) {
        String mappedField = SORT_FIELD_MAPPING.get(request.getSortBy());
        if (mappedField == null) {
            throw new InvalidInputException("Unsupported sortBy value: " + request.getSortBy());
        }
        Sort.Direction direction = switch (request.getDirection()) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
        };
        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, mappedField)
        );
    }

    private Pageable buildPriceHistoryPageable(PublicDealPriceHistoryRequest request) {
        String mappedField = PRICE_HISTORY_SORT_FIELD_MAPPING.get(request.getSortBy());
        if (mappedField == null) {
            throw new InvalidInputException("Unsupported sortBy value: " + request.getSortBy());
        }
        Sort.Direction direction = switch (request.getDirection()) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
        };
        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, mappedField)
        );
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
