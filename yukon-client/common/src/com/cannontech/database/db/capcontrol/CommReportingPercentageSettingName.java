package com.cannontech.database.db.capcontrol;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CommReportingPercentageSettingName implements DatabaseRepresentationSource {
    COMM_REPORTING_PERCENTAGE("Comm Reporting Percentage"),
    ;

    private final String displayName;

    private CommReportingPercentageSettingName(String displayName) {
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