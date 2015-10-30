package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.CommandHelper;
import com.cannontech.amr.deviceread.dao.CommandHelper.ParsedCommands;
import com.cannontech.amr.deviceread.dao.DeviceCommandService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.events.loggers.MeteringEventLogService;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class DeviceCommandServiceImpl implements DeviceCommandService {

    private static final Logger log = YukonLogManager.getLogger(DeviceCommandServiceImpl.class);

    @Autowired private CommandHelper commandHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private CommandRequestDeviceExecutor deviceExecutor;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private MeteringEventLogService eventLogService;

    private ScheduledExecutorService scheduledExecutorService = null;

    private final Set<CompletionCallback> callbacksAwaitingCompletion =
        Collections.synchronizedSet(new HashSet<CompletionCallback>());

    @Override
    public CompletionCallback execute(Set<SimpleDevice> devices, Set<? extends Attribute> attributes,
            final String command, DeviceRequestType type, LiteYukonUser user, RetryParameters retryParameters,
            CommandCompletionCallbackAdapter<CommandRequestDevice> taskCallback, String scheduleName) {

        setupTimeoutCheck();

        ParsedCommands parsedCommands = commandHelper.parseCommands(devices, attributes, command);
        Set<CommandRequestDevice> commands = parsedCommands.getCommands();
        Set<YukonPao> unsupported = parsedCommands.getUnsupportedDevices();

        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, commands.size(), user);
        commandRequestExecutionResultDao.saveUnsupported(unsupported, execution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);

        if (log.isDebugEnabled()) {
            log.debug("Read started type= " + type + " Context id=" + execution.getContextId());
            log.debug("Context id=" + execution.getContextId() + " Unsupported devices:" + unsupported);
            log.debug("Context id=" + execution.getContextId() + " Commands:" + commands);
        }

        return new CompletionCallback(execution, retryParameters, taskCallback, commands, scheduleName, user);
    }

    public class CompletionCallback extends CommandCompletionCallbackAdapter<CommandRequestDevice> {

        public static final String dateFormatDebugPattern = "MM/dd/yyyy HH:mm:ss.SSS";

        private boolean isComplete = false;
        private final Instant creationTime = new Instant();
        private Instant timeoutTime;
        private CommandCompletionCallbackAdapter<CommandRequestDevice> taskCallback;
        private CommandRequestExecutionTemplate<CommandRequestDevice> template;
        // retry number, queued true/false
        private final TreeMap<Integer, Boolean> retries = new TreeMap<Integer, Boolean>();
        private TryCallback currentCallback;
        private String scheduleName;
        private int contextId;
        //Name used for event log
        private String deviceRequestTypeName;

        public CompletionCallback(CommandRequestExecution execution, RetryParameters retryParameters,
                CommandCompletionCallbackAdapter<CommandRequestDevice> scheduledTaskCallback,
                Set<CommandRequestDevice> commands, String scheduleName, LiteYukonUser user) {
            this.scheduleName = scheduleName;
            this.taskCallback = scheduledTaskCallback;
            this.contextId = execution.getContextId();
            this.deviceRequestTypeName = execution.getCommandRequestExecutionType().getShortName();
            if (!commands.isEmpty()) {
                callbacksAwaitingCompletion.add(this);
                this.template = deviceExecutor.getExecutionTemplate(execution, user);
                initRetries(retryParameters);
                initTimeoutTime(retryParameters);
                eventLogService.readStarted(deviceRequestTypeName, scheduleName, creationTime, timeoutTime,
                    commands.size(), contextId);
                // first execution starts with tryNumber=0;
                currentCallback = new TryCallback(0, this, execution, commands, scheduleName);
                template.execute(new ArrayList<>(commands), currentCallback, execution, false);
            } else {
                eventLogService.readAttempted(deviceRequestTypeName, scheduleName, creationTime, contextId);
                // no commands to,  send complete the execution
                execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
                execution.setStopTime(new Date());
                commandRequestExecutionDao.saveOrUpdate(execution);
                if (log.isDebugEnabled()) {
                    log.debug(
                        "Context id=" + execution.getContextId() + " No commands to send. Devices are unsupported.");
                }
                taskCallback.complete();
            }
        }

        @Override
        public void complete() {
            if (!isComplete) {
                if (isRetriable()) {
                    retry();
                } else {
                    isComplete = true;
                    eventLogService.readCompleted(deviceRequestTypeName, scheduleName, contextId);
                    // CommandResultMessageListener will mark execution as complete
                    if (log.isDebugEnabled()) {
                        log.debug("Context id=" + contextId + " Execution is complete.");
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
                eventLogService.readCancelled(deviceRequestTypeName, scheduleName, currentCallback.tryNumber, user,
                    contextId, currentCallback.getExecutionId());
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
                eventLogService.readTimeout(deviceRequestTypeName, scheduleName, currentCallback.tryNumber, contextId,
                    currentCallback.getExecutionId());
                taskCallback.complete();
            }
        }

        @Override
        public void processingExceptionOccured(String reason) {
            if (!isComplete) {
                isComplete = true;
                currentCallback.completeExecution(CommandRequestExecutionStatus.FAILED);
                eventLogService.readFailed(deviceRequestTypeName, scheduleName, reason, currentCallback.tryNumber, contextId,
                    currentCallback.getExecutionId());
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
            // try number, queued(true/false)
            Entry<Integer, Boolean> retry = retries.firstEntry();
            retries.remove(retry.getKey());
            CommandRequestExecution retryExecution = new CommandRequestExecution();
            retryExecution.setCommandRequestType(currentCallback.tryExecution.getCommandRequestType());
            retryExecution.setCommandRequestExecutionType(
                currentCallback.tryExecution.getCommandRequestExecutionType());
            retryExecution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.STARTED);
            retryExecution.setRequestCount(failedCommands.size());
            retryExecution.setContextId(contextId);
            retryExecution.setStartTime(new Date());
            retryExecution.setUserName(currentCallback.tryExecution.getUserName());
            commandRequestExecutionDao.saveOrUpdate(retryExecution);
            currentCallback = new TryCallback(retry.getKey(), this, retryExecution, failedCommands, scheduleName);
            log.debug(currentCallback.getLogDetails() + " try[try number, queued?]=" + retry);
            // CommandResultMessageListener will mark retry as complete
            template.execute(new ArrayList<>(failedCommands), currentCallback, retryExecution, false, retry.getValue());
            // log.debug("*>>>>>>>>>>>>TEST CANCEL/TIMEOUT/PORTER CRASH NOW*");
        }

        /**
         * Initializes timeout time.
         * If Retry Strategy Maximum total run-time hours exists stop execution when the retry hours are reached
         * otherwise stop execution when SCHEDULED_REQUEST_MAX_RUN_HOURS is reached.
         * 
         * If SCHEDULED_REQUEST_TIMEOUT_IN_MINUTES=true hours are treated as minutes.
         * SCHEDULED_REQUEST_TIMEOUT_IN_MINUTES
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
                log.debug("Context id=" + contextId + " created on "
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
                log.debug("Context id=" + contextId + " Retries[try number, queued]=" + retries);
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
            return this.contextId;
        }
    }
    
    public class TryCallback extends CommandCompletionCallbackAdapter<CommandRequestDevice> {

        private final CompletionCallback callback;
        private final CommandRequestExecution tryExecution;
        private final Set<CommandRequestDevice> notResponded = new HashSet<>();
        private final Set<CommandRequestDevice> commandsToSend = new HashSet<>();
        private final int tryNumber;
        private final String scheduleName;
        private boolean processingExceptionOccured= false;

        private final Set<CommandRequestDevice> failedCommands = new HashSet<>();

        public TryCallback(int tryNumber, CompletionCallback callback, CommandRequestExecution tryExecution,
                Set<CommandRequestDevice> commands, String scheduleName) {
            this.callback = callback;
            this.tryExecution = tryExecution;
            commandsToSend.addAll(commands);
            notResponded.addAll(commands);
            this.tryNumber = tryNumber;
            this.scheduleName = scheduleName;
            if (log.isDebugEnabled()) {
                log.debug("STARTED " + getLogDetails() + " commands=" + commands.size());
            }
            eventLogService.tryStarted(tryExecution.getCommandRequestExecutionType().getShortName(), scheduleName,
                tryNumber, commands.size(), tryExecution.getContextId(), getExecutionId());
        }

        @Override
        public void complete() {
            if (!processingExceptionOccured) {
                if (log.isDebugEnabled()) {
                    log.debug("COMPLETE " + getLogDetails() + " sent commands=" + commandsToSend.size() + "  failed="
                        + failedCommands.size());
                }
                eventLogService.tryCompleted(tryExecution.getCommandRequestExecutionType().getShortName(), scheduleName,
                    tryNumber, failedCommands.size(), tryExecution.getContextId(), getExecutionId());
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
            processingExceptionOccured = true;
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
        

        public int getExecutionId() {
            return tryExecution.getId();
        }
    }
    
    private final class TimeoutChecker implements Runnable {
        @Override
        public void run() {
            synchronized (callbacksAwaitingCompletion) {
                try {
                    Iterator<CompletionCallback> iterator = callbacksAwaitingCompletion.iterator();
                    while (iterator.hasNext()) {
                        CompletionCallback callback = iterator.next();
                        callback.checkTimeout();
                        if (callback.isComplete) {
                            iterator.remove();
                            if (log.isDebugEnabled()) {
                                log.debug(" Context id=" + callback.getContextId() + " Callback created on "
                                    + callback.creationTime.toDateTime().toString(
                                        CompletionCallback.dateFormatDebugPattern)
                                    + " is removed from retryCallbacksAwaitingCompletion list");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    private synchronized void setupTimeoutCheck() {
        if (scheduledExecutorService == null) {
            log.debug("Setup timeout check");
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            // runs every 5 minutes.
            scheduledExecutorService.scheduleWithFixedDelay(new TimeoutChecker(), 0, 5, TimeUnit.MINUTES);
        }
    }
}
