package com.cannontech.database.db.capcontrol;

public enum PowerFactorCorrectionSettingType {
    BANDWIDTH(0.02),
    COST(20.0),
    MAX_COST(2.0),
    ;

    private final double defaultValue;

    private PowerFactorCorrectionSettingType(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

}