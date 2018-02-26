package com.cannontech.common.device.commands.service;

import java.util.List;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestBase;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandExecutionService {

    /**
     * Sends command to porter. Creates and completes command request execution.
     * 
     * @throws CommandCompletionException
     */
    CommandResultHolder execute(CommandRequestBase command, DeviceRequestType type, LiteYukonUser user)
            throws CommandCompletionException;


    /**
     * Sends multiple commands to porter. Creates and completes command request execution.
     */
    CommandRequestExecutionIdentifier execute(List<? extends CommandRequestBase> commands,
            CommandCompletionCallback<? extends CommandRequestBase> callback, DeviceRequestType type, LiteYukonUser user);

    /**
     * Sends multiple commands to porter. Uses command request execution already created.
     * 
     * @param execution - Created command request execution.
     * @param updateExecutionStatus - if true execution status will be updated after completion.
     */
    CommandRequestExecutionIdentifier execute(List<? extends CommandRequestBase> commands,
            CommandCompletionCallback<? extends CommandRequestBase> callback, CommandRequestExecution execution,
            boolean updateExecutionStatus, LiteYukonUser user);

    /**
     * Sends multiple commands to porter. Uses command request execution already created.
     * 
     * @param execution - Created command request execution.
     * @param updateExecutionStatus - if true execution status will be updated after completion.
     * @param Boolean noqueue - if noqueue is true appends noqueue to the command, if noqueue is null
     *        uses @link CommandRequestExecutionDefaultsis#isNoqueue(DeviceRequestType type)} to determine if
     *        the value is true or false.
     */
    CommandRequestExecutionIdentifier execute(List<? extends CommandRequestBase> commands,
            CommandCompletionCallback<? extends CommandRequestBase> callback, CommandRequestExecution execution,
            boolean updateExecutionStatus, Boolean noqueue, LiteYukonUser user);

    /**
     * Sends cancellation request to porter.
     * 
     * @param updateExecutionStatus - if true sets status to CANCELED for command request execution.
     * @return number of commands that we attempted to cancel
     */
    long cancelExecution(CommandCompletionCallback<?> callback, LiteYukonUser user, boolean updateExecutionStatus);
}
