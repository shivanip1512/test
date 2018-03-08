package com.cannontech.dr.rfn.model;

import java.util.Arrays;

/**
 * Possible types of Power Quality Response events that can be returned in the event log blob in the LCR TLV report.
 * Values are specified in the GridBallast Firmware Design document (section 5.2: Log Entries) at
 * http://customsp.etn.com/es/EASTeamSite/ProLaunch_Template_Rev01/NODES/Requirements/Grid%20Ballast%20Firmware%20Design.docx
 */
public enum PqrEventType {
    RESPONSE_DISABLED(0x01),
    EVENT_DETECTED(0x02),
    NOT_ACTIVATED(0x03),
    NO_DR_EVENT(0x04),
    EVENT_SUSTAINED(0x05),
    EVENT_RELEASE_MIN(0x06),
    EVENT_RELEASE(0x07),
    EVENT_RELEASE_MAX(0x08),
    EVENT_COMPLETE(0x09),
    RESTORE_DETECTED(0x0A),
    ;
    
    private final byte value;
    
    private PqrEventType(int value) {
        this.value = (byte) value;
    }
    
    public byte getValue() {
        return value;
    }
    
    public static PqrEventType of(byte value) {
        return Arrays.stream(values())
                     .filter(type -> type.value == value)
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("No PqrEventType for value: " + value));
    }
}
