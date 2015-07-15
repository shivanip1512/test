package com.cannontech.common.fdr;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum FdrDirection implements DatabaseRepresentationSource {

    RECEIVE("Receive"),
    RECEIVE_FOR_CONTROL("Receive for control"),
    RECEIVE_FOR_ANALOG_OUTPUT("Receive for Analog Output"),
    SEND("Send"),
    SEND_FOR_CONTROL("Send for control"),
    LINK_STATUS("Link Status");

    private final String dbName;

    FdrDirection(String dbName) {
        this.dbName = dbName;
    }

    public String getValue() {
        return dbName;
    }

    public Object getDatabaseRepresentation() {
        return dbName;
    }

    public static FdrDirection getEnum(String name) {
        FdrDirection ret = null;
        for (FdrDirection dir : FdrDirection.values()) {
            if (dir.getValue().equalsIgnoreCase(name)) {
                ret = dir;
                break;
            }
        }
        return ret;
    }
}
