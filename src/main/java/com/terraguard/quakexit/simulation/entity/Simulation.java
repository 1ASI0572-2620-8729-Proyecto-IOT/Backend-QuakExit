package com.terraguard.quakexit.simulation.entity;

import com.terraguard.quakexit.b2b.entity.Building;
import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "simulations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Simulation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private SimulationType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private SimulationStatus status;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "building_id") private Building building;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "device_id") private Device device;
    @Column(name = "scheduled_at") private Instant scheduledAt;
    @Column(name = "started_at") private Instant startedAt;
    @Column(name = "finished_at") private Instant finishedAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "triggered_by", nullable = false) private User triggeredBy;
}
