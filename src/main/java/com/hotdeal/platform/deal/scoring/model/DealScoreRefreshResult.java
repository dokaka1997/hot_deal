package com.hotdeal.platform.deal.scoring.model;

import com.hotdeal.platform.deal.pricing.model.PriceSnapshotResult;

public record DealScoreRefreshResult(
        PriceSnapshotResult snapshotResult,
        DealScoreBreakdown scoreBreakdown
) {
}
