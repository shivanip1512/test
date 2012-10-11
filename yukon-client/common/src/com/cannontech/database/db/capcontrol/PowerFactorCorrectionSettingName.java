package com.cannontech.database.db.capcontrol;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PowerFactorCorrectionSettingName implements DatabaseRepresentationSource {
    POWER_FACTOR_CORRECTION("Power Factor Correction"),
    ;

    private final String displayName;

    private PowerFactorCorrectionSettingName(String displayName) {
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