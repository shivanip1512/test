package com.cannontech.database.db.version;

public class CTIDatabase {
    private String version = null;
    private Integer build = null;
    
    public CTIDatabase(String version, Integer build) {
        this.version = version;
        this.build = build;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getBuild() {
        return build;
    }
    
    public void setBuild(Integer build) {
        this.build = build;
    }
    
}
