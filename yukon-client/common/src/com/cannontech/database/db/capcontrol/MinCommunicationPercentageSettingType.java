package com.cannontech.database.db.capcontrol;

public enum MinCommunicationPercentageSettingType {
    IVVC_BANKS_REPORTING_RATIO(100.0),
    IVVC_REGULATOR_REPORTING_RATIO(100.0),
    IVVC_VOLTAGEMONITOR_REPORTING_RATIO(100.0),
    ;

    private final double defaultValue;

    private MinCommunicationPercentageSettingType(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

}