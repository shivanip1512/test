package com.cannontech.web.tools.commander;

public class DeviceCommandDetail {
    
    private Integer deviceCommandId;
    private Integer commandId;
    private String category;
    private int displayOrder;
    private boolean visibleFlag;
    private String commandName;
    private String command;
    
    public DeviceCommandDetail() {
    }
    
    public DeviceCommandDetail(Integer deviceCommandId, Integer commandId, String category, int displayOrder, 
                               boolean visibleFlag, String commandName, String command) {
        this.deviceCommandId = deviceCommandId;
        this.commandId = commandId;
        this.category = category;
        this.displayOrder = displayOrder;
        this.visibleFlag = visibleFlag;
        this.commandName = commandName;
        this.command = command;
    }
    
    public Integer getDeviceCommandId() {
        return deviceCommandId;
    }
    public void setDeviceCommandId(Integer deviceCommandId) {
        this.deviceCommandId = deviceCommandId;
    }
    public Integer getCommandId() {
        return commandId;
    }
    public void setCommandId(Integer commandId) {
        this.commandId = commandId;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
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
