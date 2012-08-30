package com.cannontech.cbc.cyme.profile.CymeType;

import com.cannontech.cbc.cyme.profile.CymeRepresentation;
import com.cannontech.core.dao.NotFoundException;

public enum IntervalFormat implements CymeRepresentation {
    HOURS8760("8760HOURS"),
    SEASONS4_DAYTYPE1("4SEASONS_1DAYTYPE"),
    SEASONS4_DAYTYPES2("4SEASONS_2DAYTYPES"),
    SEASONS4_DAYTYPES3("4SEASONS_3DAYTYPES"),
    SEASONS4_DAYTYPES5("4SEASONS_5DAYTYPES"),
    SEASONS4_DAYTYPES7("4SEASONS_7DAYTYPES"),
    MONTHS12_DAYTYPE1("12MONTHS_1DAYTYPE"),
    MONTHS12_DAYTYPE2("12MONTHS_2DAYTYPES"),
    MONTHS12_DAYTYPE3("12MONTHS_3DAYTYPES"),
    MONTHS12_DAYTYPE5("12MONTHS_5DAYTYPES"),
    MONTHS12_DAYTYPE7("12MONTHS_7DAYTYPES"),
    DAYS365("365DAYS");

    private String cymeValue;
    
    private IntervalFormat(String cymeValue) {
        this.cymeValue =cymeValue;  
    }
    
    
    static public IntervalFormat getFromCymeValue(String cymeValue) {
        
        for (IntervalFormat i : values()) {
            if (i.getCymeValue().equals(cymeValue)) {
                return i;
            }
        }
        throw new NotFoundException("Global Unit was not found to match: " + cymeValue);
    }

    @Override
    public String getCymeValue() {
        return cymeValue;
    } 
}
