package com.hotdeal.platform.deal.scoring;

import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.repository.DealRepository;
import com.hotdeal.platform.deal.scoring.config.DealScoringProperties;
import com.hotdeal.platform.deal.scoring.model.DealScoreRefreshResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DealScoringSchedulerJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(DealScoringSchedulerJob.class);

    private final DealRepository dealRepository;
    private final DealPriceAndScoreService dealPriceAndScoreService;
    private final DealScoringProperties dealScoringProperties;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public DealScoringSchedulerJob(DealRepository dealRepository,
                                   DealPriceAndScoreService dealPriceAndScoreService,
                                   DealScoringProperties dealScoringProperties) {
        this.dealRepository = dealRepository;
        this.dealPriceAndScoreService = dealPriceAndScoreService;
        this.dealScoringProperties = dealScoringProperties;
    }

    @Scheduled(cron = "${app.scoring.scheduler.cron:15 */10 * * * *}")
    public void refreshActiveDealsScore() {
        if (!dealScoringProperties.getScheduler().isEnabled()) {
            return;
        }
        if (!running.compareAndSet(false, true)) {
            LOGGER.warn("deal scoring scheduler skipped because previous run is still active");
            return;
        }

        int processed = 0;
        int snapshotsCreated = 0;
        int failed = 0;
        int pageIndex = 0;

        try {
            int batchSize = Math.max(1, dealScoringProperties.getScheduler().getBatchSize());
            Page<DealEntity> page;

            do {
                page = dealRepository.findByStatusOrderByLastSeenAtDesc(
                        DealStatus.ACTIVE,
                        PageRequest.of(pageIndex, batchSize)
                );

                for (DealEntity deal : page.getContent()) {
                    try {
                        DealScoreRefreshResult result = dealPriceAndScoreService.refreshForDeal(deal, "SCHEDULED_SCORING");
                        processed++;
                        if (result.snapshotResult().created()) {
                            snapshotsCreated++;
                        }
                    } catch (Exception exception) {
                        failed++;
                        LOGGER.error("deal scoring refresh failed dealId={} message={}",
                                deal.getId(), exception.getMessage(), exception);
                    }
                }
                pageIndex++;
            } while (page.hasNext());

            LOGGER.info("deal scoring scheduler completed processed={} snapshotsCreated={} failed={}",
                    processed, snapshotsCreated, failed);
        } finally {
            running.set(false);
        }
    }
}
