package com.cannontech.common.device.streaming.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum BehaviorReportStatus implements DisplayableEnum,DatabaseRepresentationSource {
    PENDING("Pending"), 
    CONFIRMED("Confirmed")
    ;

    private final String dbString;

    private BehaviorReportStatus(String dbString) {
        this.dbString = dbString;
    }

    @Override
    public String getFormatKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }

}
