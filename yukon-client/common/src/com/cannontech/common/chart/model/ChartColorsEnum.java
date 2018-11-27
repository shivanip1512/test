package com.cannontech.common.chart.model;

public enum ChartColorsEnum {

	GREEN("#009933"), 
    BLUE("#0000FF"), 
    LIGHT_BLUE("#849FBB"), 
    RED("#FF0000"), 
    LIGHT_RED("#C77D7D"), 
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
