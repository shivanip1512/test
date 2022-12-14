package com.cannontech.amr.disconnect.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectStrategyService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;

public class DisconnectPlcServiceImpl implements DisconnectStrategyService {

    private final Logger log = YukonLogManager.getLogger(DisconnectPlcServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private MeterDao meterDao;

    private Set<PaoType> integratedTypes;
    private Set<PaoType> collarTypes;
    
    @PostConstruct
    public void init() {
        Set<PaoDefinition> collar = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_COLLAR_COMPATIBLE);
        Set<PaoDefinition> integrated = Sets.difference(
            paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_410, PaoTag.DISCONNECT_213, PaoTag.DISCONNECT_310),
            collar);

        // devices with integrated disconnect
        integratedTypes = integrated.stream()
                .map(meter -> meter.getType()).collect(Collectors.toSet());
        // devices with collar
        collarTypes = collar.stream()
                .map(meter -> meter.getType()).collect(Collectors.toSet());
    }
      
    @Override
    public FilteredDevices filter(List<SimpleDevice> meters) {
        FilteredDevices filteredDevices = new FilteredDevices();

        // add meters with integrated disconnect are valid
        filteredDevices.addValid(meters.stream()
            .filter(meter -> integratedTypes.contains(meter.getDeviceType()))
            .collect(Collectors.toList()));

        Map<Integer, SimpleDevice> metersWithCollar = meters.stream()
                .filter(meter -> collarTypes.contains(meter.getDeviceType()))
                .collect(Collectors.toMap(meter -> meter.getDeviceId(), meter -> meter));
        
        List<Integer> deviceIdsWithCollarAddress =
            meterDao.getMetersWithDisconnectCollarAddress(metersWithCollar.keySet());

        //valid meters with collar and disconnect collar address
        filteredDevices.addValid(deviceIdsWithCollarAddress.stream()
            .map(id -> metersWithCollar.get(id))
            .collect(Collectors.toList()));
        
        //not configured meters with collar but no collar disconnect address
        filteredDevices.addNotConfigured(metersWithCollar.keySet().stream()
            .filter(id -> !deviceIdsWithCollarAddress.contains(id))
            .map(id -> metersWithCollar.get(id))
            .collect(Collectors.toList()));

        return filteredDevices;
    }
    
    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        result.getCancellationCallbacks(getStrategy()).forEach(callback ->{
            commandExecutionService.cancelExecution(callback.getCommandCompletionCallback(), user, false);
        });
    }
            
    @Override
    public void execute(DisconnectCommand command, Set<SimpleDevice> meters, DisconnectCallback callback,
                        CommandRequestExecution execution, LiteYukonUser user) {
        List<CommandRequestDevice> commands =
            meters.stream().map(meter -> new CommandRequestDevice(command.getPlcCommand(), meter)).collect(
                Collectors.toList());
        if (log.isDebugEnabled()) {
            commands.forEach(c -> log.debug("PLC send" + c));
        }			          
        Callback commandCallback = new Callback(callback, meters, command);
        if(callback.getResult() != null) {
            callback.getResult().addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, commandCallback));
        }
        commandExecutionService.execute(commands, commandCallback, execution, false, user);
    }

    private class Callback extends CommandCompletionCallback<CommandRequestDevice> {

        private final DisconnectCallback callback;
        DisconnectCommand command;
        Set<SimpleDevice> meters;
        Set<SimpleDevice> devicesWithoutPoint;

        Callback(DisconnectCallback callback, Set<SimpleDevice> meters, DisconnectCommand command) {
            this.callback = callback;
            this.meters = meters;
            this.command = command;
            BiMap<PaoIdentifier, LitePoint> deviceToPoint =
                attributeService.getPoints(meters, BuiltInAttribute.DISCONNECT_STATUS);

            devicesWithoutPoint = Sets.difference(meters, Sets.newHashSet(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet())));
        }
        
        /**
         * Process devices that do not have point. The command was sent to the
         * device, but we do not know what state device is in
         * (connected/disconnected/armed). If the "connect" command was send
         * mark device as "connected" etc.
         */
        private void processUnsupported(Set<SimpleDevice> devicesWithoutPoint) {
            for (SimpleDevice meter : devicesWithoutPoint) {
                if (log.isDebugEnabled()) {
                    log.debug("PLC processUnsupported command:" + command + " Meter:" + meter);
                }
                callback.success(command, meter, null);
                meters.remove(meter);
            }
        }

        @Override
        public void complete() {
            processUnsupported(devicesWithoutPoint);
            callback.complete(getStrategy());
        }

        @Override
        public void cancel() {
            complete();
        }

        @Override
        public void processingExceptionOccurred(String reason) {
            log.debug("PLC exception (CommandCompletionCallback)");
            log.debug("Reason:" + reason);
            callback.processingExceptionOccurred(reason);
        }

        @Override
        public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
            LitePoint litePoint =
                attributeService.getPointForAttribute(command.getDevice(), BuiltInAttribute.DISCONNECT_STATUS);
            int stateGroupId = litePoint.getStateGroupID();
            LiteState liteState = stateGroupDao.findLiteState(stateGroupId, (int) value.getValue());
            Disconnect410State disconnectState = Disconnect410State.getByRawState(liteState.getStateRawState());
            if (disconnectState == Disconnect410State.CONFIRMED_DISCONNECTED
                || disconnectState == Disconnect410State.UNCONFIRMED_DISCONNECTED) {
                callback.success(DisconnectCommand.DISCONNECT, command.getDevice(), value);
            } else if (disconnectState == Disconnect410State.CONNECTED) {
                callback.success(DisconnectCommand.CONNECT, command.getDevice(), value);
            } else if (disconnectState == Disconnect410State.CONNECT_ARMED) {
                callback.success(DisconnectCommand.ARM, command.getDevice(), value);
            }
            if (log.isDebugEnabled()) {
                log.debug("PLC receivedValue:" + command + " State:" + disconnectState);
            }
            meters.remove(command.getDevice());
        }
        
        @Override
        public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
            if (log.isDebugEnabled()) {
                log.debug("PLC receivedLastError:" + command + " Error:" + error);
            }
            callback.failed(command.getDevice(), error);
            meters.remove(command.getDevice());
        }
    }

    @Override
    public StrategyType getStrategy() {
        return StrategyType.PORTER;
    }
}