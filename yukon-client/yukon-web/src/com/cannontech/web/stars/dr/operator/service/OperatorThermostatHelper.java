package com.cannontech.web.stars.dr.operator.service;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.ui.ModelMap;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;

public interface OperatorThermostatHelper {

	public List<Integer> setupModelMapForThermostats(String thermostatIds, 
	                                                 AccountInfoFragment accountInfoFragment, 
	                                                 ModelMap modelMap) throws IllegalArgumentException;
	
    public List<AccountThermostatScheduleEntry> getScheduleEntriesForJSON(String jsonString, int accountThermostatScheduleId, 
                                                                          SchedulableThermostatType thermostatType, ThermostatScheduleMode mode, 
                                                                          boolean isFahrenheit);
    
    /**
     * Helper method to default the 2nd and 3rd time/temp values for a two
     * time/temp schedule
     * @param schedule - Schedule to default
     */
    public void setToTwoTimeTemps(AccountThermostatSchedule schedule);
    
    public String generateDefaultNameForUnnamedSchdule(AccountThermostatSchedule ats, String thermostatLabel, YukonUserContext yukonUserContext);
    
    /**
     * Take an AccountThermostatSchedule Object and turn it into a sensible JSON object
     * @param schedule - schedule to convert
     * @return JSOnObject
     */
    public JSONObject AccountThermostatScheduleToJSON(AccountThermostatSchedule schedule);
    
    /**
     * Convert a JSON representation to a AccountThermostatSchedule object
     * @param obj - JSONObject to convert
     * @return AccountThermostatSchedule
     */
    public AccountThermostatSchedule JSONtoAccountThermostatSchedule(JSONObject obj);
}
