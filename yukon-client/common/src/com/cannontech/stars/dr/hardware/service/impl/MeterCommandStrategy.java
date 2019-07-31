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
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.service.DrMeterDisconnectStatusService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
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

        YukonPao device = paoDao.getYukonPao(command.getDevice().getDeviceID());
        YukonMeter meter = meterDao.getForId(command.getDevice().getDeviceID());

        try {
            switch (command.getType()) {
            // End Opt Out
            case CANCEL_TEMP_OUT_OF_SERVICE:
                log.debug("CANCEL_TEMP_OUT_OF_SERVICE");
                break;
            // Start Opt Out
            case TEMP_OUT_OF_SERVICE:
                log.debug("TEMP_OUT_OF_SERVICE");
                // Is device connected
                PointValueHolder pVHolder = attributeDynamicDataSource.getPointValue(device, BuiltInAttribute.DISCONNECT_STATUS);
                // A value of 1 currently means connected
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
                                                                             DeviceRequestType.OPT_OUT_OPT_IN_CONNECT_DISCONNECT_COMMAND,
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
