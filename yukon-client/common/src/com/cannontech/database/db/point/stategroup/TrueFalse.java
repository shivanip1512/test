package com.cannontech.database.db.point.stategroup;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.db.point.stategroup.PointState;

public enum TrueFalse implements PointState {
    TRUE(1,"True"),
    FALSE(0,"False");
    
    final private int analogValue;
    final private String displayValue; 
    
    private TrueFalse(int analogValue, String displayValue) {
        this.analogValue = analogValue;
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
    
    public static TrueFalse getForAnalogValue(int analogValue) {
        for(TrueFalse state: values()) {
            if (state.getRawState() == analogValue) {
                return state;
            }
        }
        throw new NotFoundException("TrueFalse state was not found for value: " + analogValue);
    }

    @Override
    public int getRawState() {
        return analogValue;
    }
}
