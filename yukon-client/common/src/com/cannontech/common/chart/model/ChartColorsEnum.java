package com.cannontech.common.chart.model;

public enum ChartColorsEnum {

	GREEN("#009933"), 
    BLUE("#0000FF"), 
    RED("#FF0000"), 
    YELLOW("#CC9900"), 
    ;
    
    private String rgb;

    ChartColorsEnum(String rgb) {
        this.rgb = rgb;
    }
    
    public String getRgb() {
        return this.rgb;
    }
}
