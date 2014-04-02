package com.cannontech.web.editor.point;

import org.apache.commons.lang3.Validate;

import com.cannontech.database.db.point.PointAnalogControl;

public class PointAnalogControlEntry {
    private PointAnalogControl pointControl = null;

    public PointAnalogControlEntry(PointAnalogControl pointControl) {
        Validate.notNull(pointControl, "PointStatusControl can not be a NULL reference");

        this.pointControl = pointControl;
    }

    public boolean isControlAvailable() {
        return getPointAnalogControl().hasControl();
    }

    private PointAnalogControl getPointAnalogControl() {
        return pointControl;
    }
}