package com.cannontech.dr.controlarea.model;

public enum TriggerType {
    THRESHOLD("Threshold"), STATUS("Status");
    private final String dbString;

    private TriggerType(String dbString) {
        this.dbString = dbString;
    }

    public String getDbString() {
        return dbString;
    }
}
