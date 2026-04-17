package com.hotdeal.platform.ingestion.persistence.repository;

import com.hotdeal.platform.ingestion.persistence.entity.CollectorJobExecutionEntity;
import com.hotdeal.platform.ingestion.persistence.entity.JobExecutionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface CollectorJobExecutionRepository extends JpaRepository<CollectorJobExecutionEntity, Long> {

    Optional<CollectorJobExecutionEntity> findByRunKey(String runKey);

    Optional<CollectorJobExecutionEntity> findTopBySourceIdOrderByStartedAtDesc(Long sourceId);

    Page<CollectorJobExecutionEntity> findAllByOrderByStartedAtDesc(Pageable pageable);

    Page<CollectorJobExecutionEntity> findBySourceIdOrderByStartedAtDesc(Long sourceId, Pageable pageable);

    Page<CollectorJobExecutionEntity> findByStatusOrderByStartedAtDesc(JobExecutionStatus status, Pageable pageable);

    Page<CollectorJobExecutionEntity> findBySourceIdAndStatusOrderByStartedAtDesc(Long sourceId,
                                                                                   JobExecutionStatus status,
                                                                                   Pageable pageable);

    long countBySourceIdAndStartedAtGreaterThanEqual(Long sourceId, OffsetDateTime since);

    long countBySourceIdAndStatusAndStartedAtGreaterThanEqual(Long sourceId,
                                                              JobExecutionStatus status,
                                                              OffsetDateTime since);
}
