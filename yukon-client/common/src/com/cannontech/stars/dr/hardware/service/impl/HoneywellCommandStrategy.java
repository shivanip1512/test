package com.cannontech.stars.dr.hardware.service.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.honeywell.HoneywellCommunicationException;
import com.cannontech.dr.honeywell.service.HoneywellCommunicationService;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;

public class HoneywellCommandStrategy implements LmHardwareCommandStrategy {
    private static final Logger log = YukonLogManager.getLogger(HoneywellCommandStrategy.class);

    @Autowired private HoneywellCommunicationService honeywellCommunicationService;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigDao;
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.HONEYWELL;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isHoneywell();
    }

    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {
        LiteLmHardwareBase device = command.getDevice();
        ArrayList<Integer> deviceIds=new ArrayList<>();
        deviceIds.add(device.getDeviceID());
        try {
            int groupId;
            switch (command.getType()) {
            case IN_SERVICE:
                break;
            case OUT_OF_SERVICE:
                break;
            case TEMP_OUT_OF_SERVICE:
                // TODO:Update code for eventId and test Calls for honeywell
                // groupId = getGroupId(device.getInventoryID());
                // moveDevice(deviceIds, groupId);
                // honeywellCommunicationService.cancelDREventForDevices(deviceIds, eventId, true);
                break;
            case CANCEL_TEMP_OUT_OF_SERVICE:
                // TODO: Test Calls for honeywell
                // groupId = getGroupId(device.getInventoryID());
                // moveDevice(deviceIds, groupId);
                break;
            case CONFIG:
                break;
            case PERFORMANCE_VERIFICATION:
            case READ_NOW:
            default:
                break;
            }
        } catch (HoneywellCommunicationException e) {
            log.error("Error sending command to Honeywell server.", e);
            throw new CommandCompletionException("Error sending command to Honeywell server.", e);
        }
    }

    /*private int getGroupId(int inventoryId) {
        List<LMHardwareConfiguration> hardwareConfig = lmHardwareConfigDao.getForInventoryId(inventoryId);
        return hardwareConfig.get(0).getAddressingGroupId();
    }*/
    
    private void moveDevice(ArrayList<Integer> deviceIds, int groupId) {
        // Remove devices from DR Group
        // honeywellCommunicationService.removeDeviceFromDRGroup(deviceIds, groupId);
        // Add them to a different Group
        // honeywellCommunicationService.addDevicesToGroup(deviceIds, groupId);
    }

    @Override
    public boolean canBroadcast(LmCommand command) {
        return false;
    }

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user)
            throws CommandCompletionException {
        throw new UnsupportedOperationException("No support for manual adjustment");
    }

    @Override
    public void sendBroadcastCommand(LmCommand command) throws CommandCompletionException {
        throw new UnsupportedOperationException("No support for broadcast");
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, AccountThermostatSchedule ats,
            ThermostatScheduleMode mode, Thermostat stat, LiteYukonUser user) {
        throw new UnsupportedOperationException("No support for schedules");
    }

    @Override
    public void sendTextMessage(YukonTextMessage message) {
        throw new UnsupportedOperationException("No support for text message");
    }

    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        throw new UnsupportedOperationException("No support for text message");
    }

    @Override
    public void verifyCanSendConfig(LmHardwareCommand command) throws BadConfigurationException {
        throw new UnsupportedOperationException("No support for verifyCanSendConfig");
    }
}