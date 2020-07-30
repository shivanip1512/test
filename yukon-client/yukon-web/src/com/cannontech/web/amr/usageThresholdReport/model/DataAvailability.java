package com.cannontech.web.amr.usageThresholdReport.model;

import com.cannontech.common.YukonColorPalette;

public enum DataAvailability {
    
    COMPLETE(YukonColorPalette.GREEN.getHexValue()), 
    PARTIAL(YukonColorPalette.BLUE.getHexValue()),
    NONE(YukonColorPalette.ORANGE.getHexValue()),
    UNSUPPORTED(YukonColorPalette.GRAY.getHexValue());
    
    private DataAvailability(String color) {
        this.color = color;
    }

    private final String color;
    
    public String getColor() {
        return color;
    }

}
