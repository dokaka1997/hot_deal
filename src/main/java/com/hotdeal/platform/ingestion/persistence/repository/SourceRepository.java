package com.hotdeal.platform.ingestion.persistence.repository;

import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SourceRepository extends JpaRepository<SourceEntity, Long> {

    Optional<SourceEntity> findByCode(String code);

    List<SourceEntity> findByStatus(SourceStatus status);

    List<SourceEntity> findAllByOrderByCodeAsc();
}
