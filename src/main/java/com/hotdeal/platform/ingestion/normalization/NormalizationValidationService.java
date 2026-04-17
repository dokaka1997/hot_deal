package com.hotdeal.platform.ingestion.normalization;

import com.hotdeal.platform.ingestion.normalization.model.NormalizedDealRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class NormalizationValidationService {

    public List<String> validate(NormalizedDealRecord normalizedDealRecord) {
        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(normalizedDealRecord.sourceDealId())) {
            errors.add("sourceDealId is required");
        }
        if (!StringUtils.hasText(normalizedDealRecord.title())) {
            errors.add("title is required");
        }
        if (!StringUtils.hasText(normalizedDealRecord.normalizedTitle())) {
            errors.add("normalizedTitle is required");
        }
        if (!StringUtils.hasText(normalizedDealRecord.currency())) {
            errors.add("currency is required");
        } else if (normalizedDealRecord.currency().trim().length() != 3) {
            errors.add("currency must be ISO-4217 3-letter code");
        }
        if (normalizedDealRecord.dealPrice() == null) {
            errors.add("dealPrice is required");
        } else if (normalizedDealRecord.dealPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("dealPrice must be non-negative");
        }
        if (normalizedDealRecord.originalPrice() != null) {
            if (normalizedDealRecord.originalPrice().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("originalPrice must be non-negative");
            }
            if (normalizedDealRecord.dealPrice() != null
                    && normalizedDealRecord.originalPrice().compareTo(normalizedDealRecord.dealPrice()) < 0) {
                errors.add("originalPrice must be greater than or equal to dealPrice");
            }
        }
        if (normalizedDealRecord.validFrom() != null
                && normalizedDealRecord.validUntil() != null
                && normalizedDealRecord.validUntil().isBefore(normalizedDealRecord.validFrom())) {
            errors.add("validUntil must be after validFrom");
        }
        if (StringUtils.hasText(normalizedDealRecord.externalUrl()) && !isHttpUrl(normalizedDealRecord.externalUrl())) {
            errors.add("externalUrl must be an absolute HTTP/HTTPS URL");
        }
        if (StringUtils.hasText(normalizedDealRecord.imageUrl()) && !isHttpUrl(normalizedDealRecord.imageUrl())) {
            errors.add("imageUrl must be an absolute HTTP/HTTPS URL");
        }

        return errors;
    }

    private boolean isHttpUrl(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.startsWith("http://") || normalized.startsWith("https://");
    }
}
