package com.cannontech.stars.dr.hardware.service.impl;

import java.util.ArrayList;
import java.util.List;

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
import com.cannontech.dr.honeywellWifi.model.HoneywellDREvent;
import com.cannontech.dr.honeywellWifi.model.HoneywellWifiThermostat;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.HoneywellWifiThermostatDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.google.common.collect.ImmutableSet;

public class HoneywellCommandStrategy implements LmHardwareCommandStrategy {
    private static final Logger log = YukonLogManager.getLogger(HoneywellCommandStrategy.class);

    @Autowired private HoneywellCommunicationService honeywellCommunicationService;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigDao;
    @Autowired private HoneywellWifiThermostatDao honeywellWifiThermostatDao;
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    
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
        try {
            int groupId = 0;
            int honeywellGroupId = 0;
            ArrayList<Integer> honeywellThermostatIds = new ArrayList<Integer>();
            switch (command.getType()) {
            case IN_SERVICE:
                break;
            case OUT_OF_SERVICE:
                    int grpId = (int) command.getParams().get(LmHardwareCommandParam.GROUP_ID);
                    honeywellGroupId = honeywellWifiThermostatDao.getHoneywellGroupId(grpId);
                    honeywellThermostatIds.add(getHoneywellDeviceIds(device.getDeviceID()).getThermostatId());
                    honeywellCommunicationService.removeDeviceFromDRGroup(honeywellThermostatIds, honeywellGroupId);
                    break;
            case TEMP_OUT_OF_SERVICE:
                    // Remove device from current group in honeywell, not in Yukon
                    groupId = getGroupId(device.getInventoryID());
                    honeywellGroupId = honeywellWifiThermostatDao.getHoneywellGroupId(groupId);
                    HoneywellWifiThermostat honeywellThermostat = getHoneywellDeviceIds(device.getDeviceID());
                    honeywellThermostatIds.add(honeywellThermostat.getThermostatId());
                    honeywellCommunicationService.removeDeviceFromDRGroup(honeywellThermostatIds, honeywellGroupId);
                    // Get current events for this device from Honeywell
                    List<HoneywellDREvent> drEventResponses =
                        honeywellCommunicationService.getDREventsForDevice(honeywellThermostat.getThermostatId(),
                            honeywellThermostat.getDeviceVendorUserId().toString());
                    for (HoneywellDREvent event : drEventResponses) {
                        honeywellCommunicationService.cancelDREventForDevices(honeywellThermostatIds, event.getEventId(),
                            false);
                    }
                break;
            case CANCEL_TEMP_OUT_OF_SERVICE:
                // Add device to group in honeywell
                groupId = getGroupId(device.getInventoryID());
                honeywellGroupId = honeywellWifiThermostatDao.getHoneywellGroupId(groupId);
                honeywellThermostatIds.add(getHoneywellDeviceIds(device.getDeviceID()).getThermostatId());
                honeywellCommunicationService.addDevicesToGroup(honeywellThermostatIds, honeywellGroupId);
                break;
            case CONFIG:
                // In case of enrollment the BULK parameter will not be sent, in case of CONFIG, it will be set to true
                boolean isBulkConfigCommand = false;
                boolean isDeviceConfigured = true;
                if (command.getParams().get(LmHardwareCommandParam.BULK) != null) {
                    isBulkConfigCommand = (boolean) command.getParams().get(LmHardwareCommandParam.BULK);
                }
                try {
                    groupId = getGroupId(device.getInventoryID());
                    honeywellGroupId = honeywellWifiThermostatDao.getHoneywellGroupId(groupId);
                } catch (BadConfigurationException bce) {
                    if (isBulkConfigCommand) {
                        isDeviceConfigured = false; // In case of reconfig command ignore the exception and proceed
                    } else {
                        throw bce; // In case of enrollment do not ignore the exception
                    }
                }
                honeywellThermostatIds.add(getHoneywellDeviceIds(device.getDeviceID()).getThermostatId());
                unEnrollDeviceFromPastEnrolledGroups(device.getLiteID(), honeywellThermostatIds);
                if (isDeviceConfigured) {
                    // Only when a device is enrolled currently in Yukon we make a honeywell API call to enroll, 
                    // or else not needed
                    honeywellCommunicationService.addDevicesToGroup(honeywellThermostatIds, honeywellGroupId);
                }
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
    
    /**
     * UnEnroll Device From Past Enrolled Groups by sending the unenroll API call to Honeywell for groups 
     * enrolled in the past
     * 
     * @param inventoryId contains the device id
     * @param honeywellThermostatIds list has a single honeywell thermostatId associated with the device id
     */
    private void unEnrollDeviceFromPastEnrolledGroups(int inventoryId, ArrayList<Integer> honeywellThermostatIds) {

        List<Integer> pastEnrolledGroupIds =
            ImmutableSet.copyOf(honeywellWifiThermostatDao.
                    getPastEnrolledHoneywellGroupsByInventoryId(inventoryId)).asList();

        for (Integer pastGroupId : pastEnrolledGroupIds) {
            honeywellCommunicationService.removeDeviceFromDRGroup(honeywellThermostatIds, pastGroupId);
        }
    }

    /** 
     * Gets Honeywell Device Information by using the yukon specific device Id
     * 
     * @param deviceId
     * @return
     */
    private HoneywellWifiThermostat getHoneywellDeviceIds(int deviceId) {
        HoneywellWifiThermostat thermostat = getHoneywellWifiThermostatByDeviceId(deviceId);
        int honeywellDeviceId =
            honeywellCommunicationService.getGatewayDetailsForMacId(thermostat.getMacAddress(),
                thermostat.getDeviceVendorUserId().toString());
        thermostat.setThermostatId(honeywellDeviceId);
        return thermostat;
    }

    private HoneywellWifiThermostat getHoneywellWifiThermostatByDeviceId(int deviceId) {
        HoneywellWifiThermostat thermostat = honeywellWifiThermostatDao.getHoneywellWifiThermostat(deviceId);
        return thermostat;
    }

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
        boolean isBulkConfigCommand = false;
        if (command.getParams().get(LmHardwareCommandParam.BULK) != null) {
            isBulkConfigCommand = (boolean) command.getParams().get(LmHardwareCommandParam.BULK);
            if (isBulkConfigCommand) {
                return;
            }
        }
        getGroupId(command.getDevice().getInventoryID());
    }
    
    private int getGroupId(int inventoryId) {
        List<LMHardwareConfiguration> hardwareConfig = lmHardwareConfigDao.getForInventoryId(inventoryId);
        if (hardwareConfig.size() != 1) {
            throw new BadConfigurationException("Honeywell only supports one and only one group per device. "
                + hardwareConfig.size() + " groups found.");
        }
        return hardwareConfig.get(0).getAddressingGroupId();
    }

}