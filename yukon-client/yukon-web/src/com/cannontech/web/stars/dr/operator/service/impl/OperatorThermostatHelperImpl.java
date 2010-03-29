package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareService;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.google.common.collect.Lists;

public class OperatorThermostatHelperImpl implements OperatorThermostatHelper {

	private InventoryDao inventoryDao;
	private HardwareService hardwareService;
	
	@Override
	public List<Integer> setupModelMapForThermostats(String thermostatIds, AccountInfoFragment accountInfoFragment, ModelMap modelMap) {
		
		List<Integer> thermostatIdsList = ServletUtil.getIntegerListFromString(thermostatIds);
		hardwareService.validateInventoryAgainstAccount(thermostatIdsList, accountInfoFragment.getAccountId());
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		modelMap.addAttribute("thermostatIds", thermostatIds);
		addThermostatNamesToModelMap(thermostatIdsList, modelMap);
		
		return thermostatIdsList;
	}
	
	@Override
	public JSONObject getJSONForSchedule(ThermostatSchedule schedule, boolean isFahrenheit) {

        ThermostatSeason season = schedule.getSeason();

        JSONObject scheduleObject = new JSONObject();

        JSONObject seasonObject = this.getJSONForSeason(season, isFahrenheit);

        scheduleObject.put("season", seasonObject);


        return scheduleObject;
    }
	
	@Override
	public JSONObject getJSONForSeason(ThermostatSeason season, boolean isFahrenheit) {

        JSONObject object = new JSONObject();
        Map<TimeOfWeek, List<ThermostatSeasonEntry>> seasonEntryMap = season.getSeasonEntryMap();
        for (TimeOfWeek timeOfWeek : seasonEntryMap.keySet()) {

            List<ThermostatSeasonEntry> entryList = seasonEntryMap.get(timeOfWeek);

            for (ThermostatSeasonEntry entry : entryList) {
                Integer time = entry.getStartTime().getMillisOfDay() / 1000 / 60;
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
    
	@Override
    public ThermostatSchedule getScheduleForJSON(String jsonString, boolean isFahrenheit) {

        ThermostatSchedule schedule = new ThermostatSchedule();
        JSONObject object = new JSONObject(jsonString);

        JSONObject seasonObject = object.getJSONObject("season");
        ThermostatSeason season = this.getSeasonForJSON(seasonObject, isFahrenheit);
        schedule.setSeason(season);

        return schedule;
    }
    
	@Override
    public ThermostatSeason getSeasonForJSON(JSONObject seasonObject, boolean isFahrenheit) {

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
                entry.setStartTime(LocalTime.fromMillisOfDay(time * 60 * 1000));
                entry.setCoolTemperature(coolTemperature);
                entry.setHeatTemperature(heatTemperature);
                entry.setTimeOfWeek(timeOfWeek);

                season.addSeasonEntry(entry);
            }
        }

        return season;

    }
    
	@Override
    public void setToTwoTimeTemps(ThermostatSchedule schedule) {

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
	            ThermostatSeasonEntry firstEntry = entryList.get(0);
	            firstEntry.setStartTime(new LocalTime(0, 0));
	            firstEntry.setCoolTemperature(-1);
	            firstEntry.setHeatTemperature(-1);
	            ThermostatSeasonEntry lastEntry = entryList.get(1);
	            lastEntry.setStartTime(new LocalTime(0, 0));
	            lastEntry.setCoolTemperature(-1);
	            lastEntry.setHeatTemperature(-1);
	        }
        }
    }
	
	private void addThermostatNamesToModelMap(List<Integer> thermostatIdsList, ModelMap modelMap) {
		
		// thermostat names
		List<String> thermostatNames = Lists.newArrayListWithCapacity(thermostatIdsList.size());
		for (int thermostatId : thermostatIdsList) {
			Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
			thermostatNames.add(thermostat.getLabel());
		}
		modelMap.addAttribute("thermostatNames", thermostatNames);
	}
	
	@Autowired
	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
	
	@Autowired
	public void setHardwareService(HardwareService hardwareService) {
	    this.hardwareService = hardwareService;
	}
}
