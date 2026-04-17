package com.hotdeal.platform.deal.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PublicDealSearchRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validate_shouldFailWhenMaxPriceIsLessThanMinPrice() {
        PublicDealSearchRequest request = new PublicDealSearchRequest();
        request.setMinPrice(new BigDecimal("500"));
        request.setMaxPrice(new BigDecimal("100"));

        Set<ConstraintViolation<PublicDealSearchRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("priceRangeValid");
    }

    @Test
    void validate_shouldPassWhenPriceRangeIsValid() {
        PublicDealSearchRequest request = new PublicDealSearchRequest();
        request.setMinPrice(new BigDecimal("100"));
        request.setMaxPrice(new BigDecimal("500"));

        Set<ConstraintViolation<PublicDealSearchRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
