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
    CPS_ONE_WAY_ENCRYPTION_KEY(null);
    
    private final boolean defaultable;
    private final String defaultValue;
    
    PaoInfo() {
        this.defaultable = false;
        this.defaultValue = null;
    }
    
    PaoInfo(String defaultValue) {
        this.defaultable = true;
        this.defaultValue = defaultValue;
    }
    
    public boolean isDefaultable() {
        return defaultable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    
}
