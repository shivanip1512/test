package com.cannontech.common.pao.notes.search.result.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.notes.model.PaoNote;

public class PaoNotesSearchResult {
    
    private PaoNote paoNote;
    private PaoType paoType;
    private String paoName;
    
    public PaoNote getPaoNote() {
        return paoNote;
    }
    
    public void setPaoNote(PaoNote paoNote) {
        this.paoNote = paoNote;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }
    
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    public String getPaoName() {
        return paoName;
    }
    
    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
    
}
