CREATE TABLE building_units (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    unit_number VARCHAR(30) NOT NULL,
    building_id BIGINT NOT NULL,
    resident_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_building_unit_number (building_id, unit_number),
    INDEX idx_building_unit_resident (resident_id),
    CONSTRAINT fk_building_unit_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT fk_building_unit_resident FOREIGN KEY (resident_id) REFERENCES users(id)
);

ALTER TABLE devices
    ADD COLUMN unit_id BIGINT NULL,
    ADD INDEX idx_device_unit (unit_id),
    ADD CONSTRAINT fk_device_unit FOREIGN KEY (unit_id) REFERENCES building_units(id);
