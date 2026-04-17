package com.hotdeal.platform.ingestion.persistence.repository;

import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface RawDealRepository extends JpaRepository<RawDealEntity, Long>, JpaSpecificationExecutor<RawDealEntity> {

    Optional<RawDealEntity> findBySourceIdAndSourceRecordKey(Long sourceId, String sourceRecordKey);

    Optional<RawDealEntity> findBySourceIdAndSourceRecordHash(Long sourceId, String sourceRecordHash);

    Optional<RawDealEntity> findBySourceIdAndSourceDealId(Long sourceId, String sourceDealId);

    Page<RawDealEntity> findByStatusOrderByIngestedAtDesc(RawDealStatus status, Pageable pageable);

    Page<RawDealEntity> findByStatusOrderByIngestedAtAsc(RawDealStatus status, Pageable pageable);

    List<RawDealEntity> findTop100ByStatusOrderByIngestedAtAsc(RawDealStatus status);

    @Override
    @EntityGraph(attributePaths = {"source", "jobExecution"})
    Optional<RawDealEntity> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"source", "jobExecution"})
    Page<RawDealEntity> findAll(Specification<RawDealEntity> spec, Pageable pageable);
}
