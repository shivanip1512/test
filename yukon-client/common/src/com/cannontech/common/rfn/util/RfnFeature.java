package com.cannontech.common.rfn.util;

import static com.cannontech.common.rfn.util.RfnFirmwareVersion.FW_9_0;
import static com.cannontech.common.rfn.util.RfnFirmwareVersion.FW_9_4;

import com.cannontech.common.config.MasterConfigBoolean;

public enum RfnFeature {
    //  9.0
    AGGREGATE_MESSAGING(FW_9_0),
    DEVICE_CONFIG_NOTIFICATION(FW_9_0),
    RESIDENTIAL_DEMAND_INTERVAL_CONFIGURATION(FW_9_0),
    //  9.4
    E2E_DISCONNECT(FW_9_4, MasterConfigBoolean.DISABLE_E2E_METER_DISCONNECTS),
    E2E_READ_NOW(FW_9_4, MasterConfigBoolean.DISABLE_E2E_METER_READS),
    METROLOGY_ENABLE_DISABLE(FW_9_4),
    ;
    
    private RfnFirmwareVersion startVersion;
    private MasterConfigBoolean disableSwitch;
    
    RfnFeature(RfnFirmwareVersion startVersion) {
        this(startVersion, null);
    }

    RfnFeature(RfnFirmwareVersion startVersion, MasterConfigBoolean disableSwitch) {
        this.startVersion = startVersion;
        this.disableSwitch = disableSwitch;
    }

    public MasterConfigBoolean getDisableSwitch() {
        return disableSwitch;
    }
    
    public boolean isSupportedIn(Double version) {
        return startVersion.lessOrEqualTo(version);
    }
}
