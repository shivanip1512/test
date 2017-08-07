package com.cannontech.amr.macsscheduler.model;

public class MacsSimpleOptions {
    private int targetPAObjectId;
    private String startCommand;
    private String stopCommand;
    private int repeatInterval;
    
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
    public int getRepeatInterval() {
        return repeatInterval;
    }
    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
