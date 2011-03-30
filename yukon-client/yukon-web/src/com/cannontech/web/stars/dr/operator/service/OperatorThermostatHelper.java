package com.cannontech.web.stars.dr.operator.service;

import java.util.List;

import net.sf.jsonOLD.JSONObject;

import org.springframework.ui.ModelMap;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleDisplay;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;

public interface OperatorThermostatHelper {

	public List<Integer> setupModelMapForThermostats(String thermostatIds, AccountInfoFragment accountInfoFragment, ModelMap modelMap);
	
	/**
     * Helper method to get a JSON object representation of a thermostat
     * schedule
     * @param schedule - Schedule to get object for
     * @param isFahrenheit - True if temp should be fahrenheit
     * @return JSON object
     */
	public JSONObject getJSONForSchedule(AccountThermostatSchedule schedule, boolean isFahrenheit);
	
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
     * Helper method to generate a list of ThermostatScheduleDisplays, which
     * contain printable, localized Strings representing a thermostat schedule.
     * @param yukonUserContext
     * @param type - String representation of a SchedulableThermostatType
     * @param thermostatScheduleMode
     * @param accountThermostatSchedule
     * @param isFahrenheit - True if temp should be fahrenheit
     * @return a List of ThermostatScheduleConfirmationDisplays to display
     */
    public List<ThermostatScheduleDisplay> getScheduleDisplays(YukonUserContext yukonUserContext, String type, ThermostatScheduleMode thermostatScheduleMode, AccountThermostatSchedule accountThermostatSchedule, boolean isFahrenheit, String i18nKey);
    
    /**
     * Returns the schedule mode for the provided schedule.  If the mode is Weekday, Weekend, it will
     * be changed to Weekday, Saturday, Sunday unless the schedule is a UPro schedule and the
     * schedule52Enabled role value is true.
     */
    public ThermostatScheduleMode getAdjustedScheduleMode(AccountThermostatSchedule schedule, boolean schedule52Enabled);
}
