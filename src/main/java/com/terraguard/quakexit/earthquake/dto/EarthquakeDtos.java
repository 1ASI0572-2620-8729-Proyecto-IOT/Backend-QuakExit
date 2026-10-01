package com.terraguard.quakexit.earthquake.dto;

import java.time.Instant;
import java.util.List;

public final class EarthquakeDtos {
    private EarthquakeDtos() {}
    public record EarthquakeResponse(List<EarthquakeItem> earthquakes, Instant fetchedAt) {}
    public record EarthquakeItem(String eventId, Instant occurredAt, double magnitude, String place, Double depthKm, Double latitude, Double longitude) {}
}
