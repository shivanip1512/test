package com.cannontech.web.amr.usageThresholdReport.model;

import com.cannontech.common.YukonColorPallet;

public enum DataAvailability {
    
    COMPLETE(YukonColorPallet.GREEN.getHexValue()), 
    PARTIAL(YukonColorPallet.BLUE.getHexValue()),
    NONE(YukonColorPallet.ORANGE.getHexValue()),
    UNSUPPORTED(YukonColorPallet.GRAY.getHexValue());
    
    private DataAvailability(String color) {
        this.color = color;
    }

    private final String color;
    
    public String getColor() {
        return color;
    }

}
