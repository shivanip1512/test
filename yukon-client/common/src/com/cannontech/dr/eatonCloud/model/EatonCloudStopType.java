package com.cannontech.dr.eatonCloud.model;

public enum EatonCloudStopType {
    RESTORE(0),
    STOP_CYCLE(1);

    private int value;

    private EatonCloudStopType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EatonCloudStopType of(int value) {
        for (EatonCloudStopType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Value is not a valid EatonCloudStopType");
    }

}
