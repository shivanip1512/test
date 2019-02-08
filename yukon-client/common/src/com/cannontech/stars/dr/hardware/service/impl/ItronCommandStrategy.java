package com.cannontech.stars.dr.hardware.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
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
import com.cannontech.yukon.IDatabaseCache;

public class ItronCommandStrategy  implements LmHardwareCommandStrategy{
    
    @Autowired private ItronCommunicationService itronCommunicationService;
    @Autowired private IDatabaseCache cache;
    
    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {

        /**
         * I believe the device being enrolled needs to be added to the Itron group that corresponds to the
         * yukon load group. When we initiate a control event, I think we need to target by group.1:15 PM
         * the service point needs to be enrolled in the program, or the device will not control for events in
         * that program. But the event will target devices by program and group. We might only want to control
         * one group, not all groups in the program.
         */
        long yukonGroupId = (int) command.getParams().get(LmHardwareCommandParam.GROUP_ID);
        long yukonProgramId = (int) command.getParams().get(LmHardwareCommandParam.OPTIONAL_GROUP_ID);
        
        LiteYukonPAObject programPao = cache.getAllLMPrograms().stream()
                .filter(program -> program.getLiteID() == yukonProgramId)
                .findFirst().get();
        
        LiteYukonPAObject groupPao = cache.getAllLMGroups().stream()
                .filter(group -> group.getLiteID() == yukonGroupId)
                .findFirst().get();

        long itronGroupId = itronCommunicationService.getGroup(groupPao);
        long itronProgramId  = itronCommunicationService.getProgram(programPao);
        
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
}
