package com.hotdeal.platform.ingestion.bootstrap;

import com.hotdeal.platform.ingestion.collector.mock.MockDealsCollector;
import com.hotdeal.platform.ingestion.config.IngestionProperties;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceStatus;
import com.hotdeal.platform.ingestion.persistence.entity.SourceType;
import com.hotdeal.platform.ingestion.persistence.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile({"local", "test"})
public class SampleSourceBootstrap implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSourceBootstrap.class);

    private final SourceRepository sourceRepository;
    private final IngestionProperties ingestionProperties;

    public SampleSourceBootstrap(SourceRepository sourceRepository,
                                 IngestionProperties ingestionProperties) {
        this.sourceRepository = sourceRepository;
        this.ingestionProperties = ingestionProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!ingestionProperties.isBootstrapSampleSource()) {
            return;
        }

        sourceRepository.findByCode(MockDealsCollector.SOURCE_CODE)
                .ifPresentOrElse(
                        existing -> LOGGER.info("sample source already exists sourceCode={} sourceId={}",
                                existing.getCode(), existing.getId()),
                        this::createSampleSource
                );
    }

    private void createSampleSource() {
        SourceEntity source = new SourceEntity();
        source.setCode(MockDealsCollector.SOURCE_CODE);
        source.setName("Mock Deals Source");
        source.setType(SourceType.API);
        source.setStatus(SourceStatus.ACTIVE);
        source.setBaseUrl("https://mock.example");
        source.setScheduleCron(ingestionProperties.getScheduler().getCron());
        source.setRateLimitPerMinute(120);
        source.setMetadata(Map.of(
                "owner", "platform-team",
                "note", "Bootstrap sample source for ingestion framework validation"
        ));
        SourceEntity saved = sourceRepository.save(source);
        LOGGER.info("sample source created sourceCode={} sourceId={}", saved.getCode(), saved.getId());
    }
}
