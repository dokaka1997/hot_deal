package com.hotdeal.platform.alert.matching;

import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;

@Component
public class AlertRuleMatcher {

    public boolean matches(AlertRuleEntity rule, DealEntity deal) {
        if (deal.getStatus() != DealStatus.ACTIVE) {
            return false;
        }
        if (!matchesKeyword(rule.getKeyword(), deal)) {
            return false;
        }
        if (!matchesCategory(rule.getCategory(), deal.getCategory())) {
            return false;
        }
        if (!matchesSource(rule.getSourceCode(), deal.getSource() == null ? null : deal.getSource().getCode())) {
            return false;
        }
        if (!matchesMaxPrice(rule.getMaxPrice(), deal.getDealPrice())) {
            return false;
        }
        return matchesMinDiscount(rule.getMinDiscountPercent(), deal.getDiscountPercent());
    }

    private boolean matchesKeyword(String keyword, DealEntity deal) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        String haystack = String.join(" ",
                normalize(deal.getTitle()),
                normalize(deal.getNormalizedTitle()),
                normalize(deal.getBrand()),
                normalize(deal.getCategory()),
                normalize(deal.getDescription())
        );
        return haystack.contains(needle);
    }

    private boolean matchesCategory(String expectedCategory, String actualCategory) {
        if (!StringUtils.hasText(expectedCategory)) {
            return true;
        }
        if (!StringUtils.hasText(actualCategory)) {
            return false;
        }
        return expectedCategory.trim().equalsIgnoreCase(actualCategory.trim());
    }

    private boolean matchesSource(String expectedSource, String actualSource) {
        if (!StringUtils.hasText(expectedSource)) {
            return true;
        }
        if (!StringUtils.hasText(actualSource)) {
            return false;
        }
        return expectedSource.trim().equalsIgnoreCase(actualSource.trim());
    }

    private boolean matchesMaxPrice(BigDecimal maxPrice, BigDecimal dealPrice) {
        if (maxPrice == null) {
            return true;
        }
        if (dealPrice == null) {
            return false;
        }
        return dealPrice.compareTo(maxPrice) <= 0;
    }

    private boolean matchesMinDiscount(BigDecimal minDiscountPercent, BigDecimal dealDiscountPercent) {
        if (minDiscountPercent == null) {
            return true;
        }
        BigDecimal discount = dealDiscountPercent == null ? BigDecimal.ZERO : dealDiscountPercent;
        return discount.compareTo(minDiscountPercent) >= 0;
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
