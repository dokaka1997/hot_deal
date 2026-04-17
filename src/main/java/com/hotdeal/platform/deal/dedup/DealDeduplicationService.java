package com.hotdeal.platform.deal.dedup;

import com.hotdeal.platform.deal.dedup.model.DeduplicationDecision;
import com.hotdeal.platform.deal.dedup.model.ProductMatchResult;
import com.hotdeal.platform.ingestion.normalization.model.NormalizedDealRecord;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class DealDeduplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DealDeduplicationService.class);
    private static final String DEDUPE_KEY_VERSION = "v1";

    private final NormalizedTitleCleaner normalizedTitleCleaner;
    private final ProductMatchingService productMatchingService;

    public DealDeduplicationService(NormalizedTitleCleaner normalizedTitleCleaner,
                                    ProductMatchingService productMatchingService) {
        this.normalizedTitleCleaner = normalizedTitleCleaner;
        this.productMatchingService = productMatchingService;
    }

    public DeduplicationDecision resolve(RawDealEntity rawDeal, NormalizedDealRecord normalizedDealRecord) {
        String titleKey = normalizedTitleCleaner.clean(firstNonBlank(
                normalizedDealRecord.normalizedTitle(),
                normalizedDealRecord.title()
        ));
        String brandKey = normalizeToken(normalizedDealRecord.brand());
        String couponKey = normalizeToken(normalizedDealRecord.couponCode());

        String productFingerprintSeed = String.format("product|%s|%s",
                safeToken(titleKey, normalizedDealRecord.sourceDealId()),
                safeToken(brandKey, ""));
        String candidateProductFingerprint = sha256(productFingerprintSeed);

        ProductMatchResult productMatch = productMatchingService.matchOrCreate(
                normalizedDealRecord,
                titleKey,
                brandKey,
                candidateProductFingerprint,
                rawDeal.getPayload()
        );

        String resolvedProductFingerprint = firstNonBlank(
                productMatch.product().getFingerprint(),
                candidateProductFingerprint
        );
        String dedupeKeySeed = String.format("deal|%s|%s|%s",
                DEDUPE_KEY_VERSION,
                safeToken(resolvedProductFingerprint, normalizedDealRecord.sourceDealId()),
                safeToken(couponKey, ""));
        String dedupeKey = sha256(dedupeKeySeed);

        Map<String, Object> trace = new LinkedHashMap<>();
        trace.put("version", DEDUPE_KEY_VERSION);
        trace.put("titleKey", titleKey);
        trace.put("brandKey", brandKey);
        trace.put("couponKey", couponKey);
        trace.put("productFingerprint", resolvedProductFingerprint);
        trace.put("productMatchStrategy", productMatch.strategy().name());
        trace.put("productMatchTrace", productMatch.traceNote());

        LOGGER.info("dedup resolved rawDealId={} sourceCode={} dedupeKey={} productId={} strategy={} titleKey={} brandKey={}",
                rawDeal.getId(),
                rawDeal.getSource().getCode(),
                dedupeKey,
                productMatch.product().getId(),
                productMatch.strategy(),
                titleKey,
                brandKey);

        return new DeduplicationDecision(
                dedupeKey,
                productMatch.product(),
                trace
        );
    }

    private String firstNonBlank(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary.trim();
        }
        if (StringUtils.hasText(fallback)) {
            return fallback.trim();
        }
        return null;
    }

    private String normalizeToken(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String safeToken(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return preferred;
        }
        if (StringUtils.hasText(fallback)) {
            return fallback;
        }
        return "unknown";
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", exception);
        }
    }
}
