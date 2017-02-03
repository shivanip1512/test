package com.cannontech.system;

public enum KeyFileType {

    HONEYWELL("Honeywell");

    private String keyFileType;

    KeyFileType(String keyFileType) {
        this.keyFileType = keyFileType;
    }

    public String getKeyFileType() {
        return this.keyFileType;
    }
}
