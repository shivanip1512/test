package com.cannontech.common.chart.model;

public enum ChartColorsEnum {

	GREEN("#009933"), 
    BLUE("#0000FF"), 
    LIGHT_BLUE("#3F6995"), 
    RED("#FF0000"), 
    LIGHT_RED("#A83535"), 
    YELLOW("#CC9900"),
    GREEN_FILL("#DDB67E")
    ;
    
    private String rgb;

    ChartColorsEnum(String rgb) {
        this.rgb = rgb;
    }
    
    public String getRgb() {
        return this.rgb;
    }
}
