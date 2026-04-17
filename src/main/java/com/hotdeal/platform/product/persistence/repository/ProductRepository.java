package com.hotdeal.platform.product.persistence.repository;

import com.hotdeal.platform.product.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByFingerprint(String fingerprint);

    Optional<ProductEntity> findByCanonicalSku(String canonicalSku);

    List<ProductEntity> findTop20ByNormalizedNameOrderByIdAsc(String normalizedName);
}
