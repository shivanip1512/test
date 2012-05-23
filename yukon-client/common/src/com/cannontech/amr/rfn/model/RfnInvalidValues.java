package com.cannontech.amr.rfn.model;

public enum RfnInvalidValues {
    OUTAGE_DURATION(-1);

    private final long value;

    private RfnInvalidValues(int value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
