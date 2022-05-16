package com.cannontech.multispeak.constants.iec61689_9;

/**
 * Codes as described by the standard "IEC 61968-9 - Interface Standard for Meter Reading &
 * Control [MR] [Published]".
 * 
 * http://en.wikipedia.org/wiki/IEC_61968
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
    WATER_METER("24")
    ;

    public final String code;

    EndDeviceEventDomain(String code) {
        this.code = code;
    }
}
