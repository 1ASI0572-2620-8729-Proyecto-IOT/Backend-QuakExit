package com.terraguard.quakexit.maintenance.repository;

import com.terraguard.quakexit.maintenance.entity.MaintenanceAlert;
import org.springframework.data.jpa.repository.*;

public interface MaintenanceAlertRepository extends JpaRepository<MaintenanceAlert, Long>, JpaSpecificationExecutor<MaintenanceAlert> {
    boolean existsByDeviceIdAndTypeAndStatus(Long deviceId, String type, String status);
}
