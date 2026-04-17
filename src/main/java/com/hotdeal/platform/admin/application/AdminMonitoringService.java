package com.hotdeal.platform.admin.application;

import com.hotdeal.platform.admin.api.dto.AdminCollectorJobExecutionResponse;
import com.hotdeal.platform.admin.api.dto.AdminCollectorJobQueryRequest;
import com.hotdeal.platform.admin.api.dto.AdminFailedRawDealQueryRequest;
import com.hotdeal.platform.admin.api.dto.AdminFailedRawDealResponse;
import com.hotdeal.platform.admin.api.dto.AdminRawDealReprocessResponse;
import com.hotdeal.platform.admin.api.dto.AdminSourceHealthResponse;
import com.hotdeal.platform.admin.api.mapper.AdminMonitoringMapper;
import com.hotdeal.platform.common.exception.InvalidInputException;
import com.hotdeal.platform.common.exception.ResourceNotFoundException;
import com.hotdeal.platform.common.pagination.PageResponse;
import com.hotdeal.platform.ingestion.normalization.DealNormalizationService;
import com.hotdeal.platform.ingestion.normalization.NormalizationSingleResult;
import com.hotdeal.platform.ingestion.persistence.entity.CollectorJobExecutionEntity;
import com.hotdeal.platform.ingestion.persistence.entity.JobExecutionStatus;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.repository.CollectorJobExecutionRepository;
import com.hotdeal.platform.ingestion.persistence.repository.RawDealRepository;
import com.hotdeal.platform.ingestion.persistence.repository.SourceRepository;
import com.hotdeal.platform.ingestion.persistence.spec.RawDealSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminMonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminMonitoringService.class);

    private static final Set<RawDealStatus> FAILED_RAW_STATUSES = EnumSet.of(RawDealStatus.ERROR, RawDealStatus.REJECTED);

    private static final Map<String, String> JOB_SORT_FIELD_MAPPING = Map.of(
            "startedAt", "startedAt",
            "finishedAt", "finishedAt",
            "durationMs", "durationMs",
            "status", "status",
            "totalFailed", "totalFailed"
    );

    private static final Map<String, String> RAW_FAILED_SORT_FIELD_MAPPING = Map.of(
            "ingestedAt", "ingestedAt",
            "normalizedAt", "normalizedAt",
            "status", "status",
            "createdAt", "createdAt"
    );

    private final CollectorJobExecutionRepository collectorJobExecutionRepository;
    private final SourceRepository sourceRepository;
    private final RawDealRepository rawDealRepository;
    private final DealNormalizationService dealNormalizationService;
    private final AdminMonitoringMapper adminMonitoringMapper;
    private final Clock clock;

    public AdminMonitoringService(CollectorJobExecutionRepository collectorJobExecutionRepository,
                                  SourceRepository sourceRepository,
                                  RawDealRepository rawDealRepository,
                                  DealNormalizationService dealNormalizationService,
                                  AdminMonitoringMapper adminMonitoringMapper,
                                  Clock clock) {
        this.collectorJobExecutionRepository = collectorJobExecutionRepository;
        this.sourceRepository = sourceRepository;
        this.rawDealRepository = rawDealRepository;
        this.dealNormalizationService = dealNormalizationService;
        this.adminMonitoringMapper = adminMonitoringMapper;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCollectorJobExecutionResponse> getCollectorJobHistory(AdminCollectorJobQueryRequest request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getDirection().name(), JOB_SORT_FIELD_MAPPING);
        Optional<Long> sourceId = resolveSourceId(request.getSourceCode());
        Page<CollectorJobExecutionEntity> page;

        if (sourceId.isPresent() && request.getStatus() != null) {
            page = collectorJobExecutionRepository.findBySourceIdAndStatusOrderByStartedAtDesc(
                    sourceId.get(),
                    request.getStatus(),
                    pageable
            );
        } else if (sourceId.isPresent()) {
            page = collectorJobExecutionRepository.findBySourceIdOrderByStartedAtDesc(sourceId.get(), pageable);
        } else if (request.getStatus() != null) {
            page = collectorJobExecutionRepository.findByStatusOrderByStartedAtDesc(request.getStatus(), pageable);
        } else {
            page = collectorJobExecutionRepository.findAllByOrderByStartedAtDesc(pageable);
        }

        Page<AdminCollectorJobExecutionResponse> mapped = page.map(adminMonitoringMapper::toCollectorJobResponse);
        return PageResponse.from(mapped, request.getSortBy(), request.getDirection());
    }

    @Transactional(readOnly = true)
    public List<AdminSourceHealthResponse> getSourceHealthStatus() {
        OffsetDateTime since = now().minusHours(24);
        return sourceRepository.findAllByOrderByCodeAsc()
                .stream()
                .map(source -> {
                    CollectorJobExecutionEntity lastJob = collectorJobExecutionRepository
                            .findTopBySourceIdOrderByStartedAtDesc(source.getId())
                            .orElse(null);
                    long jobsLast24h = collectorJobExecutionRepository.countBySourceIdAndStartedAtGreaterThanEqual(source.getId(), since);
                    long failuresLast24h = collectorJobExecutionRepository.countBySourceIdAndStatusAndStartedAtGreaterThanEqual(
                            source.getId(), JobExecutionStatus.FAILED, since);
                    return adminMonitoringMapper.toSourceHealthResponse(source, lastJob, jobsLast24h, failuresLast24h);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminFailedRawDealResponse> getFailedRawDeals(AdminFailedRawDealQueryRequest request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getDirection().name(), RAW_FAILED_SORT_FIELD_MAPPING);

        Set<RawDealStatus> statuses = FAILED_RAW_STATUSES;
        if (request.getStatus() != null) {
            if (!FAILED_RAW_STATUSES.contains(request.getStatus())) {
                throw new InvalidInputException("Unsupported failed raw status filter: " + request.getStatus());
            }
            statuses = EnumSet.of(request.getStatus());
        }

        Specification<RawDealEntity> specification = Specification.where(RawDealSpecifications.hasStatuses(statuses))
                .and(RawDealSpecifications.hasSourceCode(request.getSourceCode()));
        Page<AdminFailedRawDealResponse> page = rawDealRepository.findAll(specification, pageable)
                .map(adminMonitoringMapper::toFailedRawDealResponse);
        return PageResponse.from(page, request.getSortBy(), request.getDirection());
    }

    @Transactional
    public AdminRawDealReprocessResponse reprocessFailedRawDeal(Long rawDealId) {
        String actor = resolveActor();

        RawDealEntity rawDeal = rawDealRepository.findById(rawDealId)
                .orElseThrow(() -> new ResourceNotFoundException("Raw deal not found with id: " + rawDealId));

        if (!FAILED_RAW_STATUSES.contains(rawDeal.getStatus())) {
            throw new InvalidInputException("Raw deal is not in a reprocessable failed state: " + rawDeal.getStatus());
        }

        LOGGER.info("admin action=reprocess_raw_deal actor={} rawDealId={} sourceCode={} previousStatus={}",
                actor, rawDealId, rawDeal.getSource().getCode(), rawDeal.getStatus());

        rawDeal.setStatus(RawDealStatus.NEW);
        rawDeal.setParseError(null);
        rawDeal.setNormalizedAt(null);
        rawDealRepository.save(rawDeal);

        NormalizationSingleResult result = dealNormalizationService.normalizeRawDeal(rawDealId);

        LOGGER.info("admin action=reprocess_raw_deal_completed actor={} rawDealId={} newStatus={} message={}",
                actor, result.rawDealId(), result.status(), result.message());

        return new AdminRawDealReprocessResponse(result.rawDealId(), result.status(), result.message());
    }

    private Optional<Long> resolveSourceId(String sourceCode) {
        if (!StringUtils.hasText(sourceCode)) {
            return Optional.empty();
        }
        SourceEntity source = sourceRepository.findByCode(sourceCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Source not found with code: " + sourceCode));
        return Optional.of(source.getId());
    }

    private Pageable buildPageable(int page,
                                   int size,
                                   String sortBy,
                                   String direction,
                                   Map<String, String> supportedSortFields) {
        String mappedField = supportedSortFields.get(sortBy);
        if (mappedField == null) {
            throw new InvalidInputException("Unsupported sortBy value: " + sortBy);
        }
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, mappedField));
    }

    private String resolveActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            return "unknown";
        }
        return authentication.getName();
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
