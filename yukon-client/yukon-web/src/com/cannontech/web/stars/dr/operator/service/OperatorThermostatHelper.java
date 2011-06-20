package com.cannontech.web.stars.dr.operator.service;

import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.ui.ModelMap;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
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
     * Returns a Set of all the ThermostatScheduleModes that the user is allowed to use.
     */
    public Set<ThermostatScheduleMode> getAllowedModes(LiteYukonUser user);
    
    /**
     * Returns true if the specified user is permitted to use the specified schedule mode.
     */
    public boolean isModeAllowed(ThermostatScheduleMode mode, LiteYukonUser user);
    
    /**
     * Returns the mode of the specified schedule unless the user is not permitted to use that mode,
     * in which case it will return the first schedule mode supported by the schedule type that the
     * user is permitted to use.
     */
    public ThermostatScheduleMode getAdjustedScheduleMode(AccountThermostatSchedule schedule, LiteYukonUser user);

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

    /**
     * Consider the User and type of thermostat given and return all of the modes allowed for this combination.
     * Even though a thermostat may use a certain mode, the user may not have rights to use it in Yukon.
     * @param user - Usually the current user
     * @param type - the type of the current thermostat
     * @return List of ThermostatScheduleModes
     */
    public List<ThermostatScheduleMode> getAllowedModesForUserAndType(LiteYukonUser user, SchedulableThermostatType type);

    /**
     * Convert a Thermostat object into a JSON object
     * @param thermostat - the current thermostat
     * @param user - usually the current user
     * @return JSONObject
     */
    public JSONObject ThermostatToJSON(Thermostat thermostat, YukonUserContext user);

    /**
     * Get all of the SchedulableThermostatTypes compatible with a given Thermostat
     * @param thermostat - the current thermostat
     * @return List of SchedulableThermostatTypes
     */
    public List<SchedulableThermostatType> getCompatibleSchedulableThermostatTypes(Thermostat thermostat);
}
