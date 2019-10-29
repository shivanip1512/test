package com.cannontech.web.tools.commander;

public class DeviceCommandModel {
    
    private int deviceCommandId;
    private int commandId;
    private String deviceType;
    private int displayOrder;
    private boolean visibleFlag;
    private String commandName;
    private String command;
    
    public int getDeviceCommandId() {
        return deviceCommandId;
    }
    public void setDeviceCommandId(int deviceCommandId) {
        this.deviceCommandId = deviceCommandId;
    }
    public int getCommandId() {
        return commandId;
    }
    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public int getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
    public boolean isVisibleFlag() {
        return visibleFlag;
    }
    public void setVisibleFlag(boolean visibleFlag) {
        this.visibleFlag = visibleFlag;
    }
    public String getCommandName() {
        return commandName;
    }
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }


}
