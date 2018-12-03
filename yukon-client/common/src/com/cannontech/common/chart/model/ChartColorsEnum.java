package com.cannontech.common.chart.model;

public enum ChartColorsEnum {

	GREEN("#009933"), 
    BLUE("#0000FF"), 
    LIGHT_BLUE("#6587AA"), 
    RED("#FF0000"), 
    LIGHT_RED("#B95D5D"), 
    YELLOW("#CC9900"),
    GREEN_FILL("#97D5AC")
    ;
    
    private String rgb;

    ChartColorsEnum(String rgb) {
        this.rgb = rgb;
    }
    
    public String getRgb() {
        return this.rgb;
    }
}
