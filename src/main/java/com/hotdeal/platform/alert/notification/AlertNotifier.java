package com.hotdeal.platform.alert.notification;

import com.hotdeal.platform.alert.notification.model.AlertNotificationRequest;
import com.hotdeal.platform.alert.notification.model.AlertNotificationResult;
import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;

public interface AlertNotifier {

    NotificationChannel channel();

    AlertNotificationResult send(AlertNotificationRequest request);
}
