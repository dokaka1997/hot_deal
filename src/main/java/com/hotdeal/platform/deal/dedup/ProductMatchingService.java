package com.hotdeal.platform.deal.dedup;

import com.hotdeal.platform.deal.dedup.model.ProductMatchResult;
import com.hotdeal.platform.deal.dedup.model.ProductMatchStrategy;
import com.hotdeal.platform.ingestion.normalization.model.NormalizedDealRecord;
import com.hotdeal.platform.product.persistence.entity.ProductEntity;
import com.hotdeal.platform.product.persistence.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductMatchingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductMatchingService.class);

    private final ProductRepository productRepository;

    public ProductMatchingService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductMatchResult matchOrCreate(NormalizedDealRecord normalizedDealRecord,
                                            String normalizedTitleKey,
                                            String brandKey,
                                            String productFingerprint,
                                            Map<String, Object> rawPayload) {
        String canonicalSku = extractCanonicalSku(rawPayload);

        if (StringUtils.hasText(canonicalSku)) {
            Optional<ProductEntity> byCanonicalSku = productRepository.findByCanonicalSku(canonicalSku);
            if (byCanonicalSku.isPresent()) {
                ProductEntity product = hydrateProduct(byCanonicalSku.get(), normalizedDealRecord, canonicalSku, normalizedTitleKey, productFingerprint);
                return matched(product, ProductMatchStrategy.CANONICAL_SKU, "matched_by_canonical_sku");
            }
        }

        if (StringUtils.hasText(productFingerprint)) {
            Optional<ProductEntity> byFingerprint = productRepository.findByFingerprint(productFingerprint);
            if (byFingerprint.isPresent()) {
                ProductEntity product = hydrateProduct(byFingerprint.get(), normalizedDealRecord, canonicalSku, normalizedTitleKey, productFingerprint);
                return matched(product, ProductMatchStrategy.FINGERPRINT, "matched_by_fingerprint");
            }
        }

        Optional<ProductEntity> byNameAndBrand = matchByNormalizedNameAndBrand(normalizedTitleKey, brandKey);
        if (byNameAndBrand.isPresent()) {
            ProductEntity product = hydrateProduct(byNameAndBrand.get(), normalizedDealRecord, canonicalSku, normalizedTitleKey, productFingerprint);
            return matched(product, ProductMatchStrategy.NORMALIZED_NAME_BRAND, "matched_by_normalized_name_brand");
        }

        try {
            ProductEntity created = createProduct(normalizedDealRecord, canonicalSku, normalizedTitleKey, productFingerprint);
            LOGGER.info("product created productId={} fingerprint={} canonicalSku={}",
                    created.getId(), created.getFingerprint(), created.getCanonicalSku());
            return new ProductMatchResult(created, ProductMatchStrategy.CREATED_NEW, "created_new_product");
        } catch (DataIntegrityViolationException integrityViolationException) {
            Optional<ProductEntity> matchedAfterRace = findAfterCreateRace(canonicalSku, productFingerprint);
            if (matchedAfterRace.isPresent()) {
                ProductEntity product = hydrateProduct(matchedAfterRace.get(), normalizedDealRecord, canonicalSku, normalizedTitleKey, productFingerprint);
                ProductMatchStrategy strategy = StringUtils.hasText(canonicalSku)
                        ? ProductMatchStrategy.CANONICAL_SKU
                        : ProductMatchStrategy.FINGERPRINT;
                return matched(product, strategy, "matched_after_race_condition");
            }
            throw integrityViolationException;
        }
    }

    private Optional<ProductEntity> matchByNormalizedNameAndBrand(String normalizedTitleKey, String brandKey) {
        if (!StringUtils.hasText(normalizedTitleKey)) {
            return Optional.empty();
        }
        List<ProductEntity> candidates = productRepository.findTop20ByNormalizedNameOrderByIdAsc(normalizedTitleKey);
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        if (StringUtils.hasText(brandKey)) {
            return candidates.stream()
                    .filter(candidate -> brandKey.equals(normalizeBrand(candidate.getBrand())))
                    .findFirst();
        }

        List<ProductEntity> noBrandCandidates = candidates.stream()
                .filter(candidate -> !StringUtils.hasText(candidate.getBrand()))
                .toList();
        if (noBrandCandidates.size() == 1) {
            return Optional.of(noBrandCandidates.get(0));
        }
        if (candidates.size() == 1) {
            return Optional.of(candidates.get(0));
        }
        return Optional.empty();
    }

    private ProductEntity hydrateProduct(ProductEntity product,
                                         NormalizedDealRecord normalizedDealRecord,
                                         String canonicalSku,
                                         String normalizedTitleKey,
                                         String productFingerprint) {
        boolean changed = false;

        if (!StringUtils.hasText(product.getCanonicalSku()) && StringUtils.hasText(canonicalSku)) {
            product.setCanonicalSku(canonicalSku);
            changed = true;
        }
        if (!StringUtils.hasText(product.getNormalizedName()) && StringUtils.hasText(normalizedTitleKey)) {
            product.setNormalizedName(normalizedTitleKey);
            changed = true;
        }
        if (!StringUtils.hasText(product.getBrand()) && StringUtils.hasText(normalizedDealRecord.brand())) {
            product.setBrand(normalizedDealRecord.brand().trim());
            changed = true;
        }
        if (!StringUtils.hasText(product.getCategory()) && StringUtils.hasText(normalizedDealRecord.category())) {
            product.setCategory(normalizedDealRecord.category().trim());
            changed = true;
        }
        if (!StringUtils.hasText(product.getImageUrl()) && StringUtils.hasText(normalizedDealRecord.imageUrl())) {
            product.setImageUrl(normalizedDealRecord.imageUrl().trim());
            changed = true;
        }
        if (!StringUtils.hasText(product.getFingerprint()) && StringUtils.hasText(productFingerprint)) {
            product.setFingerprint(productFingerprint);
            changed = true;
        }

        if (changed) {
            return productRepository.save(product);
        }
        return product;
    }

    private ProductEntity createProduct(NormalizedDealRecord normalizedDealRecord,
                                        String canonicalSku,
                                        String normalizedTitleKey,
                                        String productFingerprint) {
        ProductEntity product = new ProductEntity();
        product.setCanonicalSku(canonicalSku);
        product.setName(normalizedDealRecord.title());
        product.setNormalizedName(normalizedTitleKey);
        product.setBrand(trimToNull(normalizedDealRecord.brand()));
        product.setCategory(trimToNull(normalizedDealRecord.category()));
        product.setImageUrl(trimToNull(normalizedDealRecord.imageUrl()));
        product.setFingerprint(productFingerprint);
        product.setAttributes(buildAttributes(normalizedDealRecord));
        product.setActive(true);
        return productRepository.save(product);
    }

    private Optional<ProductEntity> findAfterCreateRace(String canonicalSku, String productFingerprint) {
        if (StringUtils.hasText(canonicalSku)) {
            Optional<ProductEntity> byCanonicalSku = productRepository.findByCanonicalSku(canonicalSku);
            if (byCanonicalSku.isPresent()) {
                return byCanonicalSku;
            }
        }
        if (StringUtils.hasText(productFingerprint)) {
            return productRepository.findByFingerprint(productFingerprint);
        }
        return Optional.empty();
    }

    private ProductMatchResult matched(ProductEntity product, ProductMatchStrategy strategy, String traceNote) {
        LOGGER.info("product matched strategy={} productId={} fingerprint={} canonicalSku={}",
                strategy, product.getId(), product.getFingerprint(), product.getCanonicalSku());
        return new ProductMatchResult(product, strategy, traceNote);
    }

    private Map<String, Object> buildAttributes(NormalizedDealRecord normalizedDealRecord) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("sourceDealId", normalizedDealRecord.sourceDealId());
        attributes.put("seedCategory", normalizedDealRecord.category());
        attributes.put("seedBrand", normalizedDealRecord.brand());
        return attributes;
    }

    private String extractCanonicalSku(Map<String, Object> rawPayload) {
        if (rawPayload == null || rawPayload.isEmpty()) {
            return null;
        }
        return firstNonBlank(
                rawPayload.get("sku"),
                rawPayload.get("productSku"),
                rawPayload.get("upc"),
                rawPayload.get("ean"),
                rawPayload.get("gtin"),
                rawPayload.get("asin")
        );
    }

    private String firstNonBlank(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            if (value == null) {
                continue;
            }
            String text = String.valueOf(value).trim();
            if (!text.isEmpty()) {
                return text;
            }
        }
        return null;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeBrand(String brand) {
        if (!StringUtils.hasText(brand)) {
            return null;
        }
        return brand.trim().toLowerCase(Locale.ROOT);
    }
}
