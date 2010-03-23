package com.cannontech.web.stars.dr.operator.service;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.ui.ModelMap;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;

public interface OperatorThermostatHelper {

	public void validateInventoryAgainstAccount(List<Integer> inventoryIdList, int accountId) throws NotAuthorizedException;
	
	public List<Integer> setupModelMapForThermostats(String thermostatIds, AccountInfoFragment accountInfoFragment, ModelMap modelMap);
	
	/**
     * Helper method to get a JSON object representation of a thermostat
     * schedule
     * @param schedule - Schedule to get object for
     * @param isFahrenheit - True if temp should be fahrenheit
     * @return JSON object
     */
	public JSONObject getJSONForSchedule(ThermostatSchedule schedule, boolean isFahrenheit);
	
	/**
     * Helper method to get a JSON object representation of a thermostat season
     * @param season - Season to get object for
     * @param isFahrenheit - True if temp should be fahrenheit
     * @return JSON object
     */
	public JSONObject getJSONForSeason(ThermostatSeason season, boolean isFahrenheit);
	
	/**
     * Helper method to get a thermostat schedule from a JSON string
     * @param jsonString - String which contains the schedule
     * @return Schedule
     */
    public ThermostatSchedule getScheduleForJSON(String jsonString, boolean isFahrenheit);
    
    /**
     * Helper method to get a thermostat season from a JSON string
     * @param seasonObject - String which contains the season
     * @return Season
     */
    public ThermostatSeason getSeasonForJSON(JSONObject seasonObject, boolean isFahrenheit);
    
    /**
     * Helper method to default the 2nd and 3rd time/temp values for a two
     * time/temp schedule
     * @param schedule - Schedule to default
     */
    public void setToTwoTimeTemps(ThermostatSchedule schedule);
}
