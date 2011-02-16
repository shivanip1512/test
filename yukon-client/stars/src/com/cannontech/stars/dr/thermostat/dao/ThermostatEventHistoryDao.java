package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;

public interface ThermostatEventHistoryDao {
    
    /**
     * Logs a manual setting being sent.
     */
    public void logManualEvent(LiteYukonUser user, 
                                  int thermostatId, 
                                  int temperatureInF, 
                                  ThermostatMode thermostatMode, 
                                  ThermostatFanState fan, 
                                  boolean hold);
    
    /**
     * Logs a schedule being sent.
     */
    public void logScheduleEvent(LiteYukonUser user,
                                    int thermostatId,
                                    int scheduleId,
                                    ThermostatScheduleMode scheduleMode);
    
    /**
     * Logs a restore (a.k.a. run program) being sent.
     */
    public void logRestoreEvent(LiteYukonUser user, int thermostatId);
    
    /**
     * Logs an event pre-populated by the caller.
     */
    public void logEvent(ThermostatEvent event);
    
    /**
     * Retrieves all events associated with the specified thermostat IDs.
     */
    public List<ThermostatEvent> getEventsByThermostatIds(List<Integer> thermostatIds);
    
    /**
     * Retrieves the most recent N events associated with the specified thermostat IDs.
     */
    public List<ThermostatEvent> getLastNEventsByThermostatIds(List<Integer> thermostatIds, int numberOfEvents);

}
