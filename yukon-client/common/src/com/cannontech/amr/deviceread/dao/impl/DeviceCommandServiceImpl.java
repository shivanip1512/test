package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.DeviceCommandService;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
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
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class DeviceCommandServiceImpl implements DeviceCommandService{

    private static final Logger log = YukonLogManager.getLogger(DeviceCommandServiceImpl.class);
    
    @Autowired private CommandRequestDeviceExecutor deviceExecutor;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private AttributeService attributeService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private ConfigurationSource configurationSource;
    
    private ScheduledExecutorService scheduledExecutorService = null;
    
    private final Set<CompletionCallback> retryCallbacksAwaitingCompletion =
        Collections.synchronizedSet(new HashSet<CompletionCallback>());
       
    @Override
    public CompletionCallback execute(Set<SimpleDevice> devices,
            Set<? extends Attribute> attributes, final String command, DeviceRequestType type, LiteYukonUser user,
            RetryParameters retryParameters, CommandCompletionCallbackAdapter<CommandRequestDevice> taskCallback) {

        setupTimeoutCheck();
        
        CommandHelper parseCommands = new CommandHelper(devices, attributes, command);
        Set<CommandRequestDevice> commands = parseCommands.getCommandRequests();
        Set<YukonPao> unsupported = parseCommands.getUnsupportedDevices();
        
        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, commands.size(), user);
        commandRequestExecutionResultDao.saveUnsupported(unsupported, execution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);
        
        if (log.isDebugEnabled()) {
            if (attributes != null && !attributes.isEmpty()) {
                log.debug("Read by attribute " + logDetails(execution));
            } else if (!StringUtils.isEmpty(command)) {
                log.debug("Read by command" + logDetails(execution));
            } 
            log.debug(logDetails(execution) + " Unsupported devices:" + unsupported);
            log.debug(logDetails(execution) + " Commands:" + commands);
        }
        CompletionCallback callback = new CompletionCallback(execution, retryParameters, taskCallback, commands, user);
        if (commands.isEmpty()) {
            execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
            execution.setStopTime(new Date());
            commandRequestExecutionDao.saveOrUpdate(execution);
            if (log.isDebugEnabled()) {
                log.debug(logDetails(execution) + " No commands to send. Devices are unsupported.");
            }
            taskCallback.complete();
        }
        return callback;
    }

    public class CompletionCallback extends CommandCompletionCallbackAdapter<CommandRequestDevice> {
        private static final String pattern = "MM/dd/yyyy HH:mm:ss.SSS";
        
        private boolean isComplete = false;
        private final Instant creationTime = new Instant();
        private Instant timeoutTime;
        private final CommandRequestExecution execution;
        private CommandCompletionCallbackAdapter<CommandRequestDevice> taskCallback;
        private CommandRequestExecutionTemplate<CommandRequestDevice> template;
        private LiteYukonUser user;
        // retry number, queued true/false
        private final TreeMap<Integer, Boolean> retries = new TreeMap<Integer, Boolean>();
        private TryCallback currentCallback;

        CompletionCallback(CommandRequestExecution execution, RetryParameters retryParameters,
                CommandCompletionCallbackAdapter<CommandRequestDevice> scheduledTaskCallback,
                Set<CommandRequestDevice> commands, LiteYukonUser user) {
            this.execution = execution;
            if (!commands.isEmpty()) {
                this.taskCallback = scheduledTaskCallback;
                this.template = deviceExecutor.getExecutionTemplate(execution, user);
                this.user = user;
                initRetries(retryParameters);
                initTimeoutTime(retryParameters);
                retryCallbacksAwaitingCompletion.add(this);
                //first execution starts with tryNumber=0;
                currentCallback = new TryCallback(0, this, execution, commands);
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
                        log.debug(logDetails(execution) +  " Execution is complete.");
                    }
                    taskCallback.complete();
                }
            }
        }
                           
        public long cancelExecution() {
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
                log.error(logDetails(execution) + " processingExceptionOccured=" + reason);
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
            currentCallback = new TryCallback(retry.getKey(), this, rertyExecution, failedCommands);
            log.debug(logDetails(rertyExecution ) + " try[try number, queued?]="+retry);
            // CommandResultMessageListener will mark retry as complete
            template.execute(new ArrayList<>(failedCommands), currentCallback, rertyExecution, false, retry.getValue());
            //log.debug("*>>>>>>>>>>>>TEST CANCEL/TIMEOUT/PORTER CRASH NOW*");
        }
        
        /**
         * Initializes timeout time.
         * If Retry Strategy Maximum total run-time hours exists stop execution when the retry hours are reached
         * otherwise stop execution when SCHEDULED_REQUEST_MAX_RUN_HOURS is reached.
         * 
         * In DEVELOPMENT_MODE hours are treated as minutes.
         */
        private void initTimeoutTime(RetryParameters retryParameters) {
            boolean isDev = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
            if (retryParameters.getStopRetryAfterHoursCount() > 0) {
                if (isDev) {
                    Duration maxRunMinutes = Duration.standardMinutes(retryParameters.getStopRetryAfterHoursCount());
                    timeoutTime = creationTime.plus(maxRunMinutes);
                } else {
                    Duration maxRunHours = Duration.standardHours(retryParameters.getStopRetryAfterHoursCount());
                    timeoutTime = creationTime.plus(maxRunHours);
                }
                
            } else {
                if (isDev) {
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
                log.debug(logDetails(execution) + " created on " + creationTime.toDateTime().toString(pattern)
                    + ". Timeout set to " + timeoutTime.toDateTime().toString(pattern));
            }
        }

        /**
         * Initializes retries.
         * Example: 
         * queuedTries = 2
         * nonQueuedTries = 5
         * Retries[execution order, queued]={1=true, 2=true, 3=false, 4=false, 5=false, 6=false, 7=false}
         */
        private void initRetries(RetryParameters retryParameters){
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
                log.debug(logDetails(execution) + " Retries[try number, queued]="+retries);
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
        
        public int getContextId(){
            return execution.getContextId();
        }
    }

    private final class TimeoutChecker implements Runnable {
        @Override
        public void run() {
            synchronized (retryCallbacksAwaitingCompletion) {
                try {
                    Iterator<CompletionCallback> iterator = retryCallbacksAwaitingCompletion.iterator();
                    while (iterator.hasNext()) {
                        CompletionCallback callback = iterator.next();
                        callback.checkTimeout();
                        if (callback.isComplete) {
                            iterator.remove();
                            if (log.isDebugEnabled()) {
                                log.debug(logDetails(callback.currentCallback.tryExecution) + " Callback created on "
                                    + callback.creationTime.toDateTime().toString(CompletionCallback.pattern)
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
            scheduledExecutorService.scheduleWithFixedDelay(new TimeoutChecker(), 0, 2, TimeUnit.MINUTES);
        }
    }
    
    private final class CommandHelper {

        private Set<YukonPao> unsupportedDevices = new HashSet<>();
        private Set<CommandRequestDevice> commandRequests = new HashSet<>();

        CommandHelper(Set<SimpleDevice> devices, Set<? extends Attribute> attributes, final String command) {
            if (!StringUtils.isEmpty(command)) {
                List<SimpleDevice> supportedDevicesByCommand = new ArrayList<>();
                for (SimpleDevice device : devices) {
                    if (supportsPorterRequests(device.getDeviceType())) {
                        supportedDevicesByCommand.add(device);
                    } else {
                        unsupportedDevices.add(device.getPaoIdentifier());
                    }
                }
                getCommandRequests().addAll(
                    Lists.transform(supportedDevicesByCommand, new Function<SimpleDevice, CommandRequestDevice>() {

                        @Override
                        public CommandRequestDevice apply(SimpleDevice device) {
                            CommandRequestDevice cmdReq = new CommandRequestDevice();
                            cmdReq.setCommandCallback(new CommandCallbackBase(command));
                            cmdReq.setDevice(device);
                            return cmdReq;
                        }

                    }));
            } else if (attributes != null && !attributes.isEmpty()) {
                PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers =
                    attributeService.findPaoMultiPointIdentifiersForAttributesWithUnsupported(devices, attributes);
                if (meterReadCommandGeneratorService.isReadable(paoPointIdentifiers.getSupportedDevicesAndPoints())) {
                    List<PaoMultiPointIdentifier> supportedDevicesByAttribute = new ArrayList<>();
                    for (YukonPao pao : paoPointIdentifiers.getUnsupportedDevices()) {
                        unsupportedDevices.add(pao.getPaoIdentifier());
                    }
                    for (PaoMultiPointIdentifier identifier : paoPointIdentifiers.getSupportedDevicesAndPoints()) {
                        if (supportsPorterRequests(identifier.getPao().getPaoType())) {
                            supportedDevicesByAttribute.add(identifier);
                        } else {
                            unsupportedDevices.add(identifier.getPao());
                        }
                    }
                    getCommandRequests().addAll(
                        meterReadCommandGeneratorService.getCommandRequests(supportedDevicesByAttribute));
                } else {
                    for (SimpleDevice device : devices) {
                        unsupportedDevices.add(device.getPaoIdentifier());
                    }
                }
            }
        }
        
        /**
         * Returns true if the command for this paoType should be send to porter.
         * RF supports porter requests and it supports the COMMANDER_REQUESTS tag, RF should be excluded for Web Schedules.
         */
        private boolean supportsPorterRequests(PaoType type){
            return !type.isRfn() && paoDefinitionDao.isTagSupported(type, PaoTag.COMMANDER_REQUESTS);
        }
        

        public Set<CommandRequestDevice> getCommandRequests() {
            return commandRequests;
        }

        public Set<YukonPao> getUnsupportedDevices() {
            return unsupportedDevices;
        }
        
    }
   
    final class TryCallback extends CommandCompletionCallbackAdapter<CommandRequestDevice> {
        private final CompletionCallback callback;
        private final CommandRequestExecution tryExecution;
        private final Set<CommandRequestDevice> notResponded = new HashSet<>();
        private final Set<CommandRequestDevice> commandToSend = new HashSet<>();
        private int tryNumber;
  
        private final Set<CommandRequestDevice>  failedCommands = new HashSet<>();

        TryCallback(int tryNumber, CompletionCallback callback, CommandRequestExecution retryExecution, Set<CommandRequestDevice> commands) {
            this.callback = callback;
            this.tryExecution =  retryExecution;
            commandToSend.addAll(commands);
            notResponded.addAll(commands);
            this.tryNumber = tryNumber;
            if (log.isDebugEnabled()) {
                log.debug("STARTED Try=" + tryNumber + " " + logDetails(tryExecution) + " commands=" + commands.size());
            }
        }
        
        @Override
        public void complete() {
            if (log.isDebugEnabled()) {
                log.debug("COMPLETE Try=" + tryNumber + " " + logDetails(tryExecution) + " sent commands="
                    + commandToSend.size() + "  notResponded=" + notResponded.size());
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
            callback.processingExceptionOccured(reason);
        }
        
        public CommandRequestExecution getTryExecution() {
            return tryExecution;
        }
 
        public Set<CommandRequestDevice> getFailedCommands() {
            return failedCommands;
        }

        /**
         * Creates an entry in CommandRequestUnsupported for all devices that didn't respond before the user
         * canceled the request.
         */
        void markDevicesAsCanceled() {
            Set<SimpleDevice> canceledDevices = new HashSet<SimpleDevice>();
            for (CommandRequestDevice command : notResponded) {
                canceledDevices.add(command.getDevice());
            }
            if (log.isDebugEnabled() && !canceledDevices.isEmpty()) {
                log.debug(logDetails(tryExecution) + " Canceled devices:" + canceledDevices);
            }
            commandRequestExecutionResultDao.saveUnsupported(canceledDevices, tryExecution.getId(),
                CommandRequestUnsupportedType.CANCELED);
        }

        /**
         * Creates command execution result for all devices that didn't respond before the timeout time was
         * reached.
         */
        void markDevicesAsTimedOut() {
            for (CommandRequestDevice command : notResponded) {
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(tryExecution.getId(),
                    command.getDevice().getDeviceId(), DeviceError.TIMEOUT.getCode(),
                    command.getCommandCallback().getGeneratedCommand());
            }
        }
        
        /**
         * Completes execution
         * @param status - completion status
         */
        void completeExecution(CommandRequestExecutionStatus status) {
            tryExecution.setCommandRequestExecutionStatus(status);
            tryExecution.setStopTime(new Date());
            commandRequestExecutionDao.saveOrUpdate(tryExecution);
        }
    }
    
    private String logDetails(CommandRequestExecution execution) {
        String details = "Context id=" + execution.getContextId() + " Execution id=" + execution.getId()
            + " Execution Status=" + execution.getCommandRequestExecutionStatus();
        return details;
    }
}
