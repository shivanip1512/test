package com.cannontech.common.dr.setup;

import com.cannontech.common.util.TimeIntervals;

public class ControlAreaProjection {

    private ControlAreaProjectionType projectionType;
    private Integer projectionPoints;
    private TimeIntervals projectAheadDuration;

    public ControlAreaProjectionType getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(ControlAreaProjectionType projectionType) {
        this.projectionType = projectionType;
    }

    public Integer getProjectionPoints() {
        return projectionPoints;
    }

    public void setProjectionPoints(Integer projectionPoints) {
        this.projectionPoints = projectionPoints;
    }

    public TimeIntervals getProjectAheadDuration() {
        return projectAheadDuration;
    }

    public void setProjectAheadDuration(TimeIntervals projectAheadDuration) {
        this.projectAheadDuration = projectAheadDuration;
    }

}
