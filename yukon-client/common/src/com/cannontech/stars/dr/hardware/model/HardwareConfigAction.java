package com.cannontech.stars.dr.hardware.model;

import java.util.Date;

import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;

public class HardwareConfigAction {
    private Date date;
    private EnrollmentEnum actionType;
    private String programName;
    private String loadGroupName;
    private String hardwareSerialNumber;
    private int relay;

    public HardwareConfigAction(Date date, EnrollmentEnum actionType,
            String programName, String loadGroupName,
            String hardwareSerialNumber, int relay) {
        this.date = date;
        this.actionType = actionType;
        this.programName = programName;
        this.loadGroupName = loadGroupName;
        this.hardwareSerialNumber = hardwareSerialNumber;
        this.relay = relay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EnrollmentEnum getActionType() {
        return actionType;
    }

    public void setActionType(EnrollmentEnum actionType) {
        this.actionType = actionType;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getLoadGroupName() {
        return loadGroupName;
    }

    public void setLoadGroupName(String loadGroupName) {
        this.loadGroupName = loadGroupName;
    }

    public String getHardwareSerialNumber() {
        return hardwareSerialNumber;
    }

    public void setHardwareSerialNumber(String hardwareSerialNumber) {
        this.hardwareSerialNumber = hardwareSerialNumber;
    }

    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }
}
