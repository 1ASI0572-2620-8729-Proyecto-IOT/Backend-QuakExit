package com.terraguard.quakexit.common.enums;

public final class DomainEnums {
    private DomainEnums() {}

    public enum Role { HOMEOWNER, B2B_ADMIN, BUILDING_ADMIN, SYSTEM_ADMIN }
    public enum PropertyType { HOUSE, APARTMENT }
    public enum OwnershipRole { OWNER, RENTER, BUILDING_ADMIN }
    public enum DeviceStatus { ONLINE, OFFLINE, ALERT, SLEEPING }
    public enum PowerMode { NORMAL, DEEP_SLEEP }
    public enum LockStatus { LOCKED, UNLOCKED, FAULT, UNKNOWN }
    public enum LightStatus { OFF, ON, FAULT, UNKNOWN }
    public enum AlarmStatus { INACTIVE, ACTIVE, FAULT }
    public enum EventStatus { DETECTED, ACTIVE, RESOLVED, FALSE_ALARM }
    public enum Severity { LOW, MODERATE, HIGH, CRITICAL }
    public enum SimulationType { MANUAL, SCHEDULED }
    public enum SimulationStatus { PENDING, RUNNING, COMPLETED, FAILED, CANCELLED }
}
