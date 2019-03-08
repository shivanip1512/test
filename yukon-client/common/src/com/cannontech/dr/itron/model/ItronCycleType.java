package com.cannontech.dr.itron.model;

public enum ItronCycleType {
    STANDARD("Standard Cycle"),
    TRUE_CYCLE("True Cycle"),
    SMART_CYCLE("Smart Cycle");
    
    private String name;
    
    private ItronCycleType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static ItronCycleType of(String name) {
        for(ItronCycleType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Name is not a valid ItronCycleType");
    }
}
