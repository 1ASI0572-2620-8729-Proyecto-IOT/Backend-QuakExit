package com.terraguard.quakexit.device.repository;

import com.terraguard.quakexit.device.entity.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Device> {
    Optional<Device> findByDeviceCode(String deviceCode);
    Optional<Device> findByMacAddress(String macAddress);
    boolean existsByDeviceCode(String deviceCode);
    java.util.List<Device> findByOwnerId(Long ownerId);
    java.util.List<Device> findByBuildingId(Long buildingId);
}
