package com.cannontech.stars.dr.hardware.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.dr.loadgroup.model.LoadGroupState;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.service.DrMeterDisconnectStatusService;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.user.YukonUserContext;

public class MeterCommandStrategy implements LmHardwareCommandStrategy {

    private static final Logger log = YukonLogManager.getLogger(MeterCommandStrategy.class);
    
    @Autowired private AttributeDynamicDataSource attributeDynamicDataSource;
    @Autowired private DisconnectService disconnectService;
    @Autowired private DrMeterDisconnectStatusService drStatusService;
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LMHardwareControlGroupDao lMHardwareControlGroupDao;
    @Autowired private LoadGroupService loadGroupService;

    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.METER;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isMeter();
    }

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat,
                                   LiteYukonUser user)
            throws CommandCompletionException {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account,
                                                           AccountThermostatSchedule ats,
                                                           ThermostatScheduleMode mode,
                                                           Thermostat stat, LiteYukonUser user) {
        /** No Meter thermostats */
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {

        int deviceId = command.getDevice().getDeviceID();
        YukonPao device = paoDao.getYukonPao(deviceId);
        YukonMeter meter = meterDao.getForId(deviceId);
        // Checks if device is connected
        // A value of 1 currently means connected
        PointValueHolder pVHolder = attributeDynamicDataSource.getPointValue(device, BuiltInAttribute.DISCONNECT_STATUS);

        try {
            switch (command.getType()) {
            // End Opt Out
            case CANCEL_TEMP_OUT_OF_SERVICE:
                log.debug("CANCEL_TEMP_OUT_OF_SERVICE");
                break;
            // Start Opt Out
            case TEMP_OUT_OF_SERVICE:
                log.debug("TEMP_OUT_OF_SERVICE");
                if (pVHolder.getValue() != RfnDisconnectStatusState.CONNECTED.getRawState()) {
                    // Since the device is not connected, we will send a CONNECT command to the device.
                    
                    // Update the status log for this meter - sending re-connect due to opt-out
                    List<Integer> deviceIds = List.of(meter.getDeviceId());
                    Optional<Integer> eventId = drStatusService.findActiveEventForDevice(meter.getDeviceId());
                    eventId.ifPresent(id -> drStatusService.updateControlStatus(id, 
                                                                                DrMeterControlStatus.RESTORE_OPT_OUT_SENT, 
                                                                                Instant.now(), deviceIds));
                    
                    // Send the connect command
                    DisconnectMeterResult result = disconnectService.execute(DisconnectCommand.CONNECT,
                                                                             DeviceRequestType.METER_COMMAND_STRATEGY_CONNECT_DISCONNECT_COMMAND,
                                                                             meter,
                                                                             YukonUserContext.system.getYukonUser());
                    
                    if (result.isSuccess()) {
                        // Update the status log for this meter - successfully re-connected
                        eventId.ifPresent(id -> drStatusService.updateControlStatus(id, 
                                                                                    DrMeterControlStatus.RESTORE_OPT_OUT_CONFIRMED, 
                                                                                    Instant.now(), deviceIds));
                        
                    } else {
                       // Update the status log for this meter - re-connect failed
                        eventId.ifPresent(id -> drStatusService.updateControlStatus(id, 
                                                                                    DrMeterControlStatus.RESTORE_FAILED, 
                                                                                    Instant.now(), deviceIds));
                    }
                    log.debug(result);
                }
                break;
            // End Enrollment
            case OUT_OF_SERVICE:
                log.debug("OUT_OF_SERVICE");
                // Get InventroyId of device
                int inventoryId = inventoryBaseDao.getHardwareByDeviceId(deviceId).getInventoryID();
                // Get LMGroupId device belongs/ belonged to
                List<LMHardwareControlGroup> lMGroupList = lMHardwareControlGroupDao.getByInventoryId(inventoryId); // It is returning a list of all the entries oldest to newest
                // Grabbing the groupId of the most recent group (the last one in the list)             
                int lMGroupId = lMGroupList.get(lMGroupList.size()-1).getLmGroupId();
                // Determine the control state of the LMGroup
                LMDirectGroupBase lMDirectGroupBaseObject = (LMDirectGroupBase) loadGroupService.findDatedGroup(lMGroupId).getObject();
                int loadGroupStateValue = lMDirectGroupBaseObject.getGroupControlState();
                // Check if the group is currently being controlled
                if (LoadGroupState.valueOf(loadGroupStateValue) == LoadGroupState.ACTIVE) {
                    // Check if device is connected
                    if (pVHolder.getValue() != RfnDisconnectStatusState.CONNECTED.getRawState()) {
                        // send connect if device was enrolled and not connected
                        DisconnectMeterResult result = disconnectService.execute(
                                                                                 DisconnectCommand.CONNECT,
                                                                                 DeviceRequestType.METER_COMMAND_STRATEGY_CONNECT_DISCONNECT_COMMAND,
                                                                                 meter,
                                                                                 YukonUserContext.system.getYukonUser()
                                                                                 );
                        log.debug(result);
                    }
                }
                break;
            default:
                break;
            }
        } catch (UnsupportedOperationException e) {
            log.error("Error Sending Command to Meter.", e);
            throw new CommandCompletionException("Error Sending Command to Meter.", e);
        }
    }

    @Override
    public void sendTextMessage(YukonTextMessage message) {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public boolean canBroadcast(LmCommand command) {
        return false;
    }

    @Override
    public void sendBroadcastCommand(LmCommand command) throws CommandCompletionException {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void verifyCanSendConfig(LmHardwareCommand command) throws BadConfigurationException {
        //Do nothing
    }

}
