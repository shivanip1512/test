package com.cannontech.rest.api.gear.fields;

public enum MockItronCycleType {
    STANDARD("Standard Cycle"),
    TRUE_CYCLE("True Cycle"),
    SMART_CYCLE("Smart Cycle");
    
    private String name;
    
    private MockItronCycleType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static MockItronCycleType of(String name) {
        for(MockItronCycleType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Name is not a valid ItronCycleType");
    }
}
