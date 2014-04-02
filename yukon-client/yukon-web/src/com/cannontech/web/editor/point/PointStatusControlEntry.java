package com.cannontech.web.editor.point;

import org.apache.commons.lang3.Validate;

import com.cannontech.database.db.point.PointStatusControl;

public class PointStatusControlEntry {
    private PointStatusControl pointControl = null;

    public PointStatusControlEntry(PointStatusControl pointControl) {
        Validate.notNull(pointControl, "PointStatusControl can not be a NULL reference");

        this.pointControl = pointControl;
    }

    public boolean isControlAvailable() {
        return getPointStatusControl().hasControl();
    }

    private PointStatusControl getPointStatusControl() {
        return pointControl;
    }
}