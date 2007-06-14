package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.commands.impl.CommandCompletionException;

public interface CommandRequestExecutor {
    
    public CommandResultHolder execute(CommandRequest command) throws CommandCompletionException ;
    
    public CommandResultHolder execute(List<CommandRequest> commands) throws CommandCompletionException ;

    public void execute(List<CommandRequest> commands, CommandCompletionCallback callback);
}
