package com.cannontech.common.rfn.util;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigDouble;

public class RfnFeatureHelper {
    //  hide constructor
    private RfnFeatureHelper() {
    }
    
    public static boolean isSupported(RfnFeature feature, ConfigurationSource configurationSource) {
        return feature.isSupportedIn(configurationSource.getDouble(MasterConfigDouble.RFN_FIRMWARE));
    }
}
