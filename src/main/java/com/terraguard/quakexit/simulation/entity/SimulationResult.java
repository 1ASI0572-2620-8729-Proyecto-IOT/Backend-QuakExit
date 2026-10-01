package com.terraguard.quakexit.simulation.entity;

import com.terraguard.quakexit.device.entity.Device;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "simulation_results")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SimulationResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "simulation_id", nullable = false) private Simulation simulation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "device_id", nullable = false) private Device device;
    @Column(name = "lock_response_time_ms") private Long lockResponseTimeMs;
    @Column(name = "light_response_time_ms") private Long lightResponseTimeMs;
    @Column(nullable = false) private boolean success;
    @Column(name = "error_message", length = 500) private String errorMessage;
}
