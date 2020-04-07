package com.cannontech.database;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum YNBoolean implements DatabaseRepresentationSource {
    YES(true, "Y"),
    NO(false, "N")
    ;

    private final boolean booleanValue;
    private final String dbString;
    
    private YNBoolean(boolean booleanValue, String dbString) {
        this.booleanValue = booleanValue;
        this.dbString = dbString;
    }

    public boolean getBoolean() {
        return booleanValue;
    }

    public String getDbString() {
        return dbString;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }

    public static YNBoolean valueOf(boolean value) {
        return value ? YES : NO;
    }
    
    public static YNBoolean getForDbString(String dbString) {
        for (YNBoolean ynBoolean : YNBoolean.values()) {
            if (ynBoolean.getDbString().equalsIgnoreCase(dbString)) {
                return ynBoolean;
            }
        }
        throw new IllegalArgumentException(dbString);
    }
}
