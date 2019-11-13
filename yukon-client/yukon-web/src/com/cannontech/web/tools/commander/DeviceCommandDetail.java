package com.cannontech.web.tools.commander;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DeviceCommandDetail {

    private Integer deviceCommandId;
    private Integer commandId;
    private String category;
    private String displayableCategory;
    private int displayOrder;
    private boolean visibleFlag;
    private String commandName;
    private String command;

    public DeviceCommandDetail() {
    }

    public DeviceCommandDetail(Integer deviceCommandId, Integer commandId, String category, String displayableCategory,
                               int displayOrder, boolean visibleFlag, String commandName, String command) {
        this.deviceCommandId = deviceCommandId;
        this.commandId = commandId;
        this.category = category;
        this.setDisplayableCategory(displayableCategory);
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

    public String getDisplayableCategory() {
        return displayableCategory;
    }

    public void setDisplayableCategory(String displayableCategory) {
        this.displayableCategory = displayableCategory;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
