CREATE TABLE IF NOT EXISTS audit_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    user_id BIGINT NULL,
    role VARCHAR(30) NULL,
    action VARCHAR(80) NOT NULL,
    entity_type VARCHAR(80) NULL,
    entity_id BIGINT NULL,
    property_id BIGINT NULL,
    building_id BIGINT NULL,
    detail_json TINYTEXT NULL,
    ip_address VARCHAR(64) NULL,
    PRIMARY KEY (id),
    INDEX idx_audit_created_at (created_at),
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_building (building_id),
    INDEX idx_audit_property (property_id),
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notification_preferences (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    push_enabled BIT NOT NULL,
    sms_enabled BIT NOT NULL,
    whatsapp_enabled BIT NOT NULL,
    earthquake_alerts BIT NOT NULL,
    emergency_alerts BIT NOT NULL,
    mass_alarm_alerts BIT NOT NULL,
    low_battery_alerts BIT NOT NULL,
    offline_device_alerts BIT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_notification_preferences_user (user_id),
    CONSTRAINT fk_notification_preferences_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS push_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    platform VARCHAR(20) NOT NULL,
    device_name VARCHAR(120) NULL,
    active BIT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_push_token (token),
    INDEX idx_push_token_user (user_id),
    CONSTRAINT fk_push_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notification_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    user_id BIGINT NULL,
    notification_type VARCHAR(60) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    recipient VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempts INT NOT NULL,
    simulation BIT NOT NULL,
    error_message VARCHAR(500) NULL,
    PRIMARY KEY (id),
    INDEX idx_notification_user_created (user_id, created_at),
    INDEX idx_notification_status (status),
    INDEX idx_notification_type (notification_type),
    CONSTRAINT fk_notification_log_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS maintenance_alerts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    type VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL,
    device_id BIGINT NOT NULL,
    building_id BIGINT NULL,
    current_value INT NULL,
    threshold INT NULL,
    description VARCHAR(500) NULL,
    acknowledged_by BIGINT NULL,
    acknowledged_at TIMESTAMP(6) NULL,
    resolved_by BIGINT NULL,
    resolved_at TIMESTAMP(6) NULL,
    resolution_note VARCHAR(500) NULL,
    PRIMARY KEY (id),
    INDEX idx_maintenance_status (status),
    INDEX idx_maintenance_device (device_id),
    INDEX idx_maintenance_building (building_id),
    CONSTRAINT fk_maintenance_device FOREIGN KEY (device_id) REFERENCES devices(id),
    CONSTRAINT fk_maintenance_ack_user FOREIGN KEY (acknowledged_by) REFERENCES users(id),
    CONSTRAINT fk_maintenance_resolve_user FOREIGN KEY (resolved_by) REFERENCES users(id)
);
