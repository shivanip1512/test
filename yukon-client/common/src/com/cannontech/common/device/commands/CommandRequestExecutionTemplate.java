package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;

public interface CommandRequestExecutionTemplate<T> {
    
    CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback);
    
    /* This method requires CommandRequestExecution to be created prior to calling execute*/
    CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback, CommandRequestExecution execution, boolean multipleStrategies);
    
    CommandRequestExecutionContextId getContextId();

    /* This method requires CommandRequestExecution to be created prior to calling execute*/
    CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback,
            CommandRequestExecution execution, boolean multipleStrategies, boolean noqueue);
}
