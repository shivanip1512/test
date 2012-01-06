package com.cannontech.common.fdr;

public enum FdrOptionType {
    TEXT("Text"),
    COMBO("Combo"),
    ;
    
    private final String typeString;
    
    private FdrOptionType(String typeString) {
        this.typeString = typeString;
    }
    
    public String getTypeString() {
        return typeString;
    }
}
