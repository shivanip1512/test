package com.cannontech.core.authorization.support;

public enum PaoPermission {

    GET_COMMAND("Device 'get' command"), PUT_COMMAND("Device 'put' command"), CONTROL_COMMAND(
            "Device 'control' command"), OTHER_COMMAND("Unrecognized device command");

    private final String description;

    private PaoPermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
