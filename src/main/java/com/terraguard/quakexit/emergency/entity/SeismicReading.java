package com.terraguard.quakexit.emergency.entity;

import com.terraguard.quakexit.device.entity.Device;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "seismic_readings", indexes = @Index(name = "idx_reading_device_timestamp", columnList = "device_id,timestamp"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeismicReading {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "device_id", nullable = false) private Device device;
    @Column(nullable = false) private Instant timestamp;
    @Column(name = "acceleration_x", nullable = false) private double accelerationX;
    @Column(name = "acceleration_y", nullable = false) private double accelerationY;
    @Column(name = "acceleration_z", nullable = false) private double accelerationZ;
    @Column(name = "magnitude_estimated", nullable = false) private double magnitudeEstimated;
    @Column(name = "frequency_hz") private Double frequencyHz;
}
