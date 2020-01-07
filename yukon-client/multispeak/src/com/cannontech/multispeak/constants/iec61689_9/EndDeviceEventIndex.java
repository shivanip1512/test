package com.cannontech.multispeak.constants.iec61689_9;

/**
 * Codes as described by the standard "IEC 61968-9 - Interface Standard for Meter Reading &
 * Control [MR] [Published]".
 * 
 * http://en.wikipedia.org/wiki/IEC_61968
 * 
 * This list is incomplete.
 */
public enum EndDeviceEventIndex {
    LOW_LIMIT("150"),
    MOMENTARY_EVENTS("166"),
    RESET("214");

    public final String code;

    EndDeviceEventIndex(String code) {
        this.code = code;
    }
}
