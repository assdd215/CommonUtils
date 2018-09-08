package com.luzeping.aria.commonutils.notification;

public interface NotificationCenterDelegate {
    void didReceivedNotification(int id, Object... args);
}
