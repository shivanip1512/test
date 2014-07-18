package com.cannontech.cbc.cyme.model;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.db.point.stategroup.PointState;

public enum TwoStateActive implements PointState{
    INACTIVE(0,"Inactive"),
    ACTIVE(1,"Active");
    
    final private int analogValue;
    final private String displayValue; 
    
    private TwoStateActive(int analogValue, String displayValue) {
        this.analogValue = analogValue;
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
    
    public static TwoStateActive getForAnalogValue(int analogValue) {
        for(TwoStateActive state: values()) {
            if (state.getRawState() == analogValue) {
                return state;
            }
        }
        throw new NotFoundException("TwoStateActive state was not found for value: " + analogValue);
    }

    @Override
    public int getRawState() {
        return analogValue;
    }
}
