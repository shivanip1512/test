package com.cannontech.multispeak.deploy.service;

/**
 * Codes as described by IEC 61698-9.
 */
public enum EndDeviceEventDomain {
    COMMUNICATIONS("1"),
    DEVICE_ASSET("2"),
    ELECTRICT_METER("3"),
    GAS_METER("4"),
    GATEWAY("5"),
    GRID_POWER("6"),
    SECURITY("7"),
    TRANSFORMER("8"),
    TURBINE("9"),
    ;

    public final String code;

    EndDeviceEventDomain(String code) {
        this.code = code;
    }
}
