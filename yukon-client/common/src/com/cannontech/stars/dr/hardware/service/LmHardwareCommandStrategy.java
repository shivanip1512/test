package com.cannontech.stars.dr.hardware.service;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;

public interface LmHardwareCommandStrategy {
    
    /**
     * Each strategy implementation should have a type in the {@link HardwareStrategyType} enum.
     * This returns that type for this strategy implementation.
     */
    public HardwareStrategyType getType();
    
    /**
     * This associates the strategy with a subset of {@link HardwareType}s. It is up to the author of the
     * strategies to ensure that only one strategy "canHandle" for each {@link HardwareType}. 
     * It is recommended that this be implemented with {@link HardwareType} sets checking until
     * Hardware types are fully merged into {@link PaoType} at with point PaoTags should be used. 
     * The caller of this method is allowed to cache the result, therefore implementations 
     * should return always return the same answer.
     * 
     * @return true if it is appropriate to use this strategy for the given {@link HardwareType}.
     */
    public boolean canHandle(HardwareType type);
    
    /**
     * Will attempt to send a manual adjustment command to the thermostat
     */
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user) 
    throws CommandCompletionException;
    
    /**
     * Will attempt to send a schedule update command/s to the thermostat
     */
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, 
                                                           AccountThermostatSchedule ats, 
                                                           ThermostatScheduleMode mode, 
                                                           Thermostat stat, 
                                                           LiteYukonUser user);

    /**
     * Attempts to send a command to a dr device using the provided parameters.
     * @throws {@link CommandCompletionException}
     */
    public void sendCommand(LmHardwareCommand parameters) throws CommandCompletionException;
    
    /**
     * Will attempt to send a text message to a thermostat
     */
    public void sendTextMessage(YukonTextMessage message);
    
    /**
     * Will attempt to cancel a text message
     */
    public void cancelTextMessage(YukonCancelTextMessage message);

}