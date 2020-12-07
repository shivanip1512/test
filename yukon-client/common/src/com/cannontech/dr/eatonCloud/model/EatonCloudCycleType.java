package com.cannontech.dr.eatonCloud.model;

public enum EatonCloudCycleType {
    STANDARD("Standard Cycle"),
    TRUE_CYCLE("True Cycle"),
    SMART_CYCLE("Smart Cycle");

    private String name;

    private EatonCloudCycleType(String name) {
        this.name = name;
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
}