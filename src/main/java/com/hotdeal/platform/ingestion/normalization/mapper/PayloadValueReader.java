package com.hotdeal.platform.ingestion.normalization.mapper;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

final class PayloadValueReader {

    private PayloadValueReader() {
    }

    static String string(Map<String, Object> payload, String... keys) {
        Object value = value(payload, keys);
        if (value == null) {
            return null;
        }
        String normalized = String.valueOf(value).trim();
        return normalized.isEmpty() ? null : normalized;
    }

    static BigDecimal decimal(Map<String, Object> payload, String... keys) {
        Object value = value(payload, keys);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    static OffsetDateTime dateTime(Map<String, Object> payload, String... keys) {
        String value = string(payload, keys);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException parseException) {
            return null;
        }
    }

    static Boolean bool(Map<String, Object> payload, String... keys) {
        Object value = value(payload, keys);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        String text = String.valueOf(value).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(text) || "1".equals(text) || "yes".equals(text)) {
            return true;
        }
        if ("false".equals(text) || "0".equals(text) || "no".equals(text)) {
            return false;
        }
        return null;
    }

    private static Object value(Map<String, Object> payload, String... keys) {
        if (payload == null || payload.isEmpty() || keys == null || keys.length == 0) {
            return null;
        }
        return Arrays.stream(keys)
                .filter(payload::containsKey)
                .map(payload::get)
                .findFirst()
                .orElse(null);
    }
}
