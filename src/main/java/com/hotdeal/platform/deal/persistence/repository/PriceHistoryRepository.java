package com.hotdeal.platform.deal.persistence.repository;

import com.hotdeal.platform.deal.persistence.entity.PriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistoryEntity, Long> {

    Optional<PriceHistoryEntity> findTopByDealIdOrderByCapturedAtDesc(Long dealId);

    List<PriceHistoryEntity> findTop100ByDealIdOrderByCapturedAtDesc(Long dealId);

    @Query("""
            select min(ph.dealPrice)
            from PriceHistoryEntity ph
            where ph.deal.id = :dealId
              and ph.capturedAt >= :since
            """)
    BigDecimal findMinDealPriceByDealSince(@Param("dealId") Long dealId,
                                           @Param("since") OffsetDateTime since);

    @Query("""
            select min(ph.dealPrice)
            from PriceHistoryEntity ph
            where ph.product.id = :productId
              and ph.capturedAt >= :since
            """)
    BigDecimal findMinDealPriceByProductSince(@Param("productId") Long productId,
                                              @Param("since") OffsetDateTime since);
}
