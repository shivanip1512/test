package com.cannontech.amr.deviceread.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.core.dynamic.PointValueHolder;

public class TryCallback extends CommandCompletionCallbackAdapter<CommandRequestDevice> {

    private static final Logger log = YukonLogManager.getLogger(TryCallback.class);
    
    private final CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    private final CommandRequestExecutionDao commandRequestExecutionDao;

    private final CompletionCallback callback;
    private final CommandRequestExecution tryExecution;
    private final Set<CommandRequestDevice> notResponded = new HashSet<>();
    private final Set<CommandRequestDevice> commandToSend = new HashSet<>();
    private final int tryNumber;

    private final Set<CommandRequestDevice>  failedCommands = new HashSet<>();

    public TryCallback(CommandRequestExecutionDao commandRequestExecutionDao,
            CommandRequestExecutionResultDao commandRequestExecutionResultDao, int tryNumber,
            CompletionCallback callback, CommandRequestExecution tryExecution, Set<CommandRequestDevice> commands) {
        this.callback = callback;
        this.tryExecution = tryExecution;
        this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
        this.commandRequestExecutionDao = commandRequestExecutionDao;
        commandToSend.addAll(commands);
        notResponded.addAll(commands);
        this.tryNumber = tryNumber;
        //if (log.isDebugEnabled()) {
            log.debug("STARTED " + getLogDetails() + " commands=" + commands.size());
        //}
    }

    @Override
    public void complete() {
        if (log.isDebugEnabled()) {
            log.debug("COMPLETE " + getLogDetails() + " sent commands=" + commandToSend.size() + "  notResponded="
                + notResponded.size());
        }
        callback.complete();
    }

    @Override
    public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
        notResponded.remove(command);  
        failedCommands.add(command);
    }

    @Override
    public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
        notResponded.remove(command);
    } 
    
    @Override
    public void processingExceptionOccured(String reason) {
        log.error(getLogDetails() + " processingExceptionOccured=" + reason);
        callback.processingExceptionOccured(reason);
    }
    /**
     * Creates an entry in CommandRequestUnsupported for all devices that didn't respond before the user
     * canceled the request.
     */
    public void markDevicesAsCanceled() {
        Set<SimpleDevice> canceledDevices = new HashSet<SimpleDevice>();
        for (CommandRequestDevice command : notResponded) {
            canceledDevices.add(command.getDevice());
        }
        if (log.isDebugEnabled() && !canceledDevices.isEmpty()) {
            log.debug(getLogDetails() + " Canceled devices:" + canceledDevices);
        }
        commandRequestExecutionResultDao.saveUnsupported(canceledDevices, tryExecution.getId(),
            CommandRequestUnsupportedType.CANCELED);
    }

    /**
     * Creates command execution result for all devices that didn't respond before the timeout time was
     * reached.
     */
    public void markDevicesAsTimedOut() {
        for (CommandRequestDevice command : notResponded) {
            commandRequestExecutionResultDao.saveCommandRequestExecutionResult(tryExecution.getId(),
                command.getDevice().getDeviceId(), DeviceError.TIMEOUT.getCode(),
                command.getCommandCallback().getGeneratedCommand());
        }
    }

    /**
     * Completes execution
     * 
     * @param status - completion status
     */
    public void completeExecution(CommandRequestExecutionStatus status) {
        tryExecution.setCommandRequestExecutionStatus(status);
        tryExecution.setStopTime(new Date());
        commandRequestExecutionDao.saveOrUpdate(tryExecution);
    }

    public String getLogDetails() {
        String details = "Context id=" + tryExecution.getContextId() + " Execution id=" + tryExecution.getId()
            + " Try number=" + tryNumber + " Execution Status=" + tryExecution.getCommandRequestExecutionStatus();
        return details;
    }
        
    public Set<CommandRequestDevice> getFailedCommands() {
        return failedCommands;
    }
}