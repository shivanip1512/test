package com.cannontech.simplereport;

public enum SortType {
    
    INTEGER("int"),
    FLOAT("float"),
    DATE("date"),
    TEXT("text"),
    FUNCTION("funciton");
    
    private String type;
    
    private SortType(String type) {
        this.type = type;
    }
    
    public String getSortType() {
        return type;
    }
    
}