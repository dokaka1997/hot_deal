package com.hotdeal.platform.ingestion.collector.mock;

import com.hotdeal.platform.ingestion.collector.CollectedRawDeal;
import com.hotdeal.platform.ingestion.collector.CollectorContext;
import com.hotdeal.platform.ingestion.collector.DealCollector;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MockDealsCollector implements DealCollector {

    public static final String SOURCE_CODE = "MOCK_DEALS";

    @Override
    public String sourceCode() {
        return SOURCE_CODE;
    }

    @Override
    public List<CollectedRawDeal> collect(CollectorContext context) {
        return List.of(
                new CollectedRawDeal(
                        "MOCK-1001",
                        "MOCK-1001",
                        null,
                        Map.of(
                                "title", "Mock Headphones X1",
                                "brand", "MockAudio",
                                "category", "Electronics",
                                "currency", "USD",
                                "originalPrice", 89.99,
                                "dealPrice", 59.99,
                                "url", "https://mock.example/deals/1001",
                                "coupon", "SAVE10"
                        ),
                        "v1"
                ),
                new CollectedRawDeal(
                        "MOCK-1002",
                        "MOCK-1002",
                        null,
                        Map.of(
                                "title", "Mock Coffee Maker Pro",
                                "brand", "MockHome",
                                "category", "Home Appliances",
                                "currency", "USD",
                                "originalPrice", 129.00,
                                "dealPrice", 88.00,
                                "url", "https://mock.example/deals/1002"
                        ),
                        "v1"
                ),
                new CollectedRawDeal(
                        "MOCK-1003",
                        "MOCK-1003",
                        null,
                        Map.of(
                                "title", "Mock Running Shoes R5",
                                "brand", "MockSport",
                                "category", "Fashion",
                                "currency", "USD",
                                "originalPrice", 110.00,
                                "dealPrice", 69.00,
                                "url", "https://mock.example/deals/1003"
                        ),
                        "v1"
                )
        );
    }
}
