package com.cannontech.database.data.lite;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.database.data.command.YukonCommand;


public class LiteDeviceTypeCommand extends LiteBase implements YukonCommand {
    
    private int commandId = 0;
    private String deviceType;
    private int displayOrder = 0;
    private char visibleFlag = 'Y';
    
    public LiteDeviceTypeCommand(int deviceCommandId, int commandId, String devType, int order, char visible) {
        
        setLiteType(LiteTypes.DEVICE_TYPE_COMMAND);
        
        setLiteID(deviceCommandId);
        setCommandId(commandId);
        setDeviceType(devType);
        setDisplayOrder(order);
        setVisibleFlag(visible);
    }
    
    public int getDeviceCommandId() {
        return getLiteID();
    }
    
    public void setDeviceCommandId(int deviceCommandId) {
        setLiteID(deviceCommandId);
    }
    
    @Override
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
    
    public boolean isVisible() {
        return getVisibleFlag() == 'Y';
    }
    
    public char getVisibleFlag() {
        return visibleFlag;
    }
    
    public void setVisibleFlag(char visibleFlag) {
        this.visibleFlag = visibleFlag;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}