package com.cannontech.dr.rfn.model;

import java.util.Arrays;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Possible response type values that Power Quality Response events can have in the event log blob in the LCR TLV report.
 * Values are specified in the GridBallast Firmware Design document (section 5.2.1: Response Type) at
 * http://customsp.etn.com/es/EASTeamSite/ProLaunch_Template_Rev01/NODES/Requirements/Grid%20Ballast%20Firmware%20Design.docx
 */
public enum PqrResponseType implements DisplayableEnum {
    UNDER_VOLTAGE(0x00),
    OVER_VOLTAGE(0x01),
    UNDER_FREQUENCY(0x02),
    OVER_FREQUENCY(0x03),
    ;
    
    private final byte value;
    
    private PqrResponseType(int value) {
        this.value = (byte) value;
    }
    
    public byte getValue() {
        return value;
    }
    
    public static PqrResponseType of(byte value) {
        return Arrays.stream(values())
                     .filter(type -> type.value == value)
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("No PqrResponseType for value: " + value));
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.operator.pqrReport.responseType." + name();
    }
}
