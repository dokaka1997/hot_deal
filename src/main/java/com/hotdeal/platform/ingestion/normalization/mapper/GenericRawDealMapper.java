package com.hotdeal.platform.ingestion.normalization.mapper;

import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.ingestion.normalization.model.NormalizedDealRecord;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
@Order(1000)
public class GenericRawDealMapper implements RawDealMapper {

    private final Clock clock;

    public GenericRawDealMapper(Clock clock) {
        this.clock = clock;
    }

    @Override
    public boolean supports(String sourceCode) {
        return true;
    }

    @Override
    public NormalizedDealRecord map(SourceEntity source, RawDealEntity rawDeal) {
        Map<String, Object> payload = rawDeal.getPayload();

        String sourceDealId = Optional.ofNullable(rawDeal.getSourceDealId())
                .filter(StringUtils::hasText)
                .orElse(PayloadValueReader.string(payload, "sourceDealId", "dealId", "id"));

        String title = PayloadValueReader.string(payload, "title", "name");
        String normalizedTitle = normalizeTitle(title);
        String description = PayloadValueReader.string(payload, "description", "desc");
        String brand = PayloadValueReader.string(payload, "brand");
        String category = PayloadValueReader.string(payload, "category");
        String externalUrl = sanitizeUrl(PayloadValueReader.string(payload, "url", "externalUrl", "link"));
        String imageUrl = sanitizeUrl(PayloadValueReader.string(payload, "imageUrl", "image"));
        String couponCode = PayloadValueReader.string(payload, "coupon", "couponCode", "voucherCode");

        BigDecimal originalPrice = normalizePrice(PayloadValueReader.decimal(payload, "originalPrice", "listPrice", "msrp"));
        BigDecimal dealPrice = normalizePrice(PayloadValueReader.decimal(payload, "dealPrice", "salePrice", "price"));

        String currency = normalizeCurrency(PayloadValueReader.string(payload, "currency"));
        OffsetDateTime validFrom = PayloadValueReader.dateTime(payload, "validFrom", "startAt", "startTime");
        OffsetDateTime validUntil = PayloadValueReader.dateTime(payload, "validUntil", "endAt", "endTime", "expiresAt");

        DealStatus status = normalizeStatus(
                PayloadValueReader.string(payload, "status"),
                PayloadValueReader.bool(payload, "active", "isActive"),
                validUntil
        );

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("sourceCode", source.getCode());
        metadata.put("rawDealId", rawDeal.getId());
        metadata.put("payloadVersion", rawDeal.getPayloadVersion());

        return new NormalizedDealRecord(
                sourceDealId,
                title,
                normalizedTitle,
                description,
                brand,
                category,
                externalUrl,
                imageUrl,
                currency,
                originalPrice,
                dealPrice,
                couponCode,
                validFrom,
                validUntil,
                status,
                metadata
        );
    }

    protected String normalizeTitle(String title) {
        if (!StringUtils.hasText(title)) {
            return null;
        }
        String collapsed = title.trim().replaceAll("\\s+", " ");
        return collapsed.toLowerCase(Locale.ROOT);
    }

    protected BigDecimal normalizePrice(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    protected String normalizeCurrency(String currency) {
        if (!StringUtils.hasText(currency)) {
            return "USD";
        }
        return currency.trim().toUpperCase(Locale.ROOT);
    }

    protected String sanitizeUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        return null;
    }

    protected DealStatus normalizeStatus(String payloadStatus, Boolean active, OffsetDateTime validUntil) {
        if (validUntil != null && validUntil.isBefore(now())) {
            return DealStatus.EXPIRED;
        }
        if (active != null && !active) {
            return DealStatus.INACTIVE;
        }
        if (StringUtils.hasText(payloadStatus)) {
            String normalized = payloadStatus.trim().toUpperCase(Locale.ROOT);
            if ("EXPIRED".equals(normalized)) {
                return DealStatus.EXPIRED;
            }
            if ("INACTIVE".equals(normalized) || "DISABLED".equals(normalized)) {
                return DealStatus.INACTIVE;
            }
        }
        return DealStatus.ACTIVE;
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
