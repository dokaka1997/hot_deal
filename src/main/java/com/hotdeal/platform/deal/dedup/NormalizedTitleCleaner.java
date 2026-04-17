package com.hotdeal.platform.deal.dedup;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Component
public class NormalizedTitleCleaner {

    public String clean(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
