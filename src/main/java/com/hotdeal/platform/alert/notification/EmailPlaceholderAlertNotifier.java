package com.hotdeal.platform.alert.notification;

import com.hotdeal.platform.alert.notification.model.AlertNotificationRequest;
import com.hotdeal.platform.alert.notification.model.AlertNotificationResult;
import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailPlaceholderAlertNotifier implements AlertNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailPlaceholderAlertNotifier.class);

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public AlertNotificationResult send(AlertNotificationRequest request) {
        LOGGER.info("alert notification placeholder channel={} alertRuleId={} dealId={} target={}",
                channel(),
                request.alertRule().getId(),
                request.deal().getId(),
                request.alertRule().getNotificationTarget());
        return AlertNotificationResult.success("email_placeholder_notification");
    }
}
