package com.cannontech.stars.dr.hardware.service;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;

public interface LmHardwareCommandService {
    
    /**
     * Will attempt to send a manual adjustment command to the dr device
     * @throws {@link CommandCompletionException}
     */
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user) 
    throws CommandCompletionException;
    
    /**
     * Will attempt to send a schedule update command/s to the dr device
     * @throws {@link CommandCompletionException}
     */
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, 
                                                           AccountThermostatSchedule ats, 
                                                           ThermostatScheduleMode mode, 
                                                           Thermostat stat, 
                                                           LiteYukonUser user) throws CommandCompletionException;

    /**
     * Will attempt to send a configuration command to the dr device
     * @throws {@link CommandCompletionException}
     */
    public void sendConfigCommand(LmHardwareCommand command) throws CommandCompletionException;

    /**
     * Will attempt to send an in-service command to the dr device
     * @throws {@link CommandCompletionException}
     */
    public void sendInServiceCommand(LmHardwareCommand command) throws CommandCompletionException;
    
    /**
     * Will attempt to send an out-of-service command to the dr device
     * @throws {@link CommandCompletionException}
     */
    public void sendOutOfServiceCommand(LmHardwareCommand command) throws CommandCompletionException;

    /**
     * Will attempt to send an opt out (temporary out of service) command to the dr device
     * @throws {@link CommandCompletionException}
     */
    public void sendOptOutCommand(LmHardwareCommand command) throws CommandCompletionException;
    
    /**
     * Will attempt to send text message to Utility Pro thermostats
     */
    public void sendTextMessage(YukonTextMessage message);

    /**
     * Will attempt to cancel text message
     */
    public void cancelTextMessage(YukonCancelTextMessage message);

    /**
     * Will send a command to the entire LM network.
     */
    void sendBroadcastCommand(LmCommand command);
    
}