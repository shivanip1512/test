package com.cannontech.common.rfn.util;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigDouble;

public class RfnFirmwareHelper {
    //  hide constructor
    private RfnFirmwareHelper() {
    }
    
    public static boolean isSupported(RfnFeatures feature, ConfigurationSource configurationSource) {
        return feature.isSupportedIn(configurationSource.getDouble(MasterConfigDouble.RFN_FIRMWARE));
    }
}
