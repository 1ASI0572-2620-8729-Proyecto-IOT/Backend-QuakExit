package com.terraguard.quakexit.notification.service;

import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.notification.dto.NotificationDtos.*;
import com.terraguard.quakexit.notification.entity.*;
import com.terraguard.quakexit.notification.repository.*;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class NotificationManager {
    private final NotificationPreferenceRepository preferences;
    private final PushTokenRepository tokens;
    private final NotificationLogRepository logs;

    @Transactional(readOnly = true)
    public PreferencesResponse preferences(User user) {
        return toResponse(preferences.findByUserId(user.getId()).orElseGet(() -> NotificationPreference.builder().user(user).build()));
    }

    @Transactional
    public PreferencesResponse updatePreferences(PreferencesRequest request, User user) {
        NotificationPreference preference = preferences.findByUserId(user.getId()).orElseGet(() -> NotificationPreference.builder().user(user).build());
        preference.setPushEnabled(request.pushEnabled()); preference.setSmsEnabled(request.smsEnabled()); preference.setWhatsappEnabled(request.whatsappEnabled());
        preference.setEarthquakeAlerts(request.earthquakeAlerts()); preference.setEmergencyAlerts(request.emergencyAlerts()); preference.setMassAlarmAlerts(request.massAlarmAlerts());
        preference.setLowBatteryAlerts(request.lowBatteryAlerts()); preference.setOfflineDeviceAlerts(request.offlineDeviceAlerts());
        return toResponse(preferences.save(preference));
    }

    @Transactional
    public PushTokenResponse addToken(PushTokenRequest request, User user) {
        tokens.findByToken(request.token()).ifPresent(existing -> { if (!existing.getUser().getId().equals(user.getId())) throw new DuplicateResourceException("El token ya pertenece a otro usuario"); existing.setActive(true); });
        PushToken token = tokens.findByToken(request.token()).orElseGet(PushToken::new);
        token.setUser(user); token.setToken(request.token()); token.setPlatform(request.platform()); token.setDeviceName(request.deviceName()); token.setActive(true);
        return toResponse(tokens.save(token));
    }

    @Transactional
    public void removeToken(Long tokenId, User user) {
        PushToken token = tokens.findByIdAndUserId(tokenId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Token push no encontrado"));
        token.setActive(false); tokens.save(token);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> history(String channel, String status, String type, Instant from, Instant to, Pageable pageable) {
        Specification<NotificationLog> specification = Specification.where(equal("channel", channel)).and(equal("status", status)).and(equal("notificationType", type))
            .and(from == null ? null : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from))
            .and(to == null ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to));
        return logs.findAll(specification, pageable).map(this::toResponse);
    }

    @Async
    @Transactional
    public void enqueue(User user, String type, String channel, String recipient, boolean simulation) {
        NotificationLog log = logs.save(NotificationLog.builder().user(user).notificationType(type).channel(channel).recipient(recipient).simulation(simulation).status(simulation ? "SIMULATION" : "FAILED").attempts(1).errorMessage(simulation ? null : "Proveedor de notificaciones no configurado").build());
        logs.save(log);
    }

    private <T> Specification<NotificationLog> equal(String field, T value) { return value == null ? null : (root, query, cb) -> cb.equal(root.get(field), value); }
    private PreferencesResponse toResponse(NotificationPreference p) { return new PreferencesResponse(p.getUser().getId(), p.isPushEnabled(), p.isSmsEnabled(), p.isWhatsappEnabled(), p.isEarthquakeAlerts(), p.isEmergencyAlerts(), p.isMassAlarmAlerts(), p.isLowBatteryAlerts(), p.isOfflineDeviceAlerts()); }
    private PushTokenResponse toResponse(PushToken t) { return new PushTokenResponse(t.getId(), t.getToken(), t.getPlatform(), t.getDeviceName(), t.isActive(), t.getCreatedAt()); }
    private NotificationResponse toResponse(NotificationLog n) { return new NotificationResponse(n.getId(), n.getNotificationType(), n.getChannel(), n.getRecipient(), n.getStatus(), n.getAttempts(), n.isSimulation(), n.getErrorMessage(), n.getCreatedAt(), java.util.Map.of()); }
}
