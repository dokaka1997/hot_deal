package com.hotdeal.platform.alert.persistence.repository;

import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRuleRepository extends JpaRepository<AlertRuleEntity, Long> {

    Page<AlertRuleEntity> findBySubscriberKey(String subscriberKey, Pageable pageable);

    @Query("""
            select ar from AlertRuleEntity ar
            where ar.status = com.hotdeal.platform.alert.persistence.entity.AlertRuleStatus.ACTIVE
              and (ar.sourceCode is null or lower(ar.sourceCode) = lower(:sourceCode))
            order by ar.id asc
            """)
    List<AlertRuleEntity> findActiveBySourceCodeOrGlobal(@Param("sourceCode") String sourceCode);

    List<AlertRuleEntity> findByStatusOrderByCreatedAtDesc(AlertRuleStatus status);
}
