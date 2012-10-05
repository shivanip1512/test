package com.cannontech.database.db.capcontrol;

public enum VoltageViolationSettingType {
    BANDWIDTH(3, 1),
    COST(-10, 70),
    EMERGENCY_COST(-150, 300),
    ;

    private final double defaultLowValue;
    private final double defaultHighValue;

    private VoltageViolationSettingType(double defaultLowValue, double defaultHighValue) {
        this.defaultLowValue = defaultLowValue;
        this.defaultHighValue = defaultHighValue;
    }

    public double getDefaultLowValue() {
        return defaultLowValue;
    }

    public double getDefaultHighValue() {
        return defaultHighValue;
    }

}