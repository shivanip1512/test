package com.cannontech.common.tdc.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum DisplayTypeEnum implements DatabaseRepresentationSource{
    LOAD_MANAGEMENT_CLIENT("Load Management Client"),
    SCHEDULER_CLIENT("Scheduler Client"),
    ALARMS_AND_EVENTS("Alarms and Events"),
    CUSTOM_DISPLAYS("Custom Displays");

    private String dbString;

    DisplayTypeEnum(String dbString) {
        this.dbString = dbString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }

}
