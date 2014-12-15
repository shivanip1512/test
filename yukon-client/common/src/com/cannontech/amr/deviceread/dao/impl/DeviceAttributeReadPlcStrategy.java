package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
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
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
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
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class DeviceAttributeReadPlcStrategy implements DeviceAttributeReadStrategy {

    private final static Logger log = YukonLogManager.getLogger(DeviceAttributeReadPlcStrategy.class);
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    private ScheduledExecutorService executor = null;
    
    private final static int timeoutErrorCode = 227;

    private final Set<RetryCallback> retryCallbacksAwaitingCompletion =
        Collections.synchronizedSet(new HashSet<RetryCallback>());

    @Override
    public StrategyType getType() {
        return StrategyType.PLC;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        boolean result = false;
        if (paoType.isPlc()) {
            result = paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS);
        }
        return result;
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> paoPointIdentifiers, LiteYukonUser user) {
        return meterReadCommandGeneratorService.isReadable(paoPointIdentifiers);
    }

    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            GroupCommandCompletionCallback groupCallback, DeviceRequestType type, LiteYukonUser user) {
        List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(points);
        if (log.isDebugEnabled()) {
            log.debug(getType() + " Strategy initiateRead");
            log.debug("Points:" + points);
            log.debug("Commands:" + commands);
        }
        commandRequestDeviceExecutor.createTemplateAndExecute(groupCallback.getExecution(), groupCallback, commands,
            user, true);
    }

    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            final DeviceAttributeReadStrategyCallback delegateCallback, DeviceRequestType type,
            CommandRequestExecution execution, LiteYukonUser user) {
        CommandCompletionCallback<CommandRequestDevice> groupCallback =
            new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

                @Override
                public void complete() {
                    delegateCallback.complete();
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    MessageSourceResolvable summary =
                        YukonMessageSourceResolvable.createSingleCodeWithArguments(
                            "yukon.common.device.attributeRead.plc.errorSummary", error.getCategory(),
                            error.getDescription(), error.getErrorCode(), error.getPorter());
                    MessageSourceResolvable detail =
                        YukonMessageSourceResolvable.createDefaultWithoutCode(error.getTroubleshooting());
                    DeviceAttributeReadError readError =
                        new DeviceAttributeReadError(DeviceAttributeReadErrorType.COMMUNICATION, summary, detail);
                    delegateCallback.receivedError(command.getDevice().getPaoIdentifier(), readError);
                }

                @Override
                public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                    delegateCallback.receivedValue(command.getDevice().getPaoIdentifier(), value);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    delegateCallback.receivedLastValue(command.getDevice().getPaoIdentifier(), value);
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    MessageSourceResolvable summary =
                        YukonMessageSourceResolvable.createSingleCodeWithArguments(
                            "yukon.common.device.attributeRead.plc.exception", reason);
                    MessageSourceResolvable detail =
                        YukonMessageSourceResolvable.createSingleCode("yukon.common.device.attributeRead.plc.unknownError");
                    DeviceAttributeReadError exception =
                        new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, summary, detail);
                    delegateCallback.receivedException(exception);
                }
            };

        List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(points);
        if (log.isDebugEnabled()) {
            log.debug(getType() + " Strategy initiateRead");
            log.debug("Points:" + points);
            log.debug("Commands:" + commands);
        }
        commandRequestDeviceExecutor.createTemplateAndExecute(execution, groupCallback, commands, user, true);
    }

    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        List<CommandRequestDevice> commandRequests =
            meterReadCommandGeneratorService.getCommandRequests(devicesForThisStrategy);
        return commandRequests.size();
    }

    @Override
    public CommandRequestExecutionObjects<CommandRequestDevice> initiateRead(Set<SimpleDevice> devices,
            Set<? extends Attribute> attributes, final String command, DeviceRequestType type, LiteYukonUser user,
            RetryParameters retryParameters, CommandCompletionCallbackAdapter<CommandRequestDevice> callback) {

        Set<YukonPao> unsupportedDevices = new HashSet<>();
        List<CommandRequestDevice> commandRequests = new ArrayList<>();
        List<SimpleDevice> allDevices = new ArrayList<>();

        if (attributes != null && !attributes.isEmpty()) {
            List<PaoMultiPointIdentifier> supportedDevicesByAttribute = new ArrayList<>();
            PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers =
                attributeService.findPaoMultiPointIdentifiersForAttributesWithUnsupported(devices, attributes);
            unsupportedDevices = paoPointIdentifiers.getUnsupportedDevices();
            for (PaoMultiPointIdentifier identifier : paoPointIdentifiers.getSupportedDevicesAndPoints()) {
                if (identifier.getPao().getPaoType().isMct()) {
                    supportedDevicesByAttribute.add(identifier);
                } else {
                    // devices that support the attribute and that are not MCTs.
                    unsupportedDevices.add(identifier.getPao());
                }
            }
            commandRequests.addAll(meterReadCommandGeneratorService.getCommandRequests(supportedDevicesByAttribute));
            allDevices.addAll(Lists.transform(supportedDevicesByAttribute,
                new Function<PaoMultiPointIdentifier, SimpleDevice>() {
                    @Override
                    public SimpleDevice apply(PaoMultiPointIdentifier device) {
                        return new SimpleDevice(device.getPao());
                    }
                }));
        } else if (!StringUtils.isEmpty(command)) {
            List<SimpleDevice> supportedDevicesByCommand = new ArrayList<>();
            for (SimpleDevice device : devices) {
                if (device.getDeviceType().isMct()) {
                    supportedDevicesByCommand.add(device);
                } else {
                    // devices are not MCTs.
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
            allDevices.addAll(supportedDevicesByCommand);
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
       
        RetryCallback retryCallback = new RetryCallback(allDevices, execution, retryParameters.getStopRetryAfterDate(), callback);
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

        RetryCallback(List<SimpleDevice> allDevices, CommandRequestExecution execution,
                Date stopRetryAfterDate, CommandCompletionCallbackAdapter<CommandRequestDevice> callback) {
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
                    commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, device.getDeviceId(),
                        timeoutErrorCode);
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
}