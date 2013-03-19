package com.cannontech.database.db.capcontrol;

public enum CommReportingPercentageSettingType {
    CAPBANK(100.0),
    REGULATOR(100.0),
    VOLTAGE_MONITOR(100.0),
    ;

    private final double defaultValue;

    private CommReportingPercentageSettingType(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

}