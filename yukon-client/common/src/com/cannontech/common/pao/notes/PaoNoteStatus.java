package com.cannontech.common.pao.notes;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PaoNoteStatus implements DatabaseRepresentationSource {

    CREATED("C"),
    EDITED("E"),
    DELETED("D");
    
    private final String dbString;
    
    private PaoNoteStatus(String dbString) {
        this.dbString = dbString;
    }

    public String getDbString() {
        return dbString;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return getDbString();
    }
}
