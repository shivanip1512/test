package com.cannontech.database.db.version;

public final class CTIDatabase {
    private final String version;
    private final Integer build;
    
    public CTIDatabase(String version, Integer build) {
        this.version = version;
        this.build = build;
    }
    
    public String getVersion() {
        return version;
    }

    public Integer getBuild() {
        return build;
    }

}
