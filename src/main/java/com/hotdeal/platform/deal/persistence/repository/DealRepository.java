package com.hotdeal.platform.deal.persistence.repository;

import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.repository.projection.CategoryDealCountProjection;
import com.hotdeal.platform.deal.persistence.repository.projection.SourceDealCountProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface DealRepository extends JpaRepository<DealEntity, Long>, JpaSpecificationExecutor<DealEntity> {

    Optional<DealEntity> findBySourceIdAndSourceDealId(Long sourceId, String sourceDealId);

    Optional<DealEntity> findBySourceIdAndFingerprint(Long sourceId, String fingerprint);

    Optional<DealEntity> findTopBySourceIdAndDedupeKeyOrderByLastSeenAtDesc(Long sourceId, String dedupeKey);

    Page<DealEntity> findByStatusOrderByLastSeenAtDesc(DealStatus status, Pageable pageable);

    long countByStatus(DealStatus status);

    @Query("""
            select s.code as sourceCode,
                   s.name as sourceName,
                   count(d.id) as dealCount
            from DealEntity d
            join d.source s
            where (:status is null or d.status = :status)
            group by s.code, s.name
            order by count(d.id) desc, s.code asc
            """)
    List<SourceDealCountProjection> summarizeDealCountBySource(@Param("status") DealStatus status, Pageable pageable);

    @Query("""
            select coalesce(d.category, 'uncategorized') as category,
                   count(d.id) as dealCount
            from DealEntity d
            where (:status is null or d.status = :status)
            group by coalesce(d.category, 'uncategorized')
            order by count(d.id) desc, coalesce(d.category, 'uncategorized') asc
            """)
    List<CategoryDealCountProjection> summarizeDealCountByCategory(@Param("status") DealStatus status, Pageable pageable);

    @Query("""
            select avg(d.discountPercent)
            from DealEntity d
            where d.discountPercent is not null
              and (:status is null or d.status = :status)
            """)
    Double averageDiscountPercent(@Param("status") DealStatus status);

    @EntityGraph(attributePaths = {"source"})
    @Query("""
            select d
            from DealEntity d
            where (:status is null or d.status = :status)
            order by coalesce(d.dealScore, 0) desc,
                     coalesce(d.discountPercent, 0) desc,
                     d.lastSeenAt desc
            """)
    List<DealEntity> findHottestDeals(@Param("status") DealStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"source", "product"})
    Optional<DealEntity> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"source", "product"})
    Page<DealEntity> findAll(Specification<DealEntity> spec, Pageable pageable);
}
