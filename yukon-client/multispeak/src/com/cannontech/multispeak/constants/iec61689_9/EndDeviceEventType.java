package com.cannontech.multispeak.constants.iec61689_9;

/**
 * Codes as described by the standard "IEC 61968-9 – Interface Standard for Meter Reading &
 * Control [MR] [Published]".
 * 
 * http://en.wikipedia.org/wiki/IEC_61968
 */
public enum EndDeviceEventType {
    ALARM("1"),
    ALARM_MANAGEMENT("2"),
    ATTRIBUTE("3"),
    CALIBRATION("4"),
    CHECK_STATUS("5"),
    COMMAND("6"),
    CONFIGURATION("7"),
    CREDIT("8"),
    END_ALARM("9"),
    IDENTITY("10"),
    MAINT_MODE("11"),
    METERING_MODE("12"),
    OUTAGE("13"),
    QUALITY_FLAG("14"),
    READ_TYPE("15"),
    SETTING("16"),
    STATUS("17"),
    STATUS_CHECK("18"),
    TEST_MODE("19"),
    REQUIRES("20"),
    HAS_FEATURE("21"),
    ;

    public final String code;

    EndDeviceEventType(String code) {
        this.code = code;
    }
}
