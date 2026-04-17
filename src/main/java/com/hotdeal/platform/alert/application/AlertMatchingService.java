package com.hotdeal.platform.alert.application;

import com.hotdeal.platform.alert.matching.AlertRuleMatcher;
import com.hotdeal.platform.alert.notification.AlertNotificationDispatcher;
import com.hotdeal.platform.alert.notification.model.AlertNotificationRequest;
import com.hotdeal.platform.alert.notification.model.AlertNotificationResult;
import com.hotdeal.platform.alert.persistence.entity.AlertDeliveryLogEntity;
import com.hotdeal.platform.alert.persistence.entity.AlertDeliveryStatus;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleStatus;
import com.hotdeal.platform.alert.persistence.repository.AlertDeliveryLogRepository;
import com.hotdeal.platform.alert.persistence.repository.AlertRuleRepository;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.repository.DealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertMatchingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertMatchingService.class);

    private final AlertRuleRepository alertRuleRepository;
    private final AlertDeliveryLogRepository alertDeliveryLogRepository;
    private final DealRepository dealRepository;
    private final AlertRuleMatcher alertRuleMatcher;
    private final AlertNotificationDispatcher alertNotificationDispatcher;
    private final Clock clock;

    public AlertMatchingService(AlertRuleRepository alertRuleRepository,
                                AlertDeliveryLogRepository alertDeliveryLogRepository,
                                DealRepository dealRepository,
                                AlertRuleMatcher alertRuleMatcher,
                                AlertNotificationDispatcher alertNotificationDispatcher,
                                Clock clock) {
        this.alertRuleRepository = alertRuleRepository;
        this.alertDeliveryLogRepository = alertDeliveryLogRepository;
        this.dealRepository = dealRepository;
        this.alertRuleMatcher = alertRuleMatcher;
        this.alertNotificationDispatcher = alertNotificationDispatcher;
        this.clock = clock;
    }

    @Transactional
    public int matchAndDispatchForDeal(DealEntity deal, String trigger) {
        if (deal == null || deal.getId() == null || deal.getStatus() != DealStatus.ACTIVE || deal.getSource() == null) {
            return 0;
        }

        List<AlertRuleEntity> rules = alertRuleRepository.findActiveBySourceCodeOrGlobal(deal.getSource().getCode());
        int sent = 0;
        for (AlertRuleEntity rule : rules) {
            if (!alertRuleMatcher.matches(rule, deal)) {
                continue;
            }
            if (dispatchAndLog(rule, deal, trigger)) {
                sent++;
            }
        }
        return sent;
    }

    @Transactional
    public int matchRuleAgainstRecentDeals(AlertRuleEntity rule, int scanSize) {
        if (rule == null || rule.getId() == null || rule.getStatus() != AlertRuleStatus.ACTIVE) {
            return 0;
        }
        int effectiveSize = Math.max(1, scanSize);
        List<DealEntity> activeDeals = dealRepository.findByStatusOrderByLastSeenAtDesc(
                        DealStatus.ACTIVE,
                        PageRequest.of(0, effectiveSize))
                .getContent();

        int sent = 0;
        for (DealEntity deal : activeDeals) {
            if (!alertRuleMatcher.matches(rule, deal)) {
                continue;
            }
            if (dispatchAndLog(rule, deal, "RULE_BOOTSTRAP")) {
                sent++;
            }
        }
        return sent;
    }

    private boolean dispatchAndLog(AlertRuleEntity rule, DealEntity deal, String trigger) {
        if (alertDeliveryLogRepository.existsByAlertRuleIdAndDealIdAndDeliveryStatus(
                rule.getId(),
                deal.getId(),
                AlertDeliveryStatus.SENT)) {
            return false;
        }

        AlertNotificationResult notificationResult;
        try {
            notificationResult = alertNotificationDispatcher.dispatch(
                    new AlertNotificationRequest(rule, deal, trigger)
            );
        } catch (Exception exception) {
            notificationResult = AlertNotificationResult.failure("dispatch_exception:" + exception.getClass().getSimpleName());
        }

        AlertDeliveryLogEntity logEntity = new AlertDeliveryLogEntity();
        logEntity.setAlertRule(rule);
        logEntity.setDeal(deal);
        logEntity.setNotificationChannel(rule.getNotificationChannel());
        logEntity.setDeliveryStatus(notificationResult.success() ? AlertDeliveryStatus.SENT : AlertDeliveryStatus.FAILED);
        logEntity.setDeliveredAt(notificationResult.success() ? now() : null);
        logEntity.setErrorMessage(notificationResult.success() ? null : notificationResult.message());
        logEntity.setPayload(buildPayload(rule, deal, trigger, notificationResult));

        try {
            alertDeliveryLogRepository.save(logEntity);
        } catch (DataIntegrityViolationException integrityViolationException) {
            LOGGER.info("alert delivery skipped duplicate_sent ruleId={} dealId={}", rule.getId(), deal.getId());
            return false;
        }

        if (notificationResult.success()) {
            rule.setLastTriggeredAt(Instant.now());
            alertRuleRepository.save(rule);
            LOGGER.info("alert matched ruleId={} dealId={} subscriberKey={} trigger={}",
                    rule.getId(), deal.getId(), rule.getSubscriberKey(), trigger);
            return true;
        }

        LOGGER.warn("alert delivery failed ruleId={} dealId={} subscriberKey={} message={}",
                rule.getId(), deal.getId(), rule.getSubscriberKey(), notificationResult.message());
        return false;
    }

    private Map<String, Object> buildPayload(AlertRuleEntity rule,
                                             DealEntity deal,
                                             String trigger,
                                             AlertNotificationResult notificationResult) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("alertRuleId", rule.getId());
        payload.put("dealId", deal.getId());
        payload.put("subscriberKey", rule.getSubscriberKey());
        payload.put("sourceCode", deal.getSource() == null ? null : deal.getSource().getCode());
        payload.put("trigger", trigger);
        payload.put("status", notificationResult.success() ? "SENT" : "FAILED");
        payload.put("message", notificationResult.message());
        return payload;
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
