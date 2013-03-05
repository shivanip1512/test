package com.cannontech.database.db.capcontrol;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum MinCommunicationPercentageSettingName implements DatabaseRepresentationSource {
    MIN_COMM_PERCENTAGE("Mininum Communication Percentage"),
    ;

    private final String displayName;

    private MinCommunicationPercentageSettingName(String displayName) {
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