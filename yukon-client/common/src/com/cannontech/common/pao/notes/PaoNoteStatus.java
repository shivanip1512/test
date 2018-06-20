package com.cannontech.common.pao.notes;

public enum PaoNoteStatus {

    CREATED("C"),
    EDITED("E"),
    DELETED("D");
    
    private final String dbString;
    
    private PaoNoteStatus(String dbString) {
        this.dbString = dbString;
    }
    
    public String getDBString() {
        return dbString;
    }
}
