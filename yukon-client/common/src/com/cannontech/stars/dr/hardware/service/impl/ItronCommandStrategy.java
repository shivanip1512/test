package com.cannontech.stars.dr.hardware.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationService;
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

public class ItronCommandStrategy  implements LmHardwareCommandStrategy{
    
    @Autowired private ItronCommunicationService itronCommunicationService;

    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {
       
        switch (command.getType()) {
        case CONFIG:
        case IN_SERVICE:
            itronCommunicationService.enroll(command.getDevice().getAccountID());
            break;

        case OUT_OF_SERVICE:
        case TEMP_OUT_OF_SERVICE:
            itronCommunicationService.unenroll(command.getDevice().getAccountID());
            break;
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
