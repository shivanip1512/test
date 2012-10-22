package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to execute command requests
 * @param <T> - Type of command request to execute
 */
public interface CommandRequestExecutor<T> {

    /**
     * Method to execute a command request for a given user
     * @param command - Command request to execute
     * @param user - User executing the command
     * @return Results of executing command
     * @throws CommandCompletionException
     */
    public CommandResultHolder execute(T command, DeviceRequestType type, LiteYukonUser user)
            throws CommandCompletionException;

    /**
     * Method to execute multiple command request for a given user (this method
     * will block until command execution is complete)
     * @param commands - List of command request to execute
     * @param user - User executing the commands
     * @return Results of executing commands
     * @throws CommandCompletionException
     */
    public CommandResultHolder execute(List<T> commands, DeviceRequestType type, LiteYukonUser user)
            throws CommandCompletionException;

    /**
     * Method to execute multiple command request for a given user (this method
     * will NOT block until command execution is complete).
     * @param commands - Commands to execute
     * @param callback - Callback which will be called as the commands execute
     * @param user - User executing the commands
     * @return The CommandRequestExecution ID. The ID of the CommandRequestExecution database table record where command results can be found.
     */
    public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback,
    		DeviceRequestType type, LiteYukonUser user);
    
    public CommandRequestExecutionIdentifier executeWithParameterDto(List<T> commands, CommandCompletionCallback<? super T> callback, 
            CommandRequestExecutionParameterDto context);
    
    /**
     * Attempts to cancel all remaining commands handled by the specified callback and return the number of commands cancled.
     * If the callback has not already completed, the {@link CommandCompletionCallback#cancel()} and {@link CommandCompletionCallback#complete()}
     * methods are called in that order. 
     */
    public long cancelExecution(CommandCompletionCallback<? super T> callback, LiteYukonUser user);
    
    public CommandRequestExecutionTemplate<T> getExecutionTemplate(DeviceRequestType type, final LiteYukonUser user);
}
