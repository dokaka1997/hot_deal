package com.hotdeal.platform.alert.notification;

import com.hotdeal.platform.alert.notification.model.AlertNotificationRequest;
import com.hotdeal.platform.alert.notification.model.AlertNotificationResult;
import com.hotdeal.platform.alert.persistence.entity.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class AlertNotificationDispatcher {

    private final Map<NotificationChannel, AlertNotifier> notifiersByChannel;

    public AlertNotificationDispatcher(List<AlertNotifier> notifiers) {
        EnumMap<NotificationChannel, AlertNotifier> mapping = new EnumMap<>(NotificationChannel.class);
        for (AlertNotifier notifier : notifiers) {
            mapping.put(notifier.channel(), notifier);
        }
        this.notifiersByChannel = Map.copyOf(mapping);
    }

    public AlertNotificationResult dispatch(AlertNotificationRequest request) {
        NotificationChannel channel = request.alertRule().getNotificationChannel();
        AlertNotifier notifier = notifiersByChannel.get(channel);
        if (notifier == null) {
            return AlertNotificationResult.failure("no_notifier_for_channel_" + channel);
        }
        return notifier.send(request);
    }
}
