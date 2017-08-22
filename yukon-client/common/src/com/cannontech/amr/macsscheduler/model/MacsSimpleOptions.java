package com.cannontech.amr.macsscheduler.model;

public class MacsSimpleOptions {
    private int targetPAObjectId;
    private String startCommand;
    private String stopCommand;
    private Integer repeatInterval = 0;
    
    public int getTargetPAObjectId() {
        return targetPAObjectId;
    }
    public void setTargetPAObjectId(int targetPAObjectId) {
        this.targetPAObjectId = targetPAObjectId;
    }
    public String getStartCommand() {
        return startCommand;
    }
    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }
    public String getStopCommand() {
        return stopCommand;
    }
    public void setStopCommand(String stopCommand) {
        this.stopCommand = stopCommand;
    }
    public Integer getRepeatInterval() {
        return repeatInterval;
    }
    public void setRepeatInterval(Integer repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
