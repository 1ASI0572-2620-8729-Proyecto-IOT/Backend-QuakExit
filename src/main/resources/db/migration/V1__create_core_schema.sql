CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    phone_number VARCHAR(20) NULL,
    password_hash VARCHAR(255) NOT NULL,
    ownership_role VARCHAR(20) NULL,
    property_type VARCHAR(20) NULL,
    role VARCHAR(30) NOT NULL,
    enabled BIT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_email (email)
);

CREATE TABLE buildings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(160) NOT NULL,
    address VARCHAR(240) NOT NULL,
    district VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    owner_id BIGINT NOT NULL,
    total_floors INT NULL,
    PRIMARY KEY (id),
    INDEX idx_building_owner (owner_id),
    CONSTRAINT fk_building_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE devices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    device_code VARCHAR(80) NOT NULL,
    mac_address VARCHAR(17) NOT NULL,
    alias VARCHAR(120) NULL,
    status VARCHAR(20) NOT NULL,
    battery_percentage INT NULL,
    last_seen_at DATETIME(6) NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    firmware_version VARCHAR(40) NULL,
    component_updated_at DATETIME(6) NULL,
    building_id BIGINT NULL,
    owner_id BIGINT NULL,
    power_mode VARCHAR(20) NOT NULL,
    lock_status VARCHAR(20) NOT NULL,
    light_status VARCHAR(20) NOT NULL,
    siren_status VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_device_code (device_code),
    UNIQUE KEY uk_device_mac (mac_address),
    INDEX idx_device_building (building_id),
    INDEX idx_device_owner (owner_id),
    CONSTRAINT fk_device_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT fk_device_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE emergency_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    blood_type VARCHAR(20) NULL,
    allergies VARCHAR(1000) NULL,
    medical_conditions VARCHAR(1000) NULL,
    medications VARCHAR(1000) NULL,
    notes VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_emergency_profile_user (user_id),
    CONSTRAINT fk_emergency_profile_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE emergency_contacts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    relationship VARCHAR(80) NULL,
    priority INT NOT NULL,
    notify_by_sms BIT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_contact_user_phone (user_id, phone_number),
    CONSTRAINT fk_emergency_contact_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE property_layouts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    owner_id BIGINT NOT NULL,
    structure_json TINYTEXT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_layout_owner (owner_id),
    CONSTRAINT fk_property_layout_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE safe_zones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    max_capacity INT NOT NULL,
    current_occupancy INT NOT NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    floor_level INT NULL,
    building_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_safe_zone_building (building_id),
    CONSTRAINT fk_safe_zone_building FOREIGN KEY (building_id) REFERENCES buildings(id)
);

CREATE TABLE seismic_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    detected_at DATETIME(6) NOT NULL,
    resolved_at DATETIME(6) NULL,
    doors_unlocked_at DATETIME(6) NULL,
    peak_acceleration DOUBLE NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    false_alarm_reason VARCHAR(500) NULL,
    sms_notifications_sent INT NOT NULL,
    device_id BIGINT NOT NULL,
    building_id BIGINT NULL,
    cancelled_by BIGINT NULL,
    PRIMARY KEY (id),
    INDEX idx_event_status_detected (status, detected_at),
    INDEX idx_seismic_event_device (device_id),
    INDEX idx_seismic_event_building (building_id),
    INDEX idx_seismic_event_cancelled_by (cancelled_by),
    CONSTRAINT fk_seismic_event_device FOREIGN KEY (device_id) REFERENCES devices(id),
    CONSTRAINT fk_seismic_event_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT fk_seismic_event_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id)
);

CREATE TABLE seismic_readings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id BIGINT NOT NULL,
    timestamp DATETIME(6) NOT NULL,
    acceleration_x DOUBLE NOT NULL,
    acceleration_y DOUBLE NOT NULL,
    acceleration_z DOUBLE NOT NULL,
    magnitude_estimated DOUBLE NOT NULL,
    frequency_hz DOUBLE NULL,
    PRIMARY KEY (id),
    INDEX idx_reading_device_timestamp (device_id, timestamp),
    CONSTRAINT fk_seismic_reading_device FOREIGN KEY (device_id) REFERENCES devices(id)
);

CREATE TABLE simulations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    scheduled_at DATETIME(6) NULL,
    started_at DATETIME(6) NULL,
    finished_at DATETIME(6) NULL,
    triggered_by BIGINT NOT NULL,
    building_id BIGINT NULL,
    device_id BIGINT NULL,
    PRIMARY KEY (id),
    INDEX idx_simulation_triggered_by (triggered_by),
    INDEX idx_simulation_building (building_id),
    INDEX idx_simulation_device (device_id),
    CONSTRAINT fk_simulation_triggered_by FOREIGN KEY (triggered_by) REFERENCES users(id),
    CONSTRAINT fk_simulation_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT fk_simulation_device FOREIGN KEY (device_id) REFERENCES devices(id)
);

CREATE TABLE simulation_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    simulation_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    lock_response_time_ms BIGINT NULL,
    light_response_time_ms BIGINT NULL,
    success BIT NOT NULL,
    error_message VARCHAR(500) NULL,
    PRIMARY KEY (id),
    INDEX idx_simulation_result_simulation (simulation_id),
    INDEX idx_simulation_result_device (device_id),
    CONSTRAINT fk_simulation_result_simulation FOREIGN KEY (simulation_id) REFERENCES simulations(id),
    CONSTRAINT fk_simulation_result_device FOREIGN KEY (device_id) REFERENCES devices(id)
);
