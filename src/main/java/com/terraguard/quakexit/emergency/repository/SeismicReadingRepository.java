package com.terraguard.quakexit.emergency.repository;

import com.terraguard.quakexit.emergency.entity.SeismicReading;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeismicReadingRepository extends JpaRepository<SeismicReading, Long> {
    List<SeismicReading> findByDeviceIdAndTimestampAfterOrderByTimestampDesc(Long deviceId, Instant timestamp);
}
