package com.cannontech.dr.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.YukonColorPalette;

public enum PerformanceVerificationMessageStatus implements DisplayableEnum {
    SUCCESS(YukonColorPalette.GREEN),
    SUCCESS_UNENROLLED(YukonColorPalette.GREEN),
    FAILURE(YukonColorPalette.ORANGE),
    UNKNOWN(YukonColorPalette.GRAY);

    private final YukonColorPalette color;
    
    private PerformanceVerificationMessageStatus(YukonColorPalette color) {
        this.color = color;
    }
    
    public YukonColorPalette getColor() {
        return color;
    }
    
    public String getHexColor() {
        return color.getHexValue();
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.rf.broadcast.eventDetail.status." + name();
    }

}
