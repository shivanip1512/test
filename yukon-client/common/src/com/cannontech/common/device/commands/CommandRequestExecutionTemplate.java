package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;

public interface CommandRequestExecutionTemplate<T> {
    
    public CommandRequestExecutionIdentifier execute(final List<T> commands, final CommandCompletionCallback<? super T> callback);
    
    public CommandRequestExecutionContext getContext();
    
    public void setIsNoqueue(boolean noqueue);
    
    public void setPriority(int priority);
}
