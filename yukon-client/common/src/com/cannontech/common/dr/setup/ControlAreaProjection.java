package com.cannontech.common.dr.setup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ControlAreaProjection {

    private ControlAreaProjectionType projectionType;
    private Integer projectionPoint;
    private Integer projectAheadDuration;

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

    public Integer getProjectAheadDuration() {
        return projectAheadDuration;
    }

    public void setProjectAheadDuration(Integer projectAheadDuration) {
        this.projectAheadDuration = projectAheadDuration;
    }

}
