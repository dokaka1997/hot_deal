package com.hotdeal.platform.alert.persistence.repository;

import com.hotdeal.platform.alert.persistence.entity.AlertDeliveryLogEntity;
import com.hotdeal.platform.alert.persistence.entity.AlertDeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertDeliveryLogRepository extends JpaRepository<AlertDeliveryLogEntity, Long> {

    boolean existsByAlertRuleIdAndDealIdAndDeliveryStatus(Long alertRuleId, Long dealId, AlertDeliveryStatus deliveryStatus);

    List<AlertDeliveryLogEntity> findByAlertRuleIdOrderByCreatedAtDesc(Long alertRuleId);
}
