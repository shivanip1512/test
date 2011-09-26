package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CommandRequestExecutionParameterDto {

    private CommandRequestExecutionContextId contextId;
    private DeviceRequestType type;
    private LiteYukonUser user;
    private boolean noqueue = false;
    private boolean update = true;
    private int priority = 7;
    
    public CommandRequestExecutionParameterDto(CommandRequestExecutionContextId contextId, DeviceRequestType type, LiteYukonUser user) {
        this.contextId = contextId;
        this.type = type;
        this.user = user;
    }
    
    public CommandRequestExecutionParameterDto withNoqueue(boolean noqueue) {
        
        CommandRequestExecutionParameterDto newDto = this.clone();
        newDto.noqueue = noqueue;

        return newDto;
    }
    
    public CommandRequestExecutionParameterDto withUpdate(boolean update) {
        
        CommandRequestExecutionParameterDto newDto = this.clone();
        newDto.update = update;

        return newDto;
    }
    
    public CommandRequestExecutionParameterDto withPriority(int priority) {
        
        CommandRequestExecutionParameterDto newDto = this.clone();
        newDto.priority = priority;

        return newDto;
    }
    
    public CommandRequestExecutionParameterDto clone() {
        
        CommandRequestExecutionParameterDto newDto = new CommandRequestExecutionParameterDto(this.contextId, this.type, this.user);
        newDto.noqueue = this.noqueue;
        newDto.update = this.update;
        newDto.priority = this.priority;
        
        return newDto;
    }
    
    public CommandRequestExecutionContextId getContextId() {
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
    public boolean isUpdate() {
        return update;
    }
    public int getPriority() {
        return priority;
    };
    
    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("contextId", contextId.getId());
        tsc.append("type", type.name());
        tsc.append("user", user.getUsername());
        tsc.append("noqueue", noqueue);
        tsc.append("update", update);
        tsc.append("priority", priority);
        return tsc.toString();
    }
   
}
