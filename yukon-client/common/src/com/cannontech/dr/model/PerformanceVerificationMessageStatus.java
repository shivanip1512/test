package com.cannontech.dr.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PerformanceVerificationMessageStatus implements DisplayableEnum {
    SUCCESS("#093"),
    SUCCESS_UNENROLLED("#093"),
    FAILURE("#ec971f"),
    UNKNOWN("#888");

    private final String color;
    
    private PerformanceVerificationMessageStatus(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.rf.broadcast.eventDetail.status." + name();
    }

}
