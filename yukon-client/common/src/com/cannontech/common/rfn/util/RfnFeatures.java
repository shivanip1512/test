package com.cannontech.common.rfn.util;

public enum RfnFeatures {
    METROLOGY_ENABLE_DISABLE(RfnFirmwareVersion.FW_9_4),
    E2E_READ_NOW(RfnFirmwareVersion.FW_9_4),
    E2E_DISCONNECT(RfnFirmwareVersion.FW_9_4),
    ;
    
    private RfnFirmwareVersion startVersion;
    
    RfnFeatures(RfnFirmwareVersion startVersion) {
        this.startVersion = startVersion;
    }

    public boolean isSupportedIn(Double version) {
        return startVersion.lessOrEqualTo(version);
    }
}
