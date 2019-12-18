package com.cannontech.services.systemDataPublisher.yaml.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum to define List of IOT data type.
 */
public enum IOTDataType {
    TELEMETRY("Telemetry"),
    PROPERTY("Property"),
    BOTH("Both");

    @JsonValue
    private String name;

    private IOTDataType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
