package com.hotdeal.platform.deal.scoring.model;

import java.math.BigDecimal;

public record DealScoreBreakdown(
        BigDecimal finalScore,
        BigDecimal discountScore,
        BigDecimal statusScore,
        BigDecimal sourceConfidenceScore,
        BigDecimal pricePositionScore,
        BigDecimal lowestRecentPrice,
        double sourceConfidence,
        int lookbackDays,
        String formulaVersion
) {
}
