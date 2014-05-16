package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
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
     * will NOT block until command execution is complete).
     * @param commands - Commands to execute
     * @param callback - Callback which will be called as the commands execute
     * @param user - User executing the commands
     * @return The CommandRequestExecution ID. The ID of the CommandRequestExecution database table record where command results can be found.
     */
    public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback,
    		DeviceRequestType type, LiteYukonUser user);

    /**
     * Attempts to cancel all remaining commands handled by the specified callback and return the
     * number of commands canceled.
     * If the callback has not already completed, the {@link CommandCompletionCallback#cancel()} and
     * {@link CommandCompletionCallback#complete()} methods are called in that order.
     * 
     * @param updateExecutionStatus - If set to "true" the execution status will be set to
     *            CANCELLED. If set to "false" the execution status will not be unchanged.
     *            
     *            Example: Before calling this method execution status is set to CANCELLING. This
     *            method only works on PLC devices. If the attempt is made
     *            to cancel the execution for both RF and PLC devices, the status needs to
     *            be set to CANCELLED after cancellation is done for RF and PLC devices.
     *            If updateExecutionStatus is set to "false" the calling program needs to update the
     *            status to CANCELLED.
     */
    public long cancelExecution(CommandCompletionCallback<? super T> callback, LiteYukonUser user, boolean updateExecutionStatus);
    
    public CommandRequestExecutionTemplate<T> getExecutionTemplate(DeviceRequestType type, final LiteYukonUser user);

    /*
     * This method is used to execute commands. CommandRequestExecution should be created prior to calling this method.
     */
    public CommandRequestExecutionIdentifier executeWithParameterDto(List<T> commands,
                                                              CommandCompletionCallback<? super T> callback,
                                                              CommandRequestExecutionParameterDto parameterDto,
                                                              CommandRequestExecution execution);

}
