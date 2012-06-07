package com.cannontech.stars.dr.enrollment.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum EnrollmentEnum implements DisplayableEnum {
    ENROLL,
    UNENROLL;

    private final static String keyPrefix = "yukon.dr.program.enrollmentType.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
