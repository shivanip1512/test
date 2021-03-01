package com.cannontech.web.tools.points.model;

import com.cannontech.database.data.point.StatusPoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonPropertyOrder({ "pointId", "pointName", "pointType", "pointOffset", "paoId", "stateGroupId", "initialState", "enable",
    "archiveType", "archiveInterval", "timingGroup", "alarmsDisabled", "staleData", "alarming", "fdrList", "pointStatusControl" })
public class StatusPointModel<T extends StatusPoint> extends PointBaseModel<T> {

    private Integer initialState;
    private PointStatusControl pointStatusControl;

    public Integer getInitialState() {
        return initialState;
    }

    public void setInitialState(Integer initialState) {
        this.initialState = initialState;
    }

    public PointStatusControl getPointStatusControl() {
        if (pointStatusControl == null) {
            pointStatusControl = new PointStatusControl();
        }
        return pointStatusControl;
    }

    public void setPointStatusControl(PointStatusControl pointStatusControl) {
        this.pointStatusControl = pointStatusControl;
    }

    @Override
    public void buildDBPersistent(T statusPoint) {
        super.buildDBPersistent(statusPoint);

        if (getInitialState() != null) {
            statusPoint.getPointStatus().setInitialState(getInitialState());
        }
        getPointStatusControl().buildDBPersistent(statusPoint.getPointStatusControl());
    }

    @Override
    public void buildModel(T statusPoint) {
        super.buildModel(statusPoint);

        setInitialState(statusPoint.getPointStatus().getInitialState());
        getPointStatusControl().buildModel(statusPoint.getPointStatusControl());
    }
}
