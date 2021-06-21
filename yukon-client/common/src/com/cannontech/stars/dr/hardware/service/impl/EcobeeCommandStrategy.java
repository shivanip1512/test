package com.cannontech.stars.dr.hardware.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
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
import com.google.common.collect.Sets;

public class EcobeeCommandStrategy implements LmHardwareCommandStrategy {
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommandStrategy.class);

    @Autowired private EcobeeZeusCommunicationService ecobeeZeusCommunicationService;
    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigDao;

    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.ECOBEE;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isEcobee();
    }

    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {
        LiteLmHardwareBase device = command.getDevice();
        String serialNumber = device.getManufacturerSerialNumber();

        try {
            List<Integer> groupIds;
            Integer groupId = null ;
            Integer programId = null;
            int inventoryId = device.getInventoryID();

            switch (command.getType()) {
                case IN_SERVICE:
                    groupIds = getGroupId(inventoryId);
                    for (int tempGroupId : groupIds) {
                        programId = ecobeeZeusGroupService.getProgramIdToEnroll(inventoryId, tempGroupId);
                        if (ecobeeZeusGroupService.shouldEnrollToGroup(inventoryId, programId)) {
                            groupId = tempGroupId;
                            break;
                        }
                    }
                    ecobeeZeusCommunicationService.enroll(groupId, serialNumber, device.getInventoryID(), programId, true);
                    break;
                case OUT_OF_SERVICE:

                    Set<Integer> groupIdsFromCommandParam = (Set<Integer>) command.getParams().get(LmHardwareCommandParam.GROUP_ID);
                    //Remove Device to Zeus group mapping. So pass updateDeviceMapping as true.
                    ecobeeZeusCommunicationService.unEnroll(groupIdsFromCommandParam, serialNumber, device.getInventoryID(), true);

                    break;
                case TEMP_OUT_OF_SERVICE:

                    groupIds = getGroupId(command.getDevice().getInventoryID());
                    // Remove the thermostat from the groups and then cancel the demand response for the thermostat
                    // Do not remove Device to Zeus group mapping. So pass updateDeviceMapping as false.
                    ecobeeZeusCommunicationService.unEnroll(Sets.newHashSet(groupIds), serialNumber, device.getInventoryID(), false);
                    ecobeeZeusCommunicationService.cancelDemandResponse(groupIds, serialNumber);

                    break;
                case CANCEL_TEMP_OUT_OF_SERVICE:

                    // Add the thermostat to the group when user cancel the opt out.
                    // Do not remove Device to Zeus group mapping. So pass updateDeviceMapping as false.
                    groupIds = getGroupId(inventoryId);
                    groupIds.stream().forEach(tempGroupId -> {
                    int tempProgramId = ecobeeZeusGroupService.getProgramIdToEnroll(inventoryId, tempGroupId);
                    ecobeeZeusCommunicationService.enroll(tempGroupId, serialNumber, device.getInventoryID(), tempProgramId,
                            false);
                    });

                    break;
                case CONFIG:
                    // When user change the Yukon group, 1st unenroll the thermostat then enroll it to the correct
                    // group.
                    Set<Integer> removedEnrollmentGroupIds = (Set<Integer>) command.getParams().get(LmHardwareCommandParam.GROUP_ID);
                    if (CollectionUtils.isNotEmpty(removedEnrollmentGroupIds)) {
                        ecobeeZeusCommunicationService.unEnroll(removedEnrollmentGroupIds, serialNumber, device.getInventoryID(), true);
                    }
                groupIds = getGroupId(inventoryId);
                for (int tempGroupId : groupIds) {
                    programId = ecobeeZeusGroupService.getProgramIdToEnroll(inventoryId, tempGroupId);
                    if (ecobeeZeusGroupService.shouldEnrollToGroup(inventoryId, programId)) {
                        groupId = tempGroupId;
                        break;
                    }
                }

                ecobeeZeusCommunicationService.enroll(groupId, serialNumber, device.getInventoryID(), programId, true);

                    break;
                case PERFORMANCE_VERIFICATION:
                case READ_NOW:
                default:
                    break;
            }
        } catch ( EcobeeCommunicationException e) {
            log.error("Error sending command to ecobee server.", e);
            throw new CommandCompletionException("Error sending command to ecobee server.", e);
        }
    }

    private List<Integer> getGroupId(int inventoryId) {
        List<LMHardwareConfiguration> hardwareConfig = lmHardwareConfigDao.getForInventoryId(inventoryId);
        return hardwareConfig.stream().map(config -> config.getAddressingGroupId()).collect(Collectors.toList());
    }

    @Override
    public boolean canBroadcast(LmCommand command) {
        return false;
    }

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat,
                                   LiteYukonUser user) throws CommandCompletionException {
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
        getGroupId(command.getDevice().getInventoryID());
    }
}