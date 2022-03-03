package com.cannontech.dr.eatonCloud.model;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableMap;

public enum EatonCloudStopType {
    RESTORE(0),
    STOP_CYCLE(1);
    
    private final static ImmutableMap<Integer, EatonCloudStopType> lookupByValue;
    
    static {
        ImmutableMap.Builder<Integer, EatonCloudStopType> valueBuilder = ImmutableMap.builder();
        for (EatonCloudStopType stopType : values()) {
            valueBuilder.put(stopType.getValue(), stopType);
        }
        lookupByValue = valueBuilder.build();
    }

    private int value;

    private EatonCloudStopType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EatonCloudStopType of(int value) {
        var stopType = lookupByValue.get(value);
        checkArgument(stopType != null, value);
        return stopType;
    }

}
