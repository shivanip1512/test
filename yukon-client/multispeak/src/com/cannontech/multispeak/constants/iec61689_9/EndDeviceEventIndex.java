package com.cannontech.multispeak.constants.iec61689_9;

/**
 * Codes as described by IEC 61698-9. This list is incomplete.
 */
public enum EndDeviceEventIndex {
    RESET("214"),
    ;

    public final String code;

    EndDeviceEventIndex(String code) {
        this.code = code;
    }
}
