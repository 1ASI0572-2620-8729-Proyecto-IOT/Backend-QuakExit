package com.terraguard.quakexit.report.service;

import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.emergency.entity.SeismicEvent;
import com.terraguard.quakexit.emergency.repository.SeismicEventRepository;
import com.terraguard.quakexit.simulation.entity.Simulation;
import com.terraguard.quakexit.simulation.repository.SimulationRepository;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class ReportService {
    private final SeismicEventRepository events;
    private final SimulationRepository simulations;
    private final DeviceRepository devices;

    public Page<Map<String, Object>> earthquakes(Long propertyId, Long buildingId, Instant from, Instant to, String status, Double minMagnitude, Double maxMagnitude, Pageable pageable) {
        Specification<SeismicEvent> spec = Specification.where(ReportService.<SeismicEvent>equal("building.id", buildingId)).and(ReportService.<SeismicEvent>equal("status", status == null ? null : EventStatus.valueOf(status)))
            .and(from == null ? null : (r, q, c) -> c.greaterThanOrEqualTo(r.<Instant>get("detectedAt"), from))
            .and(to == null ? null : (r, q, c) -> c.lessThanOrEqualTo(r.<Instant>get("detectedAt"), to))
            .and(minMagnitude == null ? null : (r, q, c) -> c.greaterThanOrEqualTo(r.<Double>get("peakAcceleration"), minMagnitude))
            .and(maxMagnitude == null ? null : (r, q, c) -> c.lessThanOrEqualTo(r.<Double>get("peakAcceleration"), maxMagnitude));
        return events.findAll(spec, pageable).map(this::eventMap);
    }

    public Page<Map<String, Object>> simulations(Long buildingId, Instant from, Instant to, String status, Pageable pageable) {
        Specification<Simulation> spec = Specification.where(ReportService.<Simulation>equal("building.id", buildingId)).and(ReportService.<Simulation>equal("status", status == null ? null : SimulationStatus.valueOf(status)))
            .and(from == null ? null : (r, q, c) -> c.greaterThanOrEqualTo(r.<Instant>get("startedAt"), from))
            .and(to == null ? null : (r, q, c) -> c.lessThanOrEqualTo(r.<Instant>get("startedAt"), to));
        return simulations.findAll(spec, pageable).map(this::simulationMap);
    }

    public Page<Map<String, Object>> devices(Long buildingId, String status, String powerMode, Boolean lowBattery, Boolean disconnected, Pageable pageable) {
        Specification<Device> spec = Specification.where(ReportService.<Device>equal("building.id", buildingId)).and(ReportService.<Device>equal("status", status == null ? null : DeviceStatus.valueOf(status))).and(ReportService.<Device>equal("powerMode", powerMode == null ? null : PowerMode.valueOf(powerMode)))
            .and(lowBattery == null ? null : (r, q, c) -> lowBattery ? c.lessThanOrEqualTo(r.<Integer>get("batteryPercentage"), 20) : c.greaterThan(r.<Integer>get("batteryPercentage"), 20))
            .and(disconnected == null ? null : (r, q, c) -> disconnected ? c.or(c.isNull(r.get("lastSeenAt")), c.lessThan(r.<Instant>get("lastSeenAt"), Instant.now().minus(Duration.ofHours(1)))) : c.greaterThanOrEqualTo(r.<Instant>get("lastSeenAt"), Instant.now().minus(Duration.ofHours(1))));
        return devices.findAll(spec, pageable).map(this::deviceMap);
    }

    public Page<Map<String, Object>> falseAlarms(Long buildingId, Long deviceId, Instant from, Instant to, Pageable pageable) {
        Specification<SeismicEvent> spec = Specification.where(ReportService.<SeismicEvent>equal("building.id", buildingId)).and(ReportService.<SeismicEvent>equal("device.id", deviceId)).and(ReportService.<SeismicEvent>equal("status", EventStatus.FALSE_ALARM))
            .and(from == null ? null : (r, q, c) -> c.greaterThanOrEqualTo(r.<Instant>get("detectedAt"), from)).and(to == null ? null : (r, q, c) -> c.lessThanOrEqualTo(r.<Instant>get("detectedAt"), to));
        return events.findAll(spec, pageable).map(e -> Map.of("eventId", e.getId(), "deviceId", e.getDevice().getId(), "reason", Objects.requireNonNullElse(e.getFalseAlarmReason(), ""), "detectedAt", e.getDetectedAt()));
    }

    public Map<String, Object> summary(Long buildingId, Instant from, Instant to) {
        Instant start = from == null ? Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS) : from;
        Instant end = to == null ? Instant.now() : to;
        long totalEvents = events.count((root, query, cb) -> cb.and(buildingId == null ? cb.conjunction() : cb.equal(root.get("building").get("id"), buildingId), cb.between(root.get("detectedAt"), start, end)));
        long activeAlerts = events.count((root, query, cb) -> cb.and(buildingId == null ? cb.conjunction() : cb.equal(root.get("building").get("id"), buildingId), cb.equal(root.get("status"), EventStatus.ACTIVE)));
        long activeDevices = devices.count((root, query, cb) -> cb.and(buildingId == null ? cb.conjunction() : cb.equal(root.get("building").get("id"), buildingId), cb.equal(root.get("status"), DeviceStatus.ONLINE)));
        return Map.of("activeDevices", activeDevices, "activeAlerts", activeAlerts, "totalEvents", totalEvents, "from", start, "to", end, "eventsByDay", List.of());
    }

    private static <T> Specification<T> equal(String field, Object value) { return value == null ? null : (root, query, cb) -> { jakarta.persistence.criteria.Path<?> path = root; for (String part : field.split("\\.")) path = path.get(part); return cb.equal(path, value); }; }
    private Map<String, Object> eventMap(SeismicEvent e) { Map<String, Object> map = new LinkedHashMap<>(); map.put("eventId", e.getId()); map.put("deviceId", e.getDevice().getId()); map.put("buildingId", e.getBuilding() == null ? null : e.getBuilding().getId()); map.put("status", e.getStatus()); map.put("severity", e.getSeverity()); map.put("peakAcceleration", e.getPeakAcceleration()); map.put("detectedAt", e.getDetectedAt()); return map; }
    private Map<String, Object> simulationMap(Simulation s) { Map<String, Object> map = new LinkedHashMap<>(); map.put("simulationId", s.getId()); map.put("type", s.getType()); map.put("status", s.getStatus()); map.put("buildingId", s.getBuilding() == null ? null : s.getBuilding().getId()); map.put("startedAt", s.getStartedAt()); map.put("finishedAt", s.getFinishedAt()); return map; }
    private Map<String, Object> deviceMap(Device d) { Map<String, Object> map = new LinkedHashMap<>(); map.put("deviceId", d.getId()); map.put("deviceCode", d.getDeviceCode()); map.put("alias", Objects.requireNonNullElse(d.getAlias(), "")); map.put("buildingId", d.getBuilding() == null ? null : d.getBuilding().getId()); map.put("status", d.getStatus()); map.put("batteryPercentage", d.getBatteryPercentage()); map.put("powerMode", d.getPowerMode()); map.put("lastSeenAt", d.getLastSeenAt()); return map; }
}
