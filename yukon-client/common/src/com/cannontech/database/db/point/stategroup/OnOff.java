package com.cannontech.database.db.point.stategroup;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.db.point.stategroup.PointState;

public enum OnOff implements PointState {
    ON(1,"On"),
    OFF(0,"Off");
    
    final private int analogValue;
    final private String displayValue; 
    
    private OnOff(int analogValue, String displayValue) {
        this.analogValue = analogValue;
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
    
    public static OnOff getForAnalogValue(int analogValue) {
        for(OnOff state: values()) {
            if (state.getRawState() == analogValue) {
                return state;
            }
        }
        throw new NotFoundException("OnOff state was not found for value: " + analogValue);
    }

    @Override
    public int getRawState() {
        return analogValue;
    }
}
