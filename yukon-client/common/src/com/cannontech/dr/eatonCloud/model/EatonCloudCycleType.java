package com.cannontech.dr.eatonCloud.model;

public enum EatonCloudCycleType {
    STANDARD("Standard Cycle", 0),
    TRUE_CYCLE("True Cycle", 1),
    SMART_CYCLE("Smart Cycle", 2);

    private String name;
    private int value;

    private EatonCloudCycleType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }

    public static EatonCloudCycleType of(String name) {
        for (EatonCloudCycleType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Name is not a valid EatonCloudCycleType");
    }
    
    public static EatonCloudCycleType of(int value) {
        for (EatonCloudCycleType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Value is not a valid EatonCloudCycleType");
    }
}