package com.cannontech.stars.dr.hardware.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.yukon.IDatabaseCache;

public class ItronCommandStrategy  implements LmHardwareCommandStrategy{
    
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigDao;    
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    @Autowired private ProgramService programService;
    @Autowired private PaoDao paoDao;
    @Autowired private ItronCommunicationService itronCommunicationService;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private ProgramDao programDao;
    
    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {

        /**
         * I believe the device being enrolled needs to be added to the Itron group that corresponds to the
         * yukon load group. When we initiate a control event, I think we need to target by group.1:15 PM
         * the service point needs to be enrolled in the program, or the device will not control for events in
         * that program. But the event will target devices by program and group. We might only want to control
         * one group, not all groups in the program.
         */
       // long itronGroupId = itronCommunicationService.getGroup(groupId);
       // long itronProgramId = itronCommunicationService.getProgram(programId);
   
       // System.out.println(getGroupId(command.getDevice().getInventoryID()));
        switch (command.getType()) {
            case IN_SERVICE:
            case CONFIG:
        default:
            break;    
        }
    }
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.ITRON;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isItron();
    }

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user)
            throws CommandCompletionException {
        throw new UnsupportedOperationException();
        
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, AccountThermostatSchedule ats,
            ThermostatScheduleMode mode, Thermostat stat, LiteYukonUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendTextMessage(YukonTextMessage message) {
        throw new UnsupportedOperationException();
        
    }

    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canBroadcast(LmCommand command) {
        return false;
    }

    @Override
    public void sendBroadcastCommand(LmCommand command) throws CommandCompletionException {
        throw new UnsupportedOperationException();
        
    }

    @Override
    public void verifyCanSendConfig(LmHardwareCommand command) throws BadConfigurationException {
        
    }
    
    private int getGroupId(int inventoryId) {
        List<LMHardwareConfiguration> hardwareConfig = lmHardwareConfigDao.getForInventoryId(inventoryId);
        if(hardwareConfig.size() != 1) {
            throw new BadConfigurationException("Ecobee only supports one and only one group per device. "
                + hardwareConfig.size() + " groups found.");
        }
        return hardwareConfig.get(0).getAddressingGroupId();
    }
}
