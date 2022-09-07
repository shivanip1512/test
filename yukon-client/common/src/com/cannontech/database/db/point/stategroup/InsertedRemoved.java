package com.cannontech.database.db.point.stategroup;

import com.cannontech.core.dao.NotFoundException;

public enum InsertedRemoved implements PointState {
    REMOVED(0, "Removed"),
    INSERTED(1, "Inserted"),
    UNKOWN(2, "Unkown");

    final private int analogValue;
    final private String displayValue;
    
    private InsertedRemoved(int analogValue, String displayValue) {
        this.analogValue = analogValue;
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public static InsertedRemoved getForAnalogValue(int analogValue) {
        for (InsertedRemoved state : values()) {
            if (state.getRawState() == analogValue) {
                return state;
            }
        }
        throw new NotFoundException("InsertedRemoved state was not found for value: " + analogValue);
    }

    @Override
    public int getRawState() {
        return analogValue;
    }

}
