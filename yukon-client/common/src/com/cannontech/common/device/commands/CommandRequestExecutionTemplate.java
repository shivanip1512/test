package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;

public interface CommandRequestExecutionTemplate<T> {
    
    public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback);
    
    /* This method requires CommandRequestExecution to be created prior to calling execute*/
    public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback, CommandRequestExecution execution);
    public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback, boolean noQueue);
    public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback, int priority);
    public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback, boolean noQueue, int priority);
    
    public CommandRequestExecutionContextId getContextId();
}
