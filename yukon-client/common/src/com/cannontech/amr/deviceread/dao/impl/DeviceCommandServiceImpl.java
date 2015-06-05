package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.commands.impl.CommandRequestRetryExecutor;
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
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DeviceCommandServiceImpl implements DeviceCommandService{

    private static final Logger log = YukonLogManager.getLogger(DeviceCommandServiceImpl.class);
    
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private AttributeService attributeService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    private ScheduledExecutorService executor = null;
    
    private final Set<RetryCallback> retryCallbacksAwaitingCompletion =
        Collections.synchronizedSet(new HashSet<RetryCallback>());

    @Override
    public CommandRequestExecutionObjects<CommandRequestDevice> execute(Set<SimpleDevice> devices,
            Set<? extends Attribute> attributes, final String command, DeviceRequestType type, LiteYukonUser user,
            RetryParameters retryParameters, CommandCompletionCallbackAdapter<CommandRequestDevice> callback) {

        Set<YukonPao> unsupportedDevices = new HashSet<>();
        List<SimpleDevice> supportedDevices = new ArrayList<>();
        List<CommandRequestDevice> commandRequests = new ArrayList<>();

        if (!StringUtils.isEmpty(command)) {
            List<SimpleDevice> supportedDevicesByCommand = new ArrayList<>();
            for (SimpleDevice device : devices) {
                if (supportsPorterRequests(device.getDeviceType())) {
                    supportedDevicesByCommand.add(device);
                } else {
                    unsupportedDevices.add(device);
                }
            }
            commandRequests.addAll(Lists.transform(supportedDevicesByCommand,
                new Function<SimpleDevice, CommandRequestDevice>() {

                    @Override
                    public CommandRequestDevice apply(SimpleDevice device) {
                        CommandRequestDevice cmdReq = new CommandRequestDevice();
                        cmdReq.setCommandCallback(new CommandCallbackBase(command));
                        cmdReq.setDevice(device);
                        return cmdReq;
                    }

                }));
            supportedDevices.addAll(supportedDevicesByCommand);
        } else if (attributes != null && !attributes.isEmpty()) {
            PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers =
                attributeService.findPaoMultiPointIdentifiersForAttributesWithUnsupported(devices, attributes);
            if (meterReadCommandGeneratorService.isReadable(paoPointIdentifiers.getSupportedDevicesAndPoints())) {
                List<PaoMultiPointIdentifier> supportedDevicesByAttribute = new ArrayList<>();
                unsupportedDevices = paoPointIdentifiers.getUnsupportedDevices();
                for (PaoMultiPointIdentifier identifier : paoPointIdentifiers.getSupportedDevicesAndPoints()) {
                    if (supportsPorterRequests(identifier.getPao().getPaoType())) {
                        supportedDevicesByAttribute.add(identifier);
                    } else {
                        unsupportedDevices.add(identifier.getPao());
                    }
                }
                commandRequests.addAll(meterReadCommandGeneratorService.getCommandRequests(supportedDevicesByAttribute));
                supportedDevices.addAll(Lists.transform(supportedDevicesByAttribute,
                    new Function<PaoMultiPointIdentifier, SimpleDevice>() {
                        @Override
                        public SimpleDevice apply(PaoMultiPointIdentifier device) {
                            return new SimpleDevice(device.getPao());
                        }
                    }));
            } else {
                for (SimpleDevice device : devices) {
                    unsupportedDevices.add(device);
                }
            }
        } else {
            throw new UnsupportedOperationException("A command string or attribute is required to initiate read");
        }

        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, 0, user);

        if (log.isDebugEnabled()) {
            if (attributes != null && !attributes.isEmpty()) {
                log.debug("initiate read by attribute");
            } else if (!StringUtils.isEmpty(command)) {
                log.debug("initiate read by command");
            } 
            log.debug("Execution id:" + execution.getId());
            log.debug("Unsupported devices:" + unsupportedDevices);
            log.debug("Commands:" + commandRequests);
        }

        commandRequestExecutionResultDao.saveUnsupported(unsupportedDevices, execution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);

        CommandRequestExecutionObjects<CommandRequestDevice> executionObjects = null;
       
        RetryCallback retryCallback =
            new RetryCallback(supportedDevices, execution, retryParameters.getStopRetryAfterDate(), callback,
                commandRequests);
        if (!commandRequests.isEmpty()) {
            execution.setRequestCount(commandRequests.size());
            commandRequestExecutionDao.saveOrUpdate(execution);
            CommandRequestRetryExecutor<CommandRequestDevice> retryExecutor =
                new CommandRequestRetryExecutor<>(commandRequestDeviceExecutor, retryParameters, retryCallback, execution, user);
            retryCallbacksAwaitingCompletion.add(retryCallback);
            setupTimeoutCheck();
            executionObjects = retryExecutor.getExecutionObjects();
            retryExecutor.execute(commandRequests);
        } else {
            executionObjects = new CommandRequestExecutionObjects<CommandRequestDevice>(null, null, execution);
            retryCallback.complete();
        }
                
        return executionObjects;
    }
    

    
    private class RetryCallback implements CommandCompletionCallback<CommandRequestDevice> {
        private static final String pattern = "MM/dd/yyyy HH:mm:ss.SSS";
        
        private boolean isComplete = false;
        private boolean isTimeout = false;
        private boolean isCanceled = false;
        private final Instant creationTime;
        private final Instant timeoutTime;
        private final Set<SimpleDevice> respondedDevices = new HashSet<>();
        private final List<SimpleDevice> timeoutDevices = new ArrayList<>();
        private final CommandRequestExecution execution;
        private final CommandCompletionCallbackAdapter<CommandRequestDevice> callback;
        private final List<CommandRequestDevice> commandRequests;

        RetryCallback(List<SimpleDevice> allDevices, CommandRequestExecution execution,
                Date stopRetryAfterDate, CommandCompletionCallbackAdapter<CommandRequestDevice> callback,
                List<CommandRequestDevice> commandRequests) {
            timeoutDevices.addAll(allDevices);
            creationTime = new Instant();
            /*
             * If Retry Strategy Maximum total run-time hours exists stop execution when the retry hours is reached
             * otherwise stop execution when SCHEDULED_REQUEST_MAX_RUN_HOURS is reached.
             */
            if (stopRetryAfterDate != null) {
                timeoutTime = new Instant(stopRetryAfterDate);
            } else {
                Duration maxRunHours =
                    Duration.standardHours(globalSettingDao.getInteger(GlobalSettingType.SCHEDULED_REQUEST_MAX_RUN_HOURS));
                timeoutTime = creationTime.plus(maxRunHours);
            }

            if (log.isDebugEnabled()) {
                log.debug("Execution id=" + execution.getId() + " created on"
                    + creationTime.toDateTime().toString(pattern) + ". Timeout set to "
                    + timeoutTime.toDateTime().toString(pattern));
            }
            this.execution = execution;
            this.callback = callback;
            this.commandRequests = commandRequests;
        }

        @Override
        public void complete() {
            if (!isComplete) {
                isComplete = true;
                if (isCanceled) {
                    if (log.isDebugEnabled()) {
                        log.debug("Execution id=" + execution.getId() + " on "
                            + creationTime.toDateTime().toString(pattern) + " is canceled by the user.");
                    }
                    execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.CANCELLED);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Execution id=" + execution.getId() + " callback created on "
                            + creationTime.toDateTime().toString(pattern) + " completed at "
                            + new Instant().toDateTime().toString(pattern));
                        log.debug("Responded devices " + respondedDevices.size());
                        if (isTimeout) {
                            log.debug("Execution id=" + execution.getId() + " callback timed out.");
                            log.debug("Timed out was set to " + timeoutTime.toDateTime().toString(pattern));
                        } else {
                            log.debug("Execution id=" + execution.getId() + " on "
                                + creationTime.toDateTime().toString(pattern) + " completed successfully.");
                        }
                    }
                    execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
                }
                execution.setStopTime(new Date());
                commandRequestExecutionDao.saveOrUpdate(execution);
                callback.complete();
            }
        }

        @Override
        public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
            respondedDevices.add(command.getDevice());
        }

        @Override
        public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
            respondedDevices.add(command.getDevice());
        }

        @Override
        public void receivedLastResultString(CommandRequestDevice command, String value) {
            respondedDevices.add(command.getDevice());
        }
        
        @Override
        public void cancel() {
            isCanceled = true;
            complete();
        }

        public void checkTimeout() {
            Instant now = new Instant();
            if (!isComplete && !isTimeout && now.isAfter(timeoutTime)) {
                isTimeout = true;
                timeoutDevices.removeAll(new ArrayList<SimpleDevice>(respondedDevices));
                if (log.isDebugEnabled() && !timeoutDevices.isEmpty()) {
                    log.debug("Timed out devices:" + timeoutDevices);
                }
                
                for (SimpleDevice device : timeoutDevices) {
                    Iterable<CommandRequestDevice> commands =
                        Iterables.filter(commandRequests, new Predicate<CommandRequestDevice>() {
                            @Override
                            public boolean apply(CommandRequestDevice command) {
                                return command.getDevice().equals(device);
                            }
                        });
                    for (CommandRequestDevice command : commands) {
                        commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution.getId(),
                            device.getDeviceId(), DeviceError.TIMEOUT.getCode(),
                            command.getCommandCallback().getGeneratedCommand());
                    }
                }
                complete();
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
            RetryCallback other = (RetryCallback) obj;
            if (creationTime == null) {
                if (other.creationTime != null)
                    return false;
            } else if (!creationTime.equals(other.creationTime))
                return false;
            return true;
        }

        @Override
        public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
        }

        @Override
        public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
        }

        @Override
        public void processingExceptionOccured(String reason) {
            complete();
        }       
    }

    private class TimeoutChecker implements Runnable {
        @Override
        public void run() {
            synchronized (retryCallbacksAwaitingCompletion) {
                try {
                    Iterator<RetryCallback> iterator = retryCallbacksAwaitingCompletion.iterator();
                    while (iterator.hasNext()) {
                        RetryCallback callback = iterator.next();
                        callback.checkTimeout();
                        if (callback.isComplete) {
                            iterator.remove();
                            if (log.isDebugEnabled()) {
                                log.debug("Callback created on "
                                    + callback.creationTime.toDateTime().toString(RetryCallback.pattern)
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
        if (executor == null) {
            log.debug("Setup timeout check");
            executor = Executors.newSingleThreadScheduledExecutor();
            // runs every 5 minutes.
            executor.scheduleWithFixedDelay(new TimeoutChecker(), 0, 5, TimeUnit.MINUTES);
        }
    }
    
    /**
     * Returns true if the command for this paoType should be send to porter.
     * RF supports porter requests and it supports the COMMANDER_REQUESTS tag, RF should be excluded for Web Schedules.
     */
    private boolean supportsPorterRequests(PaoType type){
        return !type.isRfn() && paoDefinitionDao.isTagSupported(type, PaoTag.COMMANDER_REQUESTS);
    }
}
