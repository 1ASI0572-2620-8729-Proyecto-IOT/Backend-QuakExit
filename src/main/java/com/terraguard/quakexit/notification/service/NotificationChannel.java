package com.terraguard.quakexit.notification.service;

public interface NotificationChannel {
    String name();
    void send(String recipient, String subject, String message);
}
