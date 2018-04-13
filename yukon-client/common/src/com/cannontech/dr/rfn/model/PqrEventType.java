package com.cannontech.dr.rfn.model;

import java.util.Arrays;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Possible types of Power Quality Response events that can be returned in the event log blob in the LCR TLV report.
 * Values are specified in the GridBallast Firmware Design document (section 5.2: Log Entries) at
 * http://customsp.etn.com/es/EASTeamSite/ProLaunch_Template_Rev01/NODES/Requirements/Grid%20Ballast%20Firmware%20Design.docx
 */
public enum PqrEventType implements DisplayableEnum {
    RESPONSE_DISABLED(0x01), // OV/OF condition occurred and persisted through wait period, but PQR is disabled
    EVENT_DETECTED(0x02), // OV/OF condition occurred and was still present after activation wait period
    NOT_ACTIVATED(0x03), // OV/OF condition occurred, but was back below threshold after activation wait period
    NO_DR_EVENT(0x04), // OV/OF condition occurred, but no active DR event to inhibit
    EVENT_SUSTAINED(0x05), // OV/OF condition occurred and persisted through wait period. Active DR event will be inhibited
    EVENT_RELEASE_MIN(0x06), // PQR event active, OV/OF condition ended, but still need to wait for minimum control time before event ends.
    EVENT_RELEASE(0x07), // PQR event active, OV/OF condition ended, event will end (after randomization)
    EVENT_RELEASE_MAX(0x08), // PQR event ran for maximum allowed control time
    EVENT_COMPLETE(0x09), // PQR event occurred, OV/OF condition ended, deactivation randomization occurred, PQR event ended. (DR control resumes)
    RESTORE_DETECTED(0x0A), // PQR event active, OV/OF condition ended, and wait period completed.
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

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.operator.pqrReport.eventType." + name();
    }
}
