package com.cannontech.database.db.point;

import com.cannontech.database.data.point.AnalogControlType;

public class PointAnalogControl extends PointControl {
    private String controlType = AnalogControlType.NONE.getControlName();
    
    @Override
    public String getControlType() {
        return controlType;
    }
    
    @Override
    protected String getNormalType() {
        return AnalogControlType.NORMAL.getControlName();
    }
    
    @Override
    public boolean hasControl() {
        return ! controlType.equalsIgnoreCase(AnalogControlType.NONE.getControlName());
    }
    
    @Override
    public void setControlType(String newValue) {
        if (isValidControlType(newValue)) {
            this.controlType = newValue;
        } else {
            this.controlType = AnalogControlType.NONE.getControlName();
        }
    }
    
    @Override
    protected boolean isValidControlType(String controlType) {
        return controlType.equalsIgnoreCase(AnalogControlType.NORMAL.getControlName());
    }
}
