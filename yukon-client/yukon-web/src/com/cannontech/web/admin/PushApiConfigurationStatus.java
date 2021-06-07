package com.cannontech.web.admin;

public enum PushAPIConfigurationStatus {

    SUCCESS("success"),
    FAILED("failed");

    private String value;

    PushAPIConfigurationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PushAPIConfigurationStatus of(String value) {
        for (PushAPIConfigurationStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No PushAPIConfigurationStatus for \"" + value + "\"");
    }
}