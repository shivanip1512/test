package com.cannontech.amr.demandreset.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.DemandResetStrategyService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;

public class DemandResetPlcServiceImpl implements DemandResetStrategyService {
    private final static Logger log = YukonLogManager.getLogger(DemandResetPlcServiceImpl.class);

    private final static String DEMAND_RESET_COMMAND = "putvalue ied reset";
    private final static String LAST_RESET_TIME_COMMAND = "getconfig ied time";

    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private AttributeService attributeService;
    @Autowired @Qualifier("main") private ScheduledExecutor refreshTimer;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private CollectionActionService collectionActionService;

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        return paoDefinitionDao.filterPaosForTag(devices, PaoTag.PLC_DEMAND_RESET);
    }

    @Override
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos) {
        BiMap<PaoIdentifier, LitePoint> deviceToPoint =
            attributeService.getPoints(paos, BuiltInAttribute.IED_DEMAND_RESET_COUNT);
        return Sets.newHashSet(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));
    }

    @Override
    public void sendDemandReset(CommandRequestExecution initiatedExecution, final Set<? extends YukonPao> paos,
            DemandResetCallback callback, final LiteYukonUser user) {

        // send demand reset command
        Set<SimpleDevice> devices = new HashSet<>(PaoUtils.asSimpleDeviceListFromPaos(paos));
        List<CommandRequestDevice> initiatedCommands = getCommandRequests(devices, DEMAND_RESET_COMMAND);
        InitiatedCallback initiatedCallback = new InitiatedCallback(callback, paos);
        if(callback.getResult() != null) {
            callback.getResult().addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, initiatedCallback));
        }
        commandExecutionService.execute(initiatedCommands, initiatedCallback, initiatedExecution, false, user);
    }

    @Override
    public void sendDemandResetAndVerify(CommandRequestExecution initiatedExecution,
            CommandRequestExecution verificationExecution, Set<? extends YukonPao> paos, DemandResetCallback callback,
            LiteYukonUser user) {

        // Use midnight in the local (server) time. This assumes the meters use the same
        // timezone as the server. (The reset time for a 470 is always at midnight of the previous
        // night (morning) of the reset.)
        DateTime whenRequested = new DateTime().withTimeAtStartOfDay();

        Set<SimpleDevice> devices = new HashSet<>(PaoUtils.asSimpleDeviceListFromPaos(paos));

        sendDemandReset(initiatedExecution, paos, callback, user);

        // Only devices that support the IED_DEMAND_RESET_COUNT attribute are able to be verified.
        BiMap<PaoIdentifier, LitePoint> deviceToPoint =
            attributeService.getPoints(devices, BuiltInAttribute.IED_DEMAND_RESET_COUNT);

        Set<SimpleDevice> deviceToPointKeySet = Sets.newHashSet(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));

        Set<SimpleDevice> devicesWithoutPoint = Sets.difference(devices, deviceToPointKeySet);

        if (!devicesWithoutPoint.isEmpty()) {
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
            if(callback.getResult() != null) {
                callback.getResult().addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, verificationCallback));
            }
            commandExecutionService.execute(verificationCommands, verificationCallback, verificationExecution, false,
                user);
        }
    }

    private class InitiatedCallback extends CommandCompletionCallback<CommandRequestDevice> {

        private final DemandResetCallback callback;
        Set<? extends YukonPao> paos;

        InitiatedCallback(DemandResetCallback callback, Set<? extends YukonPao> paos) {
            this.callback = callback;
            this.paos = paos;
        }

        @Override
        public void complete() {
            // Sending a demand reset command to PLC devices is a one way command, we will not get
            // any responses other then complete
            callback.initiated(new Results(paos, new HashMap<>()), getStrategy());
        }

        @Override
        public void processingExceptionOccurred(String reason) {
            log.debug("PLC exception (InitiatedCallback)" + " Reason:" + reason);
            callback.processingExceptionOccurred(reason);
        }
    }

    private class VerificationCallback extends CommandCompletionCallback<CommandRequestDevice> {

        private DemandResetCallback callback;
        Set<SimpleDevice> meters = new HashSet<>();
        DateTime whenRequested;
        BiMap<PaoIdentifier, LitePoint> deviceToPoint;

        VerificationCallback(DemandResetCallback callback, BiMap<PaoIdentifier, LitePoint> deviceToPoint,
                DateTime whenRequested) {
            this.callback = callback;
            this.meters.addAll(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));
            this.whenRequested = whenRequested;
            this.deviceToPoint = deviceToPoint;
        }

        @Override
        public void complete() {
            callback.complete(getStrategy());
        }


        @Override
        public void cancel() {
            if (callback.getResult() != null && callback.getResult().getVerificationExecution() != null) {
                collectionActionService.addUnsupportedToResult(CollectionActionDetail.CANCELED, callback.getResult(),
                    callback.getResult().getVerificationExecution().getId(), new ArrayList<>(meters), null);
            }
            complete();
        }

        @Override
        public void processingExceptionOccurred(String reason) {
            log.debug("PLC exception (VerificationCallback)");
            log.debug("Reason:" + reason);
            callback.processingExceptionOccurred(reason);
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
                    log.debug("pointDataDate: " + resetTime.toDate());
                    log.debug("whenRequested: " + whenRequested.toDate());
                    if (resetTime.isBefore(whenRequested.toInstant())) {
                        log.error("Failed:" + device);
                        callback.cannotVerify(device, getError(DeviceError.TIMESTAMP_OUT_OF_RANGE));
                    } else {
                        log.debug("Verified:" + device);
                        callback.verified(device, value);
                    }
                }
                meters.remove(command.getDevice());
            }
        }

        @Override
        public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
            log.debug("PLC receivedLastError:" + command + " Error:" + error);
            callback.cannotVerify(command.getDevice(), error);
            meters.remove(command.getDevice());
        }
    }

    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        result.getCancellationCallbacks(getStrategy()).forEach(callback -> {
            commandExecutionService.cancelExecution(callback.getCommandCompletionCallback(), user, false);
        });
    }

    private List<CommandRequestDevice> getCommandRequests(Set<SimpleDevice> devices, String command) {
        return devices.stream().map(d -> new CommandRequestDevice(command, d)).collect(Collectors.toList());
    }

    private SpecificDeviceErrorDescription getError(DeviceError error) {

        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(error);
        SpecificDeviceErrorDescription deviceErrorDescription =
            new SpecificDeviceErrorDescription(errorDescription, error.getDescriptionResolvable());

        return deviceErrorDescription;
    }

    @Override
    public StrategyType getStrategy() {
        return StrategyType.PORTER;
    }
}
