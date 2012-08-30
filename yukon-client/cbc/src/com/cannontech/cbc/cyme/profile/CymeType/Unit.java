package com.cannontech.cbc.cyme.profile.CymeType;

import com.cannontech.cbc.cyme.profile.CymeRepresentation;
import com.cannontech.core.dao.NotFoundException;

public enum Unit implements CymeRepresentation {
    AverageKW("AverageKW"),
    PERCENT("%"),
    PU("PU"),
    KWKVAR_KW("KWKVAR_KW"),
    KWKVAR_KVAR("KWKVAR_KVAR"),
    KWPF_PW("KWPF_PW"),
    KWPF_PF("KWPF_PF"),
    KVAPF_KVA("KVAPF_KVA"),
    KVAPF_PF("KVAPF_PF"),
    AMPPF_AMP("AMPPF_AMP"),
    AMPPF_PF("AMPPF_PF"),
    PERCENT_PQ_P("%PQ_P"),
    PERCENT_PQ_Q("%PQ_Q"),
    ALL("ALL");

    private String cymeValue;
    
    private Unit(String cymeValue) {
        this.cymeValue = cymeValue;
    }
    
    static public Unit getFromCymeValue(String cymeValue) {
        
        for (Unit i : values()) {
            if (i.getCymeValue().equals(cymeValue)) {
                return i;
            }
        }
        throw new NotFoundException("Unit was not found to match: " + cymeValue);
    }

    @Override
    public String getCymeValue() {
        return cymeValue;
    } 
}
