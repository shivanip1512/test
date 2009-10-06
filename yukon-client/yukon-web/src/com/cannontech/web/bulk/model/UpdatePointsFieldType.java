package com.cannontech.web.bulk.model;


public enum UpdatePointsFieldType {
    EXPLICIT_MULTIPLIER("Set Multiplier"),
    ADJUSTED_MULTIPLIER("Multiply Multiplier"),
    DECIMAL_PLACES("Set Decimal Places");
    
    private final String displayValue;
    
    private UpdatePointsFieldType(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
}
