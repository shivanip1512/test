package com.cannontech.amr.demandreset.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.PlcDemandResetService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigIntegerKeysEnum;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PlcDemandResetServiceImpl implements PlcDemandResetService {
    private final static Logger log = YukonLogManager.getLogger(PlcDemandResetServiceImpl.class);
    private final int minutesToWait;

    private final static String DEMAND_RESET_COMMAND = "putvalue ied reset";
    private final static String LAST_RESET_TIME_COMMAND = "getconfig ied time";

    @Autowired private GroupCommandExecutor groupCommandExecutor;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired @Qualifier("main") private ScheduledExecutor refreshTimer;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Autowired
    public  PlcDemandResetServiceImpl(ConfigurationSource configurationSource) {
        minutesToWait = configurationSource.getInteger(MasterConfigIntegerKeysEnum.PLC_ACTIONS_CANCEL_TIMEOUT, 10);
    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        return paoDefinitionDao.filterPaosForTag(devices, PaoTag.PLC_DEMAND_RESET);
    }

    @Override
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos){
        BiMap<PaoIdentifier, LitePoint> deviceToPoint =
            attributeService.getPoints(paos, BuiltInAttribute.IED_DEMAND_RESET_COUNT);
        return Sets.newHashSet(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));
    }
    
    @Override
    public CommandCompletionCallback<CommandRequestDevice> sendDemandReset(CommandRequestExecution initiatedExecution,
                                                                           final Set<? extends YukonPao> paos,
                                                                           DemandResetCallback callback,
                                                                           final LiteYukonUser user) {

        // send demand reset command
        Set<SimpleDevice> devices = new HashSet<SimpleDevice>(PaoUtils.asSimpleDeviceListFromPaos(paos));
        List<CommandRequestDevice> initiatedCommands = getCommandRequests(devices, DEMAND_RESET_COMMAND);
        InitiatedCallback initiatedCallback = new InitiatedCallback(callback, paos);
		commandRequestDeviceExecutor.createTemplateAndExecute(initiatedExecution, initiatedCallback, initiatedCommands,
				user, true);
        return initiatedCallback;
    }
    
    @Override
    public Set<CommandCompletionCallback<CommandRequestDevice>> sendDemandResetAndVerify(CommandRequestExecution initiatedExecution,
                                                                                         CommandRequestExecution verificationExecution,
                                                                                         final Set<? extends YukonPao> paos,
                                                                                         final DemandResetCallback callback,
                                                                                         final LiteYukonUser user) {

        // Use midnight in the local (server) time. This assumes the meters use the same
        // timezone as the server. (The reset time for a 470 is always at midnight of the previous
        // night (morning) of the reset.)
        final DateTime whenRequested = new DateTime().withTimeAtStartOfDay();

        // Callbacks returned from this method are needed for cancellation
        Set<CommandCompletionCallback<CommandRequestDevice>> callbacks =
            new HashSet<CommandCompletionCallback<CommandRequestDevice>>();

        Set<SimpleDevice> devices = new HashSet<SimpleDevice>(PaoUtils.asSimpleDeviceListFromPaos(paos));

        CommandCompletionCallback<CommandRequestDevice> initiatedCallback =
            sendDemandReset(initiatedExecution, paos, callback, user);
        callbacks.add(initiatedCallback);

        // Only devices that support the IED_DEMAND_RESET_COUNT attribute are able to be verified.
        BiMap<PaoIdentifier, LitePoint> deviceToPoint =
            attributeService.getPoints(devices, BuiltInAttribute.IED_DEMAND_RESET_COUNT);

        Set<SimpleDevice> deviceToPointKeySet = Sets.newHashSet(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));
        
        Set<SimpleDevice> devicesWithoutPoint = Sets.difference(devices, deviceToPointKeySet);
        callbacks.add(initiatedCallback);
        if (log.isDebugEnabled() && !devicesWithoutPoint.isEmpty()) {
            log.error("Can't Verify:" + devicesWithoutPoint + " \"IED Demand Reset Count\" point is missing.");
        }
        for (SimpleDevice device : devicesWithoutPoint) {
            callback.cannotVerify(device, getError(DeviceError.NO_POINT));
        }

        // send verification to devices that support IED_DEMAND_RESET_COUNT attribute
        if (!deviceToPoint.isEmpty()) {
            List<CommandRequestDevice> verificationCommands =
                getCommandRequests(deviceToPointKeySet, LAST_RESET_TIME_COMMAND);
            VerificationCallback verificationCallback =
                new VerificationCallback(callback, deviceToPoint, whenRequested);
			commandRequestDeviceExecutor.createTemplateAndExecute(verificationExecution, verificationCallback,
					verificationCommands, user, true);
            callbacks.add(verificationCallback);
        }
        return callbacks;
    }
      
    private class InitiatedCallback implements CommandCompletionCallback<CommandRequestDevice> {
        
        private final DemandResetCallback callback;
        Set<? extends YukonPao> paos;

        InitiatedCallback(DemandResetCallback callback, Set<? extends YukonPao> paos) {
            this.callback = callback;
            this.paos = paos;
        }

        @Override
        public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
        }

        @Override
        public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
        }

        @Override
        public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
        }

        @Override
        public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
        }

        @Override
        public void receivedLastResultString(CommandRequestDevice command, String value) {
        }

        @Override
        public void complete() {
            // Sending a demand reset command to PLC devices is a one way command, we will not get
            // any responses other then complete
            Map<SimpleDevice, SpecificDeviceErrorDescription> errors = new HashMap<>();
            callback.initiated(new Results(paos, errors));
        }

        @Override
        public void cancel() {
        }

        @Override
        public void processingExceptionOccured(String reason) {
            log.debug("PLC exception (InitiatedCallback)");
            log.debug("Reason:" + reason);
            callback.processingExceptionOccured(reason);
        } 
    }

    private class VerificationCallback implements CommandCompletionCallback<CommandRequestDevice> {

        private final DemandResetCallback callback;
        Set<SimpleDevice> meters = new HashSet<>();
        DateTime whenRequested;
        BiMap<PaoIdentifier, LitePoint> deviceToPoint;
        
        VerificationCallback(DemandResetCallback callback, BiMap<PaoIdentifier, LitePoint> deviceToPoint, DateTime whenRequested) {
            this.callback = callback;
            this.meters.addAll(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));
            this.whenRequested = whenRequested;
            this.deviceToPoint = deviceToPoint;
        }

        @Override
        public void complete() {
            log.debug("PLC Complete (VerificationCallback)");
            if(log.isDebugEnabled()){
                log.debug("PLC Canceled:"+callback.isCanceled());
            }
            if (!callback.isCanceled()) {
                log.debug("PLC Completed");
                callback.complete();
            }else{
                if(log.isDebugEnabled()){
                    log.debug("PLC Waiting for "+ minutesToWait + " minutes before completing");
                }
            }
        }

        @Override
        public void cancel() {
            log.debug("PLC Cancel (VerificationCallback)");
            if (log.isDebugEnabled()) {
                log.debug("Wait " + minutesToWait + " minutes before proccessing cancelations.");
            }
            Runnable cancelationRunner = new Runnable() {
                @Override
                public void run() {
                    if (log.isDebugEnabled()) {
                        log.debug("Proccessing cancelations for meters:" + meters);
                    }
                    for (SimpleDevice meter : meters) {
                        callback.canceled(meter);
                    }
                    log.debug("PLC Cancel Complete (VerificationCallback)");
                    callback.complete();
                }
            };
            refreshTimer.schedule(cancelationRunner, minutesToWait, TimeUnit.MINUTES);
        }

        @Override
        public void processingExceptionOccured(String reason) {
            log.debug("PLC exception (VerificationCallback)");
            if(log.isDebugEnabled()){
                log.debug("Reason:" + reason);
            }
            callback.processingExceptionOccured(reason);
        }

        @Override
        public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
            SimpleDevice device = command.getDevice();
            /*
             * after "getconfig ied time command" is send we receive 2 values one for
             * "IED Demand Reset Count" another for IED Blink Count" ignore "IED Blink Count"
             */
            if (value.getId() == deviceToPoint.get(device.getPaoIdentifier()).getLiteID()) {
                if (value.getPointDataTimeStamp() == null) {
                    log.error("Failed:" + device);
                    callback.cannotVerify(device, getError(DeviceError.NO_TIMESTAMP));
                } else {
                    Instant resetTime = new Instant(value.getPointDataTimeStamp());
                    if (log.isDebugEnabled()) {
                        log.debug("pointDataDate: " + resetTime.toDate());
                        log.debug("whenRequested: " + whenRequested.toDate());
                    }
                    if (resetTime.isBefore(whenRequested.toInstant())) {
                        log.error("Failed:" + device);
                        callback.cannotVerify(device, getError(DeviceError.TIMESTAMP_OUT_OF_RANGE));
                    } else {
                        log.debug("Verified:" + device);
                        callback.verified(device, resetTime);
                    }
                }
                meters.remove(command.getDevice());
            }
        }
        
        @Override
        public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
            if (log.isDebugEnabled()) {
                log.debug("PLC receivedLastError:" + command + " Error:" + error);
            }
            callback.cannotVerify(command.getDevice(), error);
            meters.remove(command.getDevice());
        }
        
        @Override
        public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {}

        @Override
        public void receivedIntermediateResultString(CommandRequestDevice command, String value) {}

        @Override
        public void receivedLastResultString(CommandRequestDevice command, String value) {}
    }

    @Override
    public void cancel(Set<CommandCompletionCallback<CommandRequestDevice>> toCancel, LiteYukonUser user) {
        for (CommandCompletionCallback<CommandRequestDevice> callback : toCancel) {
            commandRequestDeviceExecutor.cancelExecution(callback, user, false);
        }
    }
    
    private List<CommandRequestDevice> getCommandRequests(Set<SimpleDevice> devices, final String command) {
        Function<SimpleDevice, CommandRequestDevice> toCommandRequestDevice =
            new Function<SimpleDevice, CommandRequestDevice>() {
                @Override
                public CommandRequestDevice apply(SimpleDevice meter) {
                    CommandRequestDevice request = new CommandRequestDevice();
                    request.setDevice(meter);
                    request.setCommandCallback(new CommandCallbackBase(command));
                    return request;
                }
            };
            
        List<CommandRequestDevice> commands =
            Lists.newArrayList(Iterables.transform(devices, toCommandRequestDevice));
        if (log.isDebugEnabled()) {
            for (CommandRequestDevice c : commands) {
                log.debug("PLC send " + c);
            }
        }
        return commands;
    }
    
    private SpecificDeviceErrorDescription getError(DeviceError error) {
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(error);
        SpecificDeviceErrorDescription deviceErrorDescription =
            new SpecificDeviceErrorDescription(errorDescription, null);

        return deviceErrorDescription;
    }
}
