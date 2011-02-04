package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;

public interface ThermostatEventHistoryDao {
    
    public void logManualEvent(LiteYukonUser user, 
                                  int thermostatId, 
                                  int temperatureInF, 
                                  ThermostatMode thermostatMode, 
                                  ThermostatFanState fan, 
                                  boolean hold);
    
    public void logScheduleEvent(LiteYukonUser user,
                                    int thermostatId,
                                    int scheduleId,
                                    ThermostatScheduleMode scheduleMode);
    
    public void logRestoreEvent(LiteYukonUser user, int thermostatId);
    
    public void logEvent(ThermostatEvent event);
    
    public List<ThermostatEvent> getEventsByThermostatIds(List<Integer> thermostatIds);
    
    public List<ThermostatEvent> getLastNEventsByThermostatIds(List<Integer> thermostatIds, int numberOfEvents);

}
