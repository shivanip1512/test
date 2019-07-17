package com.cannontech.common.dr.setup;

import com.cannontech.common.util.TimeIntervals;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ControlAreaProjection {

    private ControlAreaProjectionType projectionType;
    private Integer projectionPoint;
    private TimeIntervals projectAheadDuration;

    public ControlAreaProjectionType getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(ControlAreaProjectionType projectionType) {
        this.projectionType = projectionType;
    }

    public Integer getProjectionPoint() {
        return projectionPoint;
    }

    public void setProjectionPoint(Integer projectionPoint) {
        this.projectionPoint = projectionPoint;
    }

    public TimeIntervals getProjectAheadDuration() {
        return projectAheadDuration;
    }

    public void setProjectAheadDuration(TimeIntervals projectAheadDuration) {
        this.projectAheadDuration = projectAheadDuration;
    }

}
