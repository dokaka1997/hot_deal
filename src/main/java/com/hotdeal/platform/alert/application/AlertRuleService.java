package com.hotdeal.platform.alert.application;

import com.hotdeal.platform.alert.api.dto.AlertRuleListQueryRequest;
import com.hotdeal.platform.alert.api.dto.CreateAlertRuleRequest;
import com.hotdeal.platform.alert.api.mapper.AlertRuleMapper;
import com.hotdeal.platform.alert.config.AlertProperties;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleStatus;
import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;
import com.hotdeal.platform.alert.persistence.repository.AlertRuleRepository;
import com.hotdeal.platform.common.exception.InvalidInputException;
import com.hotdeal.platform.common.exception.ResourceNotFoundException;
import com.hotdeal.platform.common.pagination.PageResponse;
import com.hotdeal.platform.alert.api.dto.AlertRuleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AlertRuleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertRuleService.class);

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "createdAt", "createdAt",
            "updatedAt", "updatedAt",
            "lastTriggeredAt", "lastTriggeredAt",
            "status", "status"
    );

    private final AlertRuleRepository alertRuleRepository;
    private final AlertRuleMapper alertRuleMapper;
    private final AlertMatchingService alertMatchingService;
    private final AlertProperties alertProperties;

    public AlertRuleService(AlertRuleRepository alertRuleRepository,
                            AlertRuleMapper alertRuleMapper,
                            AlertMatchingService alertMatchingService,
                            AlertProperties alertProperties) {
        this.alertRuleRepository = alertRuleRepository;
        this.alertRuleMapper = alertRuleMapper;
        this.alertMatchingService = alertMatchingService;
        this.alertProperties = alertProperties;
    }

    @Transactional
    public AlertRuleResponse create(CreateAlertRuleRequest request) {
        AlertRuleEntity entity = new AlertRuleEntity();
        entity.setSubscriberKey(request.getSubscriberKey().trim());
        entity.setName(trimToNull(request.getName()));
        entity.setKeyword(trimToNull(request.getKeyword()));
        entity.setCategory(trimToNull(request.getCategory()));
        entity.setSourceCode(trimToNull(request.getSourceCode()));
        entity.setMaxPrice(request.getMaxPrice());
        entity.setMinDiscountPercent(request.getMinDiscountPercent());
        entity.setNotificationChannel(request.getNotificationChannel() == null
                ? NotificationChannel.INTERNAL_LOG
                : request.getNotificationChannel());
        entity.setNotificationTarget(trimToNull(request.getNotificationTarget()));
        entity.setStatus(AlertRuleStatus.ACTIVE);
        entity.setMetadata(buildMetadata());

        AlertRuleEntity saved = alertRuleRepository.save(entity);
        int bootstrapMatches = alertMatchingService.matchRuleAgainstRecentDeals(
                saved,
                alertProperties.getMatching().getBootstrapScanSize()
        );
        LOGGER.info("alert rule created ruleId={} subscriberKey={} bootstrapMatches={}",
                saved.getId(), saved.getSubscriberKey(), bootstrapMatches);
        return alertRuleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<AlertRuleResponse> list(AlertRuleListQueryRequest request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getDirection().name());
        Page<AlertRuleResponse> page;
        if (StringUtils.hasText(request.getSubscriberKey())) {
            page = alertRuleRepository.findBySubscriberKey(request.getSubscriberKey().trim(), pageable)
                    .map(alertRuleMapper::toResponse);
        } else {
            page = alertRuleRepository.findAll(pageable)
                    .map(alertRuleMapper::toResponse);
        }
        return PageResponse.from(page, request.getSortBy(), request.getDirection());
    }

    @Transactional
    public AlertRuleResponse disable(Long alertRuleId) {
        AlertRuleEntity entity = alertRuleRepository.findById(alertRuleId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found with id: " + alertRuleId));
        entity.setStatus(AlertRuleStatus.DISABLED);
        AlertRuleEntity saved = alertRuleRepository.save(entity);
        LOGGER.info("alert rule disabled ruleId={} subscriberKey={}", saved.getId(), saved.getSubscriberKey());
        return alertRuleMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long alertRuleId) {
        AlertRuleEntity entity = alertRuleRepository.findById(alertRuleId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found with id: " + alertRuleId));
        alertRuleRepository.delete(entity);
        LOGGER.info("alert rule deleted ruleId={} subscriberKey={}", entity.getId(), entity.getSubscriberKey());
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        String mappedField = SORT_FIELD_MAPPING.get(sortBy);
        if (mappedField == null) {
            throw new InvalidInputException("Unsupported sortBy value: " + sortBy);
        }
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, mappedField));
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Map<String, Object> buildMetadata() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("version", "mvp-v1");
        return metadata;
    }
}
