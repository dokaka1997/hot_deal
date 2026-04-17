package com.hotdeal.platform.alert.application;

import com.hotdeal.platform.alert.api.dto.AlertRuleResponse;
import com.hotdeal.platform.alert.api.dto.CreateAlertRuleRequest;
import com.hotdeal.platform.alert.api.mapper.AlertRuleMapper;
import com.hotdeal.platform.alert.config.AlertProperties;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleEntity;
import com.hotdeal.platform.alert.persistence.entity.AlertRuleStatus;
import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;
import com.hotdeal.platform.alert.persistence.repository.AlertRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertRuleServiceTest {

    @Mock
    private AlertRuleRepository alertRuleRepository;

    @Mock
    private AlertRuleMapper alertRuleMapper;

    @Mock
    private AlertMatchingService alertMatchingService;

    private AlertRuleService alertRuleService;

    @BeforeEach
    void setUp() {
        AlertProperties alertProperties = new AlertProperties();
        alertProperties.getMatching().setBootstrapScanSize(30);
        alertRuleService = new AlertRuleService(
                alertRuleRepository,
                alertRuleMapper,
                alertMatchingService,
                alertProperties
        );
    }

    @Test
    void create_shouldTrimAndPersistRuleWithDefaults() {
        CreateAlertRuleRequest request = new CreateAlertRuleRequest();
        request.setSubscriberKey("  guest-01  ");
        request.setName("  Best deals  ");
        request.setKeyword("  iphone  ");
        request.setCategory("  electronics  ");
        request.setSourceCode("  mock_deals  ");
        request.setMaxPrice(new BigDecimal("900"));
        request.setMinDiscountPercent(new BigDecimal("20"));
        request.setNotificationChannel(null);
        request.setNotificationTarget("  user@example.com  ");

        AlertRuleResponse expected = new AlertRuleResponse(
                100L,
                "guest-01",
                "Best deals",
                "iphone",
                "electronics",
                "mock_deals",
                new BigDecimal("900"),
                new BigDecimal("20"),
                NotificationChannel.INTERNAL_LOG,
                "user@example.com",
                AlertRuleStatus.ACTIVE,
                null,
                OffsetDateTime.parse("2026-04-15T00:00:00Z"),
                OffsetDateTime.parse("2026-04-15T00:00:00Z")
        );

        when(alertRuleRepository.save(any(AlertRuleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(alertMatchingService.matchRuleAgainstRecentDeals(any(AlertRuleEntity.class), eq(30))).thenReturn(2);
        when(alertRuleMapper.toResponse(any(AlertRuleEntity.class))).thenReturn(expected);

        AlertRuleResponse actual = alertRuleService.create(request);

        assertThat(actual).isEqualTo(expected);

        ArgumentCaptor<AlertRuleEntity> captor = ArgumentCaptor.forClass(AlertRuleEntity.class);
        verify(alertRuleRepository).save(captor.capture());
        AlertRuleEntity saved = captor.getValue();
        assertThat(saved.getSubscriberKey()).isEqualTo("guest-01");
        assertThat(saved.getName()).isEqualTo("Best deals");
        assertThat(saved.getKeyword()).isEqualTo("iphone");
        assertThat(saved.getCategory()).isEqualTo("electronics");
        assertThat(saved.getSourceCode()).isEqualTo("mock_deals");
        assertThat(saved.getNotificationChannel()).isEqualTo(NotificationChannel.INTERNAL_LOG);
        assertThat(saved.getNotificationTarget()).isEqualTo("user@example.com");
        assertThat(saved.getStatus()).isEqualTo(AlertRuleStatus.ACTIVE);
        assertThat(saved.getMetadata()).containsEntry("version", "mvp-v1");

        verify(alertMatchingService).matchRuleAgainstRecentDeals(saved, 30);
        verify(alertRuleMapper).toResponse(saved);
    }
}
