package com.terraguard.quakexit.simulation.repository;

import com.terraguard.quakexit.common.enums.DomainEnums.SimulationStatus;
import com.terraguard.quakexit.simulation.entity.Simulation;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationRepository extends JpaRepository<Simulation, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Simulation> {
    List<Simulation> findByStatusAndScheduledAtLessThanEqual(SimulationStatus status, Instant at);
}
