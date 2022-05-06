package com.cannontech.multispeak.db.v4;

import java.util.Date;
import java.util.List;

import com.cannontech.msp.beans.v4.ControlEventType;
import com.cannontech.multispeak.db.MspLmMapping;

public class MspLoadControl {
    private List<MspLmMapping> mspLmInterfaceMappings;
    private ControlEventType controlEventType;
    private Date startTime;
    private Date stopTime;

    public MspLoadControl(List<MspLmMapping> mspLmInterfaceMappings, ControlEventType controlEventType, Date startTime,
            Date stopTime) {
        super();
        this.mspLmInterfaceMappings = mspLmInterfaceMappings;
        this.controlEventType = controlEventType;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public MspLoadControl() {
    }

    public List<MspLmMapping> getMspLmInterfaceMappings() {
        return mspLmInterfaceMappings;
    }

    public void setMspLmInterfaceMappings(List<MspLmMapping> mspLmInterfaceMappings) {
        this.mspLmInterfaceMappings = mspLmInterfaceMappings;
    }

    public ControlEventType getControlEventType() {
        return controlEventType;
    }

    public void setControlEventType(ControlEventType controlEventType) {
        this.controlEventType = controlEventType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
}