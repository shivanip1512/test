package com.cannontech.common.rfn.util;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigDouble;

public class RfnFeatureHelper {
    //  hide constructor
    private RfnFeatureHelper() {
    }
    
    public static boolean isSupported(RfnFeature feature, ConfigurationSource configurationSource) {
        var disableSwitch = feature.getDisableSwitch();
        if (disableSwitch != null) {
            if (configurationSource.getBoolean(disableSwitch)) {
                return false;
            }
        }
        return feature.isSupportedIn(configurationSource.getDouble(MasterConfigDouble.RFN_FIRMWARE));
    }
}
