package com.cannontech.stars.dr.thermostat.service;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;

public interface ThermostatCommandExecutionService {
    
    /**
     * Will attempt to send a manual adjustment command to the thermostat
     */
    public void doManualAdjustment(ThermostatManualEvent event, 
                                                          Thermostat thermostat, 
                                                          LiteYukonUser user) throws CommandCompletionException;
    
    /**
     * Will attempt to send a schedule update command/s to the thermostat
     */
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, 
                                                           AccountThermostatSchedule ats, 
                                                           ThermostatScheduleMode mode, 
                                                           Thermostat stat, 
                                                           LiteYukonUser user);
    
    
    /**
     * Save the ActivityLog and LMCustomerEventBase entries to the database
     */
    public void saveAndLogUpdateEvent(CustomerAccount account, 
                                       AccountThermostatSchedule schedule,
                                       TimeOfWeek timeOfWeek, 
                                       Thermostat stat, 
                                       LiteYukonUser user);
}