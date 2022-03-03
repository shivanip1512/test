package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
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
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
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
    @Autowired private HoneywellWifiThermostatDao honeywellWifiThermostatDao;

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
            boolean isDeviceConfigured = true;
            HoneywellWifiThermostat honeywellThermostat = getHoneywellDeviceIds(device.getDeviceID());
            if (command.getType() != LmHardwareCommandType.PERFORMANCE_VERIFICATION
                && command.getType() != LmHardwareCommandType.READ_NOW) {
                honeywellThermostat = getHoneywellDeviceIds(device.getDeviceID());
                try {
                    groupId = getGroupId(device.getInventoryID());
                    honeywellGroupId = honeywellWifiThermostatDao.getHoneywellGroupId(groupId);
                } catch (BadConfigurationException bce) {
                    log.debug("Honeywell device is not enrolled, hence cannot remove the device from a group");
                    isDeviceConfigured = false;
                }
            }
            switch (command.getType()) {
            case IN_SERVICE:
                // Cleanup the past enrollments
                unEnrollDeviceFromPastEnrolledGroups(device.getLiteID(), honeywellThermostat, groupId);
                addDevicesToHoneywellGroup(honeywellThermostat, honeywellGroupId, isDeviceConfigured);
                break;

            case CANCEL_TEMP_OUT_OF_SERVICE:
                addDevicesToHoneywellGroup(honeywellThermostat, honeywellGroupId, isDeviceConfigured);
                break;

            case OUT_OF_SERVICE: // OOS command
                // Cleanup the past enrollments
                unEnrollDeviceFromPastEnrolledGroups(device.getLiteID(), honeywellThermostat, groupId);
                // Get subscribed events and send cancel for events to honeywell
                unEnrollDeviceFromCurrentGroupAndCancelDREvents(honeywellThermostat, honeywellGroupId,
                    isDeviceConfigured);
                break;

            case TEMP_OUT_OF_SERVICE: // For Opt-Outs only
                // Unenroll from current group if configured, get subscribed events and send cancel for events
                // to honeywell
                unEnrollDeviceFromCurrentGroupAndCancelDREvents(honeywellThermostat, honeywellGroupId,
                    isDeviceConfigured);
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
    
    /** Adds the thermostat device to Honeywell group if its enrolled/configured with a LM program.
     * @param honeywellThermostat represents the Honeywell device details
     * @param honeywellGroupId gives the honeywell groupid 
     * @param isDeviceConfigured boolean value gives if honeywell device is configured/enrolled with a LM group.
     */
    private void addDevicesToHoneywellGroup(HoneywellWifiThermostat honeywellThermostat, int honeywellGroupId,
            boolean isDeviceConfigured) {
        if (isDeviceConfigured) {
            // Add device to honeywell group, if already configured/enrolled with a group in Yukon
            honeywellCommunicationService.addDevicesToGroup(
                Collections.singletonList(honeywellThermostat.getThermostatId()), honeywellGroupId);
        }
    }

    /**
     * Un-enroll Device From Current Group , finds the current DR events registered with Honeywell 
     * and sends cancel DR Events message for them to honeywell.
     * 
     * @param honeywellThermostat contains thermostat details with vendorUserId in addition to id
     * @param honeywellGroupId contains the currently enrolled honeywell GroupId, if configured
     * @param isDeviceConfigured set to true if device is enrolled/configured
     */
    private void unEnrollDeviceFromCurrentGroupAndCancelDREvents(HoneywellWifiThermostat honeywellThermostat,
            Integer honeywellGroupId, boolean isDeviceConfigured) {
        
        if (isDeviceConfigured) {
            // Remove device from current group in honeywell, not in Yukon
            honeywellCommunicationService.removeDeviceFromDRGroup(
                Collections.singletonList(honeywellThermostat.getThermostatId()),
                honeywellGroupId);
        }
        // Get current events for this device from Honeywell
        List<HoneywellDREvent> drEventResponses =
            honeywellCommunicationService.getDREventsForDevice(honeywellThermostat.getThermostatId(),
                honeywellThermostat.getDeviceVendorUserId().toString());
        for (HoneywellDREvent event : drEventResponses) {
            // For devices which do not have any events, eventId is returned as null, don't send cancellation
            if (event.getEventId() != null) {
                honeywellCommunicationService.cancelDREventForDevices(
                    Collections.singletonList(honeywellThermostat.getThermostatId()),
                    event.getEventId(), true);
            }
        }
    }
    /**
     * UnEnroll Device From Past Enrolled Groups by sending the unenroll API call to Honeywell for groups 
     * enrolled in the past
     * 
     * @param inventoryId contains the device id
     * @param honeywellThermostat contains device information
     * @param currentGroupId contains the currently enrolled group Id
     */
    private void unEnrollDeviceFromPastEnrolledGroups(int inventoryId, HoneywellWifiThermostat honeywellThermostat,
            int currentGroupId) {

        List<Integer> pastEnrolledGroupIds =
            honeywellWifiThermostatDao.getPastEnrolledHoneywellGroupsByInventoryId(inventoryId);

        for (Integer pastGroupId : pastEnrolledGroupIds) {
            if (currentGroupId != pastGroupId) {
                honeywellCommunicationService.removeDeviceFromDRGroup(
                    Collections.singletonList(honeywellThermostat.getThermostatId()), pastGroupId);
            }
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