package com.hotdeal.platform.alert.matching;

import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.support.fixture.DealEntityTestBuilder;
import com.hotdeal.platform.support.fixture.SourceEntityTestBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AlertRuleMatcherTest {

    private final AlertRuleMatcher alertRuleMatcher = new AlertRuleMatcher();

    @Test
    void matches_shouldReturnTrueWhenDealMatchesAllConditions() {
        AlertRuleEntity rule = new AlertRuleEntity();
        rule.setKeyword("iphone");
        rule.setCategory("electronics");
        rule.setSourceCode("mock_deals");
        rule.setMaxPrice(new BigDecimal("900.00"));
        rule.setMinDiscountPercent(new BigDecimal("10.00"));

        SourceEntity source = SourceEntityTestBuilder.aSource()
                .withCode("mock_deals")
                .build();
        DealEntity deal = DealEntityTestBuilder.aDeal()
                .withSource(source)
                .withTitle("Apple iPhone 15 Pro")
                .withCategory("electronics")
                .withDealPrice(new BigDecimal("899.00"))
                .withDiscountPercent(new BigDecimal("15.00"))
                .withStatus(DealStatus.ACTIVE)
                .build();

        assertThat(alertRuleMatcher.matches(rule, deal)).isTrue();
    }

    @Test
    void matches_shouldReturnFalseWhenDealIsNotActive() {
        AlertRuleEntity rule = new AlertRuleEntity();
        rule.setKeyword("iphone");

        DealEntity deal = DealEntityTestBuilder.aDeal()
                .withTitle("Apple iPhone 15")
                .withStatus(DealStatus.EXPIRED)
                .build();

        assertThat(alertRuleMatcher.matches(rule, deal)).isFalse();
    }

    @Test
    void matches_shouldReturnFalseWhenPriceOrDiscountDoNotMatch() {
        AlertRuleEntity rule = new AlertRuleEntity();
        rule.setMaxPrice(new BigDecimal("500.00"));
        rule.setMinDiscountPercent(new BigDecimal("30.00"));

        DealEntity deal = DealEntityTestBuilder.aDeal()
                .withDealPrice(new BigDecimal("799.00"))
                .withDiscountPercent(new BigDecimal("20.00"))
                .withStatus(DealStatus.ACTIVE)
                .build();

        assertThat(alertRuleMatcher.matches(rule, deal)).isFalse();
    }
}
