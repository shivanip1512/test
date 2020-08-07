package com.cannontech.web.amr.usageThresholdReport.model;

import com.cannontech.common.YukonColorPalette;

public enum DataAvailability {
    
    COMPLETE(YukonColorPalette.GREEN), 
    PARTIAL(YukonColorPalette.BLUE),
    NONE(YukonColorPalette.ORANGE),
    UNSUPPORTED(YukonColorPalette.GRAY);
    
    private DataAvailability(YukonColorPalette color) {
        this.color = color;
    }

    private final YukonColorPalette color;
    
    public YukonColorPalette getColor() {
        return color;
    }
    
    public String getColorHex() {
        return color.getHexValue();
    }

}
