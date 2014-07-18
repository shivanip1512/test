package com.cannontech.cbc.cyme.profile.CymeType;

import com.cannontech.cbc.cyme.profile.CymeRepresentation;
import com.cannontech.core.dao.NotFoundException;

public enum GlobalUnit implements CymeRepresentation{
    AVERAGEKW("AVERAGEKW"),
    KW_KVAR("KW-KVAR"),
    KW_PF("KW-PF"),
    KVA_PF("KVA-PF"),
    AMP_PF("AMP-PF"),
    PERCENT("%"),
    PERCENT_PQ("%PQ"),
    PU("PU");

    private String cymeValue; 
    
    private GlobalUnit(String cymeValue) {
        this.cymeValue = cymeValue;
    }

    
    static public GlobalUnit getFromCymeValue(String cymeValue) {
        
        for (GlobalUnit unit : values()) {
            if (unit.getCymeValue().equals(cymeValue)) {
                return unit;
            }
        }
        throw new NotFoundException("Global Unit was not found to match: " + cymeValue);
    }

    @Override
    public String getCymeValue() {
        return cymeValue;
    }
}
