package com.hotdeal.platform.alert.notification.model;

public record AlertNotificationResult(
        boolean success,
        String message
) {
    public static AlertNotificationResult success(String message) {
        return new AlertNotificationResult(true, message);
    }

    public static AlertNotificationResult failure(String message) {
        return new AlertNotificationResult(false, message);
    }
}
