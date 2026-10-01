package com.terraguard.quakexit.simulation.repository;

import com.terraguard.quakexit.simulation.entity.SimulationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationResultRepository extends JpaRepository<SimulationResult, Long> {}
