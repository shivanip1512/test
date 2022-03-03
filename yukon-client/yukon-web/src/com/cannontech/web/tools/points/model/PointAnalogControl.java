package com.cannontech.web.tools.points.model;

import com.cannontech.database.data.point.AnalogControlType;

public class PointAnalogControl extends PointControl<com.cannontech.database.db.point.PointAnalogControl> {

    private AnalogControlType controlType;

    public AnalogControlType getControlType() {
        return controlType;
    }

    public void setControlType(AnalogControlType controlType) {
        this.controlType = controlType;
    }

    @Override
    public void buildModel(com.cannontech.database.db.point.PointAnalogControl analogControl) {
        super.buildModel(analogControl);

        setControlType(AnalogControlType.getAnalogControlTypeValue(analogControl.getControlType()));
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.point.PointAnalogControl analogControl) {
        super.buildDBPersistent(analogControl);

        // This case will be handled when we can change the Control Type to None through Update.
        // Suppose created Point with controlType set to "Normal" and "controlOffset": set to 10.
        // In case of Update, Changed controlType to "NONE" and removed controlOffset" field then value of
        // "controlOffset": is 10 which is not correct case so setting default values in "NONE" case.
        if (getControlType() == AnalogControlType.NONE) {
            // update the super fields when NONE
            analogControl.setControlInhibited(false);
            analogControl.setControlOffset(0);
        } else {
            if (getControlType() != null) {
                analogControl.setControlType(getControlType().getControlName());
            }
        }
    }
}
