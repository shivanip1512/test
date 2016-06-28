package com.cannontech.common.device.streaming.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum BehaviorType implements DatabaseRepresentationSource {
    DATA_STREAMING("Data Streaming");
    ;

    private final String dbString;

    private BehaviorType(String dbString) {
        this.dbString = dbString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }
}
