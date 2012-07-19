package com.cannontech.database.db.point;

import com.cannontech.database.data.point.ControlType;

public class PointAnalogControl extends PointControl 
{
    @Override
    protected boolean isValidControlType(String controlType) {
        
        return controlType.equalsIgnoreCase(ControlType.NORMAL.getControlName());
    }
}
