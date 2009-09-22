package com.cannontech.web.bulk.model;


public enum UpdatePointsFieldType {
    EXPLICIT_MULTIPLIER("Explicit Set Multiplier"),
    ADJUSTED_MULTIPLIER("Multiply The Current Multiplier Value"),
    DECIMAL_PLACES("Decimal Places");
    
    private final String displayValue;
    
    private UpdatePointsFieldType(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
}
