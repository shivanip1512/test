package com.cannontech.web.admin;

public enum PushApiConfigurationStatus {

    SUCCESS("success"),
    FAILED("failed");

    private String value;

    PushApiConfigurationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PushApiConfigurationStatus of(String value) {
        for (PushApiConfigurationStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No PushApiConfigurationStatus for \"" + value + "\"");
    }
}