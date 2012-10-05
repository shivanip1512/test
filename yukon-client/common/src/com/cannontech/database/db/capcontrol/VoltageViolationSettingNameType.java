package com.cannontech.database.db.capcontrol;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum VoltageViolationSettingNameType implements DatabaseRepresentationSource {
    LOW_VOLTAGE_VIOLATION("Low Voltage Violation"),
    HIGH_VOLTAGE_VIOLATION("High Voltage Violation"),
    ;

    private final String displayName;

    private VoltageViolationSettingNameType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return displayName;
    }

}