package com.cannontech.stars.dr.enrollment.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.DisplayableShortEnum;

public enum EnrollmentInService implements DisplayableEnum, DisplayableShortEnum {
    INSERVICE("yukon.web.modules.operator.hardwareConfig.inService"),
    OUTOFSERVICE("yukon.web.modules.operator.hardwareConfig.outOfService"),
    NA("yukon.web.modules.operator.hardwareConfig.notApplicable");
    
    private final String key;
    
    private EnrollmentInService(String key) {
        this.key = key;
    }
    
    static public EnrollmentInService determineInService(boolean isInService) {
        if ( isInService) {
            return INSERVICE;
        }
        return OUTOFSERVICE;
    }

    @Override
    public String getFormatKey() {
        return key;
    }
    
    @Override
    public String getShortFormatKey() {
        return key + ".short";
    }
}
