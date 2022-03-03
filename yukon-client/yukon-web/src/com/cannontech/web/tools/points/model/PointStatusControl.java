package com.cannontech.web.tools.points.model;

import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.StatusControlType;

public class PointStatusControl extends PointControl<com.cannontech.database.db.point.PointStatusControl> {

    private Integer closeTime1;
    private Integer closeTime2;
    private String openCommand;
    private String closeCommand;
    private Integer commandTimeOut;
    private StatusControlType controlType;

    public Integer getCloseTime1() {
        return closeTime1;
    }

    public void setCloseTime1(Integer closeTime1) {
        this.closeTime1 = closeTime1;
    }

    public Integer getCloseTime2() {
        return closeTime2;
    }

    public void setCloseTime2(Integer closeTime2) {
        this.closeTime2 = closeTime2;
    }

    public String getOpenCommand() {
        return openCommand;
    }

    public void setOpenCommand(String openCommand) {
        this.openCommand = openCommand;
    }

    public String getCloseCommand() {
        return closeCommand;
    }

    public void setCloseCommand(String closeCommand) {
        this.closeCommand = closeCommand;
    }

    public Integer getCommandTimeOut() {
        return commandTimeOut;
    }

    public void setCommandTimeOut(Integer commandTimeOut) {
        this.commandTimeOut = commandTimeOut;
    }

    public StatusControlType getControlType() {
        return controlType;
    }

    public void setControlType(StatusControlType controlType) {
        this.controlType = controlType;
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.point.PointStatusControl pointStatusControl) {
        super.buildDBPersistent(pointStatusControl);

     // This case will be handled when we can change the Control Type to None through Update
        if (getControlType() == StatusControlType.NONE) {
            pointStatusControl.setCloseTime1(0);
            pointStatusControl.setCloseTime2(0);
            pointStatusControl.setStateOneControl(ControlStateType.CLOSE.getControlCommand());
            pointStatusControl.setStateZeroControl(ControlStateType.OPEN.getControlCommand());
            pointStatusControl.setControlInhibited(false);
            pointStatusControl.setControlOffset(0);
            pointStatusControl.setControlType(StatusControlType.NONE.getControlName());
            pointStatusControl.setCommandTimeOut(0);
        } else {
            if (getCloseTime1() != null) {
                pointStatusControl.setCloseTime1(getCloseTime1());
            }

            if (getCloseTime2() != null) {
                pointStatusControl.setCloseTime2(getCloseTime2());
            }

            if (getCommandTimeOut() != null) {
                pointStatusControl.setCommandTimeOut(getCommandTimeOut());
            }

            if (getCloseCommand() != null) {
                pointStatusControl.setStateOneControl(getCloseCommand());
            }

            if (getOpenCommand() != null) {
                pointStatusControl.setStateZeroControl(getOpenCommand());
            }

            if (getControlType() != null) {
                pointStatusControl.setControlType(getControlType().getControlName());
            }
        }

    }

    @Override
    public void buildModel(com.cannontech.database.db.point.PointStatusControl pointStatusControl) {
        super.buildModel(pointStatusControl);

        setCloseTime1(pointStatusControl.getCloseTime1());
        setCloseTime2(pointStatusControl.getCloseTime2());
        setCommandTimeOut(pointStatusControl.getCommandTimeOut());
        setControlType(StatusControlType.getStatusControlType(pointStatusControl.getControlType()));
        setCloseCommand(pointStatusControl.getStateOneControl());
        setOpenCommand(pointStatusControl.getStateZeroControl());
    }
}
