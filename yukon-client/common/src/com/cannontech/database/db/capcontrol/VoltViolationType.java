package com.cannontech.database.db.capcontrol;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum VoltViolationType implements DatabaseRepresentationSource {
    LOW_VOLTAGE_VIOLATION("Low Voltage Violation"),
    HIGH_VOLTAGE_VIOLATION("High Voltage Violation"),
    ;

    private final String displayName;

    private VoltViolationType(String displayName) {
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