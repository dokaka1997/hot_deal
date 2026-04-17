package com.hotdeal.platform.alert.notification;

import com.hotdeal.platform.alert.notification.model.AlertNotificationRequest;
import com.hotdeal.platform.alert.notification.model.AlertNotificationResult;
import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingAlertNotifier implements AlertNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAlertNotifier.class);

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.INTERNAL_LOG;
    }

    @Override
    public AlertNotificationResult send(AlertNotificationRequest request) {
        LOGGER.info("alert notification sent channel={} alertRuleId={} dealId={} subscriberKey={} trigger={} title={} price={}",
                channel(),
                request.alertRule().getId(),
                request.deal().getId(),
                request.alertRule().getSubscriberKey(),
                request.trigger(),
                request.deal().getTitle(),
                request.deal().getDealPrice());
        return AlertNotificationResult.success("logged_internal_notification");
    }
}
