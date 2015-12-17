package com.cannontech.stars.dr.program.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ProgramEnrollmentResultEnum implements DisplayableEnum {
    SUCCESS,
    SUCCESS_HARDWARE_CONFIG;

    private static final String keyPrefix = "yukon.dr.consumer.enrollment.result.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

}
