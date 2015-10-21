package com.cannontech.amr.deviceread.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class CompletionCallback extends CommandCompletionCallbackAdapter<CommandRequestDevice> {

    private static final Logger log = YukonLogManager.getLogger(CompletionCallback.class);

    public static final String dateFormatDebugPattern = "MM/dd/yyyy HH:mm:ss.SSS";

    private final CommandRequestExecutionDao commandRequestExecutionDao;
    private final GlobalSettingDao globalSettingDao;
    private final ConfigurationSource configurationSource;
    private final CommandRequestDeviceExecutor deviceExecutor;
    private final CommandRequestExecutionResultDao commandRequestExecutionResultDao;

    private boolean isComplete = false;
    private final Instant creationTime = new Instant();
    private Instant timeoutTime;
    private final CommandRequestExecution execution;
    private CommandCompletionCallbackAdapter<CommandRequestDevice> taskCallback;
    private CommandRequestExecutionTemplate<CommandRequestDevice> template;
    // retry number, queued true/false
    private final TreeMap<Integer, Boolean> retries = new TreeMap<Integer, Boolean>();
    private TryCallback currentCallback;

    public CompletionCallback(CommandRequestDeviceExecutor deviceExecutor,
            CommandRequestExecutionDao commandRequestExecutionDao,
            CommandRequestExecutionResultDao commandRequestExecutionResultDao, GlobalSettingDao globalSettingDao,
            ConfigurationSource configurationSource, CommandRequestExecution execution, RetryParameters retryParameters,
            CommandCompletionCallbackAdapter<CommandRequestDevice> scheduledTaskCallback,
            Set<CommandRequestDevice> commands, LiteYukonUser user) {
        this.execution = execution;
        this.commandRequestExecutionDao = commandRequestExecutionDao;
        this.globalSettingDao = globalSettingDao;
        this.configurationSource = configurationSource;
        this.deviceExecutor = deviceExecutor;
        this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
        if (!commands.isEmpty()) {
            this.taskCallback = scheduledTaskCallback;
            this.template = deviceExecutor.getExecutionTemplate(execution, user);
            initRetries(retryParameters);
            initTimeoutTime(retryParameters);
            // first execution starts with tryNumber=0;
            currentCallback = new TryCallback(commandRequestExecutionDao, commandRequestExecutionResultDao, 0, this,
                execution, commands);
            template.execute(new ArrayList<>(commands), currentCallback, execution, false);
        }
    }

    @Override
    public void complete() {
        if (!isComplete) {
            if (isRetriable()) {
                retry();
            } else {
                isComplete = true;
                // CommandResultMessageListener will mark execution as complete
                if (log.isDebugEnabled()) {
                    log.debug("Context id=" + execution.getContextId() + " Execution is complete.");
                }
                taskCallback.complete();
            }
        }
    }

    public long cancelExecution(LiteYukonUser user) {
        long commands = 0;
        if (!isComplete) {
            isComplete = true;
            commands = deviceExecutor.cancelExecution(currentCallback, user, false);
            currentCallback.completeExecution(CommandRequestExecutionStatus.CANCELLED);
            currentCallback.markDevicesAsCanceled();
            taskCallback.complete();
        }
        return commands;
    }

    /**
     * Completes the execution if the timeout time was reached.
     */
    public void checkTimeout() {
        if (!isComplete && new Instant().isAfter(timeoutTime)) {
            isComplete = true;
            currentCallback.completeExecution(CommandRequestExecutionStatus.COMPLETE);
            currentCallback.markDevicesAsTimedOut();
            taskCallback.complete();
        }
    }

    @Override
    public void processingExceptionOccured(String reason) {
        if (!isComplete) {
            isComplete = true;
            currentCallback.completeExecution(CommandRequestExecutionStatus.FAILED);
            taskCallback.complete();
        }
    }

    /**
     * Returns true if the execution can be retried.
     */
    private boolean isRetriable() {
        return !retries.isEmpty() && !currentCallback.getFailedCommands().isEmpty();
    }

    /**
     * Attempts to re-send commands to failed devices.
     */
    private void retry() {
        Set<CommandRequestDevice> failedCommands = currentCallback.getFailedCommands();
        //try number, queued(true/false)
        Entry<Integer, Boolean> retry = retries.firstEntry();
        retries.remove(retry.getKey());
        CommandRequestExecution rertyExecution = new CommandRequestExecution();
        rertyExecution.setCommandRequestType(execution.getCommandRequestType());
        rertyExecution.setCommandRequestExecutionType(execution.getCommandRequestExecutionType());
        rertyExecution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.STARTED);
        rertyExecution.setRequestCount(failedCommands.size());
        rertyExecution.setContextId(execution.getContextId());
        rertyExecution.setStartTime(new Date());
        rertyExecution.setUserName(execution.getUserName());
        commandRequestExecutionDao.saveOrUpdate(rertyExecution);
        currentCallback = new TryCallback(commandRequestExecutionDao, commandRequestExecutionResultDao, retry.getKey(),
            this, rertyExecution, failedCommands);
        log.debug(currentCallback.getLogDetails() + " try[try number, queued?]=" + retry);
        // CommandResultMessageListener will mark retry as complete
        template.execute(new ArrayList<>(failedCommands), currentCallback, rertyExecution, false, retry.getValue());
        // log.debug("*>>>>>>>>>>>>TEST CANCEL/TIMEOUT/PORTER CRASH NOW*");
    }

    /**
     * Initializes timeout time.
     * If Retry Strategy Maximum total run-time hours exists stop execution when the retry hours are reached
     * otherwise stop execution when SCHEDULED_REQUEST_MAX_RUN_HOURS is reached.
     * 
     * If SCHEDULED_REQUEST_TIMEOUT_IN_MINUTES=true hours are treated as minutes. SCHEDULED_REQUEST_TIMEOUT_IN_MINUTES
     * default is false.
     */
    private void initTimeoutTime(RetryParameters retryParameters) {
        boolean isInMinutes =
            configurationSource.getBoolean(MasterConfigBoolean.SCHEDULED_REQUEST_TIMEOUT_IN_MINUTES, false);
        if (retryParameters.getStopRetryAfterHoursCount() > 0) {
            if (isInMinutes) {
                Duration maxRunMinutes = Duration.standardMinutes(retryParameters.getStopRetryAfterHoursCount());
                timeoutTime = creationTime.plus(maxRunMinutes);
            } else {
                Duration maxRunHours = Duration.standardHours(retryParameters.getStopRetryAfterHoursCount());
                timeoutTime = creationTime.plus(maxRunHours);
            }

        } else {
            if (isInMinutes) {
                Duration maxRunMinutes = Duration.standardMinutes(
                    globalSettingDao.getInteger(GlobalSettingType.SCHEDULED_REQUEST_MAX_RUN_HOURS));
                timeoutTime = creationTime.plus(maxRunMinutes);
            } else {
                Duration maxRunHours = Duration.standardHours(
                    globalSettingDao.getInteger(GlobalSettingType.SCHEDULED_REQUEST_MAX_RUN_HOURS));
                timeoutTime = creationTime.plus(maxRunHours);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Context id=" + execution.getContextId() + " created on "
                + creationTime.toDateTime().toString(dateFormatDebugPattern) + ". Timeout set to "
                + timeoutTime.toDateTime().toString(dateFormatDebugPattern));
        }
    }

    /**
     * Initializes retries.
     * Example:
     * queuedTries = 2
     * nonQueuedTries = 5
     * Retries[execution order, queued]={1=true, 2=true, 3=false, 4=false, 5=false, 6=false, 7=false}
     */
    private void initRetries(RetryParameters retryParameters) {
        int queuedTries = retryParameters.getQueuedTries();
        int nonQueuedTries = retryParameters.getNonQueuedTries();
        int counter = 1;
        while (queuedTries-- > 0) {
            retries.put(counter++, true);
        }

        while (nonQueuedTries-- > 0) {
            retries.put(counter++, false);
        }
        if (log.isDebugEnabled()) {
            log.debug("Context id=" + execution.getContextId() + " Retries[try number, queued]=" + retries);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        CompletionCallback other = (CompletionCallback) obj;
        if (creationTime == null) {
            if (other.creationTime != null)
                return false;
        } else if (!creationTime.equals(other.creationTime))
            return false;
        return true;
    }

    public int getContextId() {
        return execution.getContextId();
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
