package com.cannontech.common.chart.model;

import com.cannontech.common.YukonColorPalette;

public enum ChartColorsEnum {

    GREEN(YukonColorPalette.GREEN),
    GREEN_FILL(YukonColorPalette.GREEN_LIGHT),
    LIGHT_BLUE(YukonColorPalette.BLUE_LIGHT),
    LIGHT_RED(YukonColorPalette.RED_LIGHT),
    ;
    
    private YukonColorPalette color;

    ChartColorsEnum(YukonColorPalette color) {
        this.color = color;
    }
    
    public YukonColorPalette getColor() {
        return this.color;
    }
    
    public String getColorHex() {
        return this.color.getHexValue();
    }
}
