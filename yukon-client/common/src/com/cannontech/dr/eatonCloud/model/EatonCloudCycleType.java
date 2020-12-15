package com.cannontech.dr.eatonCloud.model;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableMap;

public enum EatonCloudCycleType {
    STANDARD("Standard Cycle", 0),
    TRUE_CYCLE("True Cycle", 1),
    SMART_CYCLE("Smart Cycle", 2);

    private final static ImmutableMap<Integer, EatonCloudCycleType> lookupByValue;
    private final static ImmutableMap<String, EatonCloudCycleType> lookupByName;
    
    static {
        ImmutableMap.Builder<Integer, EatonCloudCycleType> idBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, EatonCloudCycleType> nameBuilder = ImmutableMap.builder();
        for (EatonCloudCycleType cycleType : values()) {
            idBuilder.put(cycleType.value, cycleType);
            nameBuilder.put(cycleType.name, cycleType);
        }
        lookupByValue = idBuilder.build();
        lookupByName = nameBuilder.build();
    }

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
        EatonCloudCycleType cycleType = lookupByName.get(name);
        checkArgument(cycleType != null, name);
        return cycleType;
    }
    
    public static EatonCloudCycleType of(int value) {
        EatonCloudCycleType cycleType = lookupByValue.get(value);
        checkArgument(cycleType != null, value);
        return cycleType;
    }
}