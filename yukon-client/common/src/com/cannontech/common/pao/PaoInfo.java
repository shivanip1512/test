package com.cannontech.common.pao;

/**
 * Enum values to represent PaoInfo (ex. StaticPaoInfo.value)
 */
public enum PaoInfo {

    // RDS Transmitter PaoInfo values
    RDS_TRANSMITTER_IP_PORT,
    RDS_TRANSMITTER_IP_ADDRESS,
    RDS_TRANSMITTER_SITE_ADDRESS,
    RDS_TRANSMITTER_ENCODER_ADDRESS,
    RDS_TRANSMITTER_TRANSMIT_SPEED,
    RDS_TRANSMITTER_GROUP_TYPE,
    
    // Other....
    CPS_ONE_WAY_ENCRYPTION_KEY;
    
    private final String defaultValue;
    
    PaoInfo() {
        this.defaultValue = null;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
}
