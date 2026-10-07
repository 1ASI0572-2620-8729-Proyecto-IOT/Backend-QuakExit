package com.terraguard.quakexit.device.repository;

import com.terraguard.quakexit.device.entity.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Device> {
    Optional<Device> findByDeviceCode(String deviceCode);
    Optional<Device> findByMacAddress(String macAddress);
    boolean existsByDeviceCode(String deviceCode);
    java.util.List<Device> findByOwnerId(Long ownerId);
    java.util.List<Device> findByUnitId(Long unitId);
    java.util.List<Device> findByBuildingId(Long buildingId);
    @Query("select d from Device d where d.building.owner.id = :ownerId")
    java.util.List<Device> findByBuildingOwnerId(@Param("ownerId") Long ownerId);
}
