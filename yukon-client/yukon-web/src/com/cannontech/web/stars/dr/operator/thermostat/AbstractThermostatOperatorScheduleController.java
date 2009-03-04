package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.web.security.annotation.CheckRole;

/**
 * Base controller for Operator-side Thermostat schedule operations
 */
@CheckRole(YukonRole.CONSUMER_INFO)
@Controller
public class AbstractThermostatOperatorScheduleController {


    /**
     * Helper method to default the 2nd and 3rd time/temp values for a two
     * time/temp schedule
     * @param schedule - Schedule to default
     */
    protected void setToTwoTimeTemps(ThermostatSchedule schedule) {

        ThermostatSeason season = schedule.getSeason();
        if(season != null) {
	        Map<TimeOfWeek, List<ThermostatSeasonEntry>> seasonEntryMap = season.getSeasonEntryMap();
	        for (Map.Entry<TimeOfWeek, List<ThermostatSeasonEntry>> entry : seasonEntryMap.entrySet()) {
	
	            TimeOfWeek key = entry.getKey();
	            List<ThermostatSeasonEntry> entryList = entry.getValue();
	
	            // There should always be 4 season entries per season
	            if (entryList.size() != 4) {
	                throw new IllegalArgumentException("There should be 4 season entries in the " + 
	                		season.getThermostatMode() + ", " + key.toString() + " season entry list.");
	            }
	
	            // Default the time to 0 seconds past midnight and the temp to
	            // -1
	            ThermostatSeasonEntry firstEntry = entryList.get(1);
	            firstEntry.setStartTime(0);
	            firstEntry.setCoolTemperature(-1);
	            firstEntry.setHeatTemperature(-1);
	            ThermostatSeasonEntry lastEntry = entryList.get(2);
	            lastEntry.setStartTime(0);
	            lastEntry.setCoolTemperature(-1);
	            lastEntry.setHeatTemperature(-1);
	        }
        }
    }

    /**
     * Helper method to get a JSON object representation of a thermostat
     * schedule
     * @param schedule - Schedule to get object for
     * @param isFahrenheit - True if temp should be fahrenheit
     * @return JSON object
     */
    protected JSONObject getJSONForSchedule(ThermostatSchedule schedule, boolean isFahrenheit) {

        ThermostatSeason season = schedule.getSeason();

        JSONObject scheduleObject = new JSONObject();

        JSONObject seasonObject = this.getJSONForSeason(season, isFahrenheit);

        scheduleObject.put("season", seasonObject);


        return scheduleObject;
    }

    /**
     * Helper method to get a JSON object representation of a thermostat season
     * @param season - Season to get object for
     * @param isFahrenheit - True if temp should be fahrenheit
     * @return JSON object
     */
    protected JSONObject getJSONForSeason(ThermostatSeason season, boolean isFahrenheit) {

        JSONObject object = new JSONObject();
        Map<TimeOfWeek, List<ThermostatSeasonEntry>> seasonEntryMap = season.getSeasonEntryMap();
        for (TimeOfWeek timeOfWeek : seasonEntryMap.keySet()) {

            List<ThermostatSeasonEntry> entryList = seasonEntryMap.get(timeOfWeek);

            for (ThermostatSeasonEntry entry : entryList) {
                Integer time = entry.getStartTime();
                Integer coolTemperature = entry.getCoolTemperature();
                Integer heatTemperature = entry.getHeatTemperature();

                JSONObject timeTemp = new JSONObject();
                timeTemp.put("time", time);
                
                if(coolTemperature == null) {
                	coolTemperature = 72;
                }
                if(heatTemperature == null) {
                	heatTemperature = 72;
                }
                
                String tempUnit = (isFahrenheit) ? CtiUtilities.FAHRENHEIT_CHARACTER : 
                								   CtiUtilities.CELSIUS_CHARACTER;
                
                coolTemperature = (int) CtiUtilities.convertTemperature(
                		coolTemperature, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
                heatTemperature = (int) CtiUtilities.convertTemperature(
                		heatTemperature, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
                
                timeTemp.put("coolTemp", coolTemperature);
                timeTemp.put("heatTemp", heatTemperature);

                object.accumulate(timeOfWeek.toString(), timeTemp);
            }
        }

        return object;
    }

    /**
     * Helper method to get a thermostat schedule from a JSON string
     * @param jsonString - String which contains the schedule
     * @return Schedule
     */
    protected ThermostatSchedule getScheduleForJSON(String jsonString,
            boolean isFahrenheit) {

        ThermostatSchedule schedule = new ThermostatSchedule();
        JSONObject object = new JSONObject(jsonString);

        JSONObject seasonObject = object.getJSONObject("season");
        ThermostatSeason season = this.getSeasonForJSON(seasonObject, isFahrenheit);
        schedule.setSeason(season);

        return schedule;
    }

    /**
     * Helper method to get a thermostat season from a JSON string
     * @param seasonObject - String which contains the season
     * @return Season
     */
    protected ThermostatSeason getSeasonForJSON(JSONObject seasonObject,
            boolean isFahrenheit) {

        ThermostatSeason season = new ThermostatSeason();

        List<TimeOfWeek> timeOfWeekList = new ArrayList<TimeOfWeek>();
        timeOfWeekList.add(TimeOfWeek.WEEKDAY);
        timeOfWeekList.add(TimeOfWeek.SATURDAY);
        timeOfWeekList.add(TimeOfWeek.SUNDAY);
        timeOfWeekList.add(TimeOfWeek.WEEKEND);

        // Add the season entries (time/value pairs) for each of the
        // TimeOfWeeks
        for (TimeOfWeek timeOfWeek : timeOfWeekList) {

        	JSONArray timeOfWeekArray;
        	try {
				timeOfWeekArray = seasonObject.getJSONArray(timeOfWeek.toString());
        	} catch (JSONException e) {
        		// this time of week doesn't exist - continue to the next
        		continue;
        	}

            for (Object object : timeOfWeekArray.toArray()) {
                JSONObject jsonObject = (JSONObject) object;

                Integer time = jsonObject.getInt("time");
                Integer coolTemperature = jsonObject.getInt("coolTemp");
                Integer heatTemperature = jsonObject.getInt("heatTemp");

                // Convert celsius temp to fahrenheit if needed
                if (!isFahrenheit) {
                    coolTemperature = (int) CtiUtilities.convertTemperature(coolTemperature,
                                                                        CtiUtilities.CELSIUS_CHARACTER,
                                                                        CtiUtilities.FAHRENHEIT_CHARACTER);
                    heatTemperature = (int) CtiUtilities.convertTemperature(heatTemperature,
                    		CtiUtilities.CELSIUS_CHARACTER,
                    		CtiUtilities.FAHRENHEIT_CHARACTER);
                }

                ThermostatSeasonEntry entry = new ThermostatSeasonEntry();
                entry.setStartTime(time);
                entry.setCoolTemperature(coolTemperature);
                entry.setHeatTemperature(heatTemperature);
                entry.setTimeOfWeek(timeOfWeek);

                season.addSeasonEntry(entry);
            }
        }

        return season;

    }

}
