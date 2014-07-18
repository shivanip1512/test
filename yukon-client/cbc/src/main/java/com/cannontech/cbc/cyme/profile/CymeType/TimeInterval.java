package com.cannontech.cbc.cyme.profile.CymeType;

import com.cannontech.cbc.cyme.profile.CymeRepresentation;
import com.cannontech.core.dao.NotFoundException;

public enum TimeInterval implements CymeRepresentation{
    MINUTES5("5MINUTES",5),
    MINUTES15("15MINUTES",15),
    MINUTES30("30MINUTES",30),
    HOUR("HOUR",60);

    private String cymeValue;
    private int minutes;
    
    private TimeInterval(String cymeValue, int minutes) {
        this.cymeValue = cymeValue;
        this.minutes = minutes;
    }
    
    static public TimeInterval getFromCymeValue(String cymeValue) {
        
        for (TimeInterval i : values()) {
            if (i.getCymeValue().equals(cymeValue)) {
                return i;
            }
        }
        throw new NotFoundException("TimeInterval was not found to match: " + cymeValue);
    }

    @Override
    public String getCymeValue() {
        return cymeValue;
    }
    
    public int getMinutes() {
        return minutes;
    }
}