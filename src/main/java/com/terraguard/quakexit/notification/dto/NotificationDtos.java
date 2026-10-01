package com.terraguard.quakexit.notification.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.Map;

public final class NotificationDtos {
    private NotificationDtos() {}
    public record PreferencesRequest(boolean pushEnabled, boolean smsEnabled, boolean whatsappEnabled, boolean earthquakeAlerts, boolean emergencyAlerts, boolean massAlarmAlerts, boolean lowBatteryAlerts, boolean offlineDeviceAlerts) {}
    public record PreferencesResponse(Long userId, boolean pushEnabled, boolean smsEnabled, boolean whatsappEnabled, boolean earthquakeAlerts, boolean emergencyAlerts, boolean massAlarmAlerts, boolean lowBatteryAlerts, boolean offlineDeviceAlerts) {}
    public record PushTokenRequest(@NotBlank @Size(max = 500) String token, @NotBlank @Size(max = 20) String platform, @Size(max = 120) String deviceName) {}
    public record PushTokenResponse(Long id, String token, String platform, String deviceName, boolean active, Instant createdAt) {}
    public record NotificationResponse(Long id, String type, String channel, String recipient, String status, int attempts, boolean simulation, String errorMessage, Instant sentAt, Map<String, Object> detail) {}
}
