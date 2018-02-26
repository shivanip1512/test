package com.cannontech.common.device.commands;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.impl.CommandRequestExecutionDefaults;
import com.cannontech.database.data.lite.LiteYukonUser;

public class ExecutionParameters {
    private int contextId;
    private DeviceRequestType type;
    private LiteYukonUser user;
    private boolean noqueue;
    private int priority;
    private boolean updateExecutionStatus;
        
    public ExecutionParameters(CommandRequestExecutionContextId contextId, DeviceRequestType type,
            boolean updateExecutionStatus, Boolean noqueue, LiteYukonUser user) {

        this.contextId = contextId.getId();
        this.type = type;
        this.user = user;
        this.noqueue = noqueue == null ? CommandRequestExecutionDefaults.isNoqueue(type) : noqueue;
        this.priority = CommandRequestExecutionDefaults.getPriority(type);
        this.updateExecutionStatus = updateExecutionStatus;
    }

    public int getContextId() {
        return contextId;
    }
    public DeviceRequestType getType() {
        return type;
    }
    public LiteYukonUser getUser() {
        return user;
    }
    public boolean isNoqueue() {
        return noqueue;
    }
    public int getPriority() {
        return priority;
    };
    
    public boolean updateExecutionStatus() {
        return updateExecutionStatus;
    }
    
    public void overridePriority(int newPriority) {
       this.priority = newPriority;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("contextId", contextId);
        builder.append("type", type.name());
        builder.append("user", user.getUsername());
        builder.append("noqueue", noqueue);
        builder.append("priority", priority);
        builder.append("updateExecutionStatus", updateExecutionStatus);
        return builder.toString();
    }
}
