package com.hotdeal.platform.deal.scoring;

import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.pricing.PriceHistoryService;
import com.hotdeal.platform.deal.pricing.model.PriceSnapshotResult;
import com.hotdeal.platform.deal.scoring.model.DealScoreBreakdown;
import com.hotdeal.platform.deal.scoring.model.DealScoreRefreshResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealPriceAndScoreService {

    private final PriceHistoryService priceHistoryService;
    private final DealScoringService dealScoringService;

    public DealPriceAndScoreService(PriceHistoryService priceHistoryService,
                                    DealScoringService dealScoringService) {
        this.priceHistoryService = priceHistoryService;
        this.dealScoringService = dealScoringService;
    }

    @Transactional
    public DealScoreRefreshResult refreshForDeal(DealEntity deal, String trigger) {
        PriceSnapshotResult snapshotResult = priceHistoryService.captureSnapshotIfNeeded(deal, trigger);
        DealScoreBreakdown scoreBreakdown = dealScoringService.scoreAndPersist(deal);
        return new DealScoreRefreshResult(snapshotResult, scoreBreakdown);
    }
}
