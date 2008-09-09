package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
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
     * @throws PaoAuthorizationException - When user doesn't have permission to
     *             execute the command
     */
    public CommandResultHolder execute(T command, LiteYukonUser user)
            throws CommandCompletionException, PaoAuthorizationException;

    /**
     * Method to execute multiple command request for a given user (this method
     * will block until command execution is complete)
     * @param commands - List of command request to execute
     * @param user - User executing the commands
     * @return Results of executing commands
     * @throws CommandCompletionException
     * @throws PaoAuthorizationException - When user doesn't have permission to
     *             execute the commands
     */
    public CommandResultHolder execute(List<T> commands, LiteYukonUser user)
            throws CommandCompletionException, PaoAuthorizationException;

    /**
     * Method to execute multiple command request for a given user (this method
     * will NOT block until command execution is complete).
     * @param commands - Commands to execute
     * @param callback - Callback which will be called as the commands execute
     * @param user - User executing the commands
     * @throws PaoAuthorizationException - When user doesn't have permission to
     *             execute the commands
     */
    public void execute(List<T> commands, CommandCompletionCallback<? super T> callback,
            LiteYukonUser user) throws PaoAuthorizationException;
    
    /**
     * 
     * @param commands
     * @param commandRequestToken
     * @param user
     * @return
     */
    public long cancelExecution(CommandCompletionCallback<? super T> callback, LiteYukonUser user);
}
