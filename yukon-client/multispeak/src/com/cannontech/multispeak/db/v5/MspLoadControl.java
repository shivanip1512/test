package com.cannontech.multispeak.db.v5;

import java.util.Date;
import java.util.List;

import com.cannontech.msp.beans.v5.enumerations.ControlEventKind;
import com.cannontech.multispeak.db.MspLmMapping;

public class MspLoadControl {
    private List<MspLmMapping> mspLmInterfaceMappings;
    private ControlEventKind controlEventKind;
    private Date startTime;
    private Date stopTime;

    public MspLoadControl(List<MspLmMapping> mspLmInterfaceMappings, ControlEventKind controlEventKind, Date startTime,
            Date stopTime) {
        super();
        this.mspLmInterfaceMappings = mspLmInterfaceMappings;
        this.controlEventKind = controlEventKind;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public MspLoadControl() {
    }

    public ControlEventKind getControlEventKind() {
        return controlEventKind;
    }

    public void setControlEventKind(ControlEventKind controlEventKind) {
        this.controlEventKind = controlEventKind;
    }

    public List<MspLmMapping> getMspLmInterfaceMappings() {
        return mspLmInterfaceMappings;
    }

    public void setMspLmInterfaceMappings(List<MspLmMapping> mspLmInterfaceMappings) {
        this.mspLmInterfaceMappings = mspLmInterfaceMappings;
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
