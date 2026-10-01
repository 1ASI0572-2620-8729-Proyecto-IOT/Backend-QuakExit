package com.terraguard.quakexit.emergency.repository;

import com.terraguard.quakexit.common.enums.DomainEnums.EventStatus;
import com.terraguard.quakexit.emergency.entity.SeismicEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeismicEventRepository extends JpaRepository<SeismicEvent, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<SeismicEvent> {
    Optional<SeismicEvent> findFirstByDeviceIdAndStatusOrderByDetectedAtDesc(Long deviceId, EventStatus status);
}
