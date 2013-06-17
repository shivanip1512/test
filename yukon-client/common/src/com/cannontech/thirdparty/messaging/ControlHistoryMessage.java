package com.cannontech.thirdparty.messaging;

import java.util.Date;

import com.cannontech.message.util.Message;

public class ControlHistoryMessage extends Message {

    private int paoId;
    private int pointId;
    private int rawState;
    private Date startTime;
    private int controlDuration;
    private int reductionRatio;
    private String controlType;
    private String activeRestore;
    private double reductionValue;
    private int controlPriority;
    private int associationId;
    
    public static final int CONTROL_RESTORE_DURATION = -1;
    
    public int getPaoId() {
        return paoId;
    }
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    public int getPointId() {
        return pointId;
    }
    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
    public int getRawState() {
        return rawState;
    }
    public void setRawState(int rawState) {
        this.rawState = rawState;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public int getControlDuration() {
        return controlDuration;
    }
    public void setControlDuration(int controlDuration) {
        this.controlDuration = controlDuration;
    }
    public int getReductionRatio() {
        return reductionRatio;
    }
    public void setReductionRatio(int reductionRatio) {
        this.reductionRatio = reductionRatio;
    }
    public String getControlType() {
        return controlType;
    }
    public void setControlType(String controlType) {
        this.controlType = controlType;
    }
    public String getActiveRestore() {
        return activeRestore;
    }
    public void setActiveRestore(String activeRestore) {
        this.activeRestore = activeRestore;
    }
    public double getReductionValue() {
        return reductionValue;
    }
    public void setReductionValue(double reductionValue) {
        this.reductionValue = reductionValue;
    }
    public int getControlPriority() {
        return controlPriority;
    }
    public void setControlPriority(int controlPriority) {
        this.controlPriority = controlPriority;
    }
    public void setAssociationId(int associationId) {
        this.associationId = associationId;
    }
    public int getAssociationId() {
        return associationId;
    }
}
