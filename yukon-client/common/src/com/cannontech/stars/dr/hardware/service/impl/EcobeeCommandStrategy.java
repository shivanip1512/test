package com.cannontech.stars.dr.hardware.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.EcobeeException;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
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

public class EcobeeCommandStrategy implements LmHardwareCommandStrategy {
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommandStrategy.class);

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;
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
        int ecId = device.getEnergyCompanyId();
        String serialNumber = device.getManufacturerSerialNumber();

        try {
            int groupId;
            switch(command.getType()) {
            case IN_SERVICE:
                groupId = getGroupId(device.getInventoryID());
                ecobeeCommunicationService.moveDeviceToSet(serialNumber, Integer.toString(groupId), ecId);
                break;
            case OUT_OF_SERVICE:
                // move device to EcobeeCommunicationService.UNENROLLED_SET
                // if there are no longer any enrollments for the group remove it via deleteManagementSet
                ecobeeCommunicationService.moveDeviceToSet(serialNumber, EcobeeCommunicationService.UNENROLLED_SET, ecId);
                // TODO get groupId of group previously enrolled in
                //  if (!hasActiveEnrollments(groupId)) {
                //      ecobeeCommunicationService.deleteManagementSet(Integer.toString(groupId), ecId);
                //  }
                break;
            case TEMP_OUT_OF_SERVICE:
                // move device to EcobeeCommunicationService.OPT_OUT_SET
                ecobeeCommunicationService.moveDeviceToSet(serialNumber, EcobeeCommunicationService.OPT_OUT_SET, ecId);
                break;
            case CANCEL_TEMP_OUT_OF_SERVICE:
                groupId = getGroupId(device.getInventoryID());
                // move device back to ecobee management set with the enerolled LM groups paoId
                ecobeeCommunicationService.moveDeviceToSet(serialNumber, Integer.toString(groupId), ecId);
                break;
            case CONFIG:
            case PERFORMANCE_VERIFICATION:
            case READ_NOW:
                // TODO
                //ecobeeCommunicationService.readDeviceData(serialNumbers, dateRange, energyCompanyId);
            default:
                break;
            }
        } catch (EcobeeException e) {
            log.error("Unable to send command.", e);
            throw new CommandCompletionException("Unable to send command.", e);
        }
    }
    
    private int getGroupId(int inventoryId) {
        List<LMHardwareConfiguration> hardwareConfig = lmHardwareConfigDao.getForInventoryId(inventoryId);
        if(hardwareConfig.size() != 1) {
            throw new IllegalStateException(hardwareConfig.size() 
                                            + " groups. Ecobee only supports one and only one group per device.");
        }
        return hardwareConfig.get(0).getAddressingGroupId();
    }

    private boolean hasActiveEnrollments(int groupId) {
        List<LMHardwareControlGroup> enrollments = lmHardwareControlGroupDao.getByLMGroupId(groupId);
        for (LMHardwareControlGroup enrollment : enrollments) {
            if (enrollment.getType() == LMHardwareControlGroup.ENROLLMENT_ENTRY && enrollment.isActiveEnrollment()) {
                return true;
            }
        }
        return false;
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
}