package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriod;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class OperatorThermostatHelperImpl implements OperatorThermostatHelper {

	private InventoryDao inventoryDao;
	private HardwareUiService hardwareUiService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private AccountThermostatScheduleDao accountThermostatScheduleDao;
	
	@Override
	public List<Integer> setupModelMapForThermostats(String thermostatIds, AccountInfoFragment accountInfoFragment, ModelMap modelMap) {
		
	    thermostatIds = thermostatIds.replace("[", "");
	    thermostatIds = thermostatIds.replace("]", "");
		List<Integer> thermostatIdsList = ServletUtil.getIntegerListFromString(thermostatIds);
		hardwareUiService.validateInventoryAgainstAccount(thermostatIdsList, accountInfoFragment.getAccountId());
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		modelMap.addAttribute("thermostatIds", thermostatIds);
		
		// thermostat names
		List<String> thermostatNames = Lists.newArrayListWithCapacity(thermostatIdsList.size());
		for (int thermostatId : thermostatIdsList) {
			Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
			thermostatNames.add(thermostat.getLabel());
		}
		modelMap.addAttribute("thermostatNames", thermostatNames);
		
		return thermostatIdsList;
	}
	
	@Override
    public JSONObject AccountThermostatScheduleToJSON(AccountThermostatSchedule schedule) {

        JSONObject scheduleJSON = new JSONObject();
        
        scheduleJSON.put("accountId", schedule.getAccountId());
        scheduleJSON.put("scheduleName", schedule.getScheduleName());
        scheduleJSON.put("scheduleId", schedule.getAccountThermostatScheduleId());
        
        //number of time divisions available in a given day
        scheduleJSON.put("supportedPeriods", schedule.getThermostatType().getPeriodStyle().getRealPeriods().size());
        
        //type details
        JSONObject scheduleType = new JSONObject();
        scheduleType.put("name", schedule.getThermostatScheduleMode());
        scheduleType.put("periodStyle", schedule.getThermostatType().getPeriodStyle());
        
        scheduleJSON.put("type", scheduleType);
        scheduleJSON.put("schedulalbleThermostatType", schedule.getThermostatType());
        scheduleJSON.put("thermostatScheduleMode", schedule.getThermostatScheduleMode());
        
        List<AccountThermostatScheduleEntry> entries = schedule.getScheduleEntries();
        schedule.getEntriesByTimeOfWeekMultimap();
        for(int i=0; i<entries.size(); i++){
            AccountThermostatScheduleEntry entry = entries.get(i);
            JSONObject period = new JSONObject();
            
            //we keep 'pseudo' periods in the schedule and need to preserve them.  The interface needs
            //to know how to handle this.  The following chain will only return a period if it is 'real'
            if (entry.getCoolTemp().toFahrenheit().getIntValue() == -1 && entry.getHeatTemp().toFahrenheit().getIntValue() == -1) {
                // temp of -1 means ignore this time/temp pair - used when only
                // sending two time/temp values
                period.put("pseudo", true);
            }else{
                period.put("pseudo", false);
            }
            
            period.put("timeOfWeek", entry.getTimeOfWeek().toString());
            period.put("secondsFromMidnight", entry.getStartTime());
            period.put("cool_F", entry.getCoolTemp().toFahrenheit().getValue());
            period.put("heat_F", entry.getHeatTemp().toFahrenheit().getValue());
            scheduleJSON.accumulate("periods", period);
        }

        return scheduleJSON;
    }
	
	@Override
    public AccountThermostatSchedule JSONtoAccountThermostatSchedule(JSONObject obj) {
	    AccountThermostatSchedule ats = new AccountThermostatSchedule();
	    
	    //set params
	    ats.setAccountThermostatScheduleId(obj.getInt("scheduleId"));
	    ats.setScheduleName(obj.getString("scheduleName"));
	    ats.setThermostatType(SchedulableThermostatType.valueOf(obj.getString("schedulableThermostatType")));
	    ats.setThermostatScheduleMode(ThermostatScheduleMode.valueOf(obj.getString("thermostatScheduleMode")));
	    
	    //fixup COMMERCIAL_EXPRESSSTAT setToTwoTimeTemps
        if (ats.getThermostatType().getPeriodStyle() == ThermostatSchedulePeriodStyle.TWO_TIMES) {
            setToTwoTimeTemps(ats);
        }
	    
	    //buildup the entries list.  The JSON object groups the entries into sensible days
	    List<AccountThermostatScheduleEntry> entries = new ArrayList<AccountThermostatScheduleEntry>();
	    JSONArray periods = obj.getJSONArray("periods");
	    for(int i=0; i<periods.size(); i++) {
	      JSONObject period = periods.getJSONObject(i);
	      //synthesize the entry by hand
          AccountThermostatScheduleEntry entry = new AccountThermostatScheduleEntry();
          entry.setAccountThermostatScheduleId(ats.getAccountThermostatScheduleId());
          entry.setTimeOfWeek(TimeOfWeek.valueOf(period.getString("timeOfWeek")));
          entry.setCoolTemp(new FahrenheitTemperature(period.getDouble("cool_F")));
          entry.setHeatTemp(new FahrenheitTemperature(period.getDouble("heat_F")));
          entry.setStartTime(period.getInt("secondsFromMidnight"));
          entries.add(entry);
	    }
	    
	    //add the entries to the schedule
	    ats.setScheduleEntries(entries);
	    return ats;
	}
	
	@Override
    public List<AccountThermostatScheduleEntry> getScheduleEntriesForJSON(String jsonString, int accountThermostatScheduleId, 
                                                                          SchedulableThermostatType schedulableThermostatType, ThermostatScheduleMode thermostatMode, 
                                                                          boolean isFahrenheit) {

		JSONObject scheduleObject = JSONObject.fromObject(jsonString);
        JSONObject seasonObject = scheduleObject.getJSONObject("season");

        List<AccountThermostatScheduleEntry> atsEntries = Lists.newArrayList();

        Set<TimeOfWeek> associatedTimeOfWeeks = thermostatMode.getAssociatedTimeOfWeeks();

        // Add the season entries (time/value pairs) for each of the
        // TimeOfWeeks
        for (TimeOfWeek timeOfWeek : associatedTimeOfWeeks) {

        	JSONArray timeOfWeekArray;
        	try {
				timeOfWeekArray = seasonObject.getJSONArray(timeOfWeek.toString());
        	} catch (JSONException e) {
        		continue; // this time of week doesn't exist - continue to the next
        	}

        	for (ThermostatSchedulePeriod period : schedulableThermostatType.getPeriodStyle().getAllPeriods()) {

        	    //synthesize the entry by hand
                AccountThermostatScheduleEntry entry = new AccountThermostatScheduleEntry();
                entry.setAccountThermostatScheduleId(accountThermostatScheduleId);
                entry.setTimeOfWeek(timeOfWeek);
                if (!period.isPsuedo()) {
                    JSONObject jsonObject = timeOfWeekArray.getJSONObject(period.getEntryIndex());

                    int timeMinutes = jsonObject.getInt("time");
                    int coolTemp = jsonObject.getInt("coolTemp");
                    int heatTemp = jsonObject.getInt("heatTemp");

                    // Convert celsius temp to fahrenheit if needed
                    if (!isFahrenheit) {
                        coolTemp = (int)CtiUtilities.convertTemperature(coolTemp, CtiUtilities.CELSIUS_CHARACTER, CtiUtilities.FAHRENHEIT_CHARACTER);
                        heatTemp = (int)CtiUtilities.convertTemperature(heatTemp, CtiUtilities.CELSIUS_CHARACTER, CtiUtilities.FAHRENHEIT_CHARACTER);
                    }

                    entry.setStartTime(timeMinutes * 60); // stored as seconds in DB
                    entry.setCoolTemp(new FahrenheitTemperature(coolTemp));
                    entry.setHeatTemp(new FahrenheitTemperature(heatTemp));
                }

                atsEntries.add(entry);
            }
        }
        
        return atsEntries;
    }
    
	@Override
    public void setToTwoTimeTemps(AccountThermostatSchedule schedule) {

		ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMap = schedule.getEntriesByTimeOfWeekMultimap();
		
        for (TimeOfWeek timeOfWeek : entriesByTimeOfWeekMap.keySet()) {

            List<AccountThermostatScheduleEntry> entryList = entriesByTimeOfWeekMap.get(timeOfWeek);

            // There should always be 4 season entries per season
            if (entryList.size() != 4) {
                throw new IllegalArgumentException("There should be 4 entries in the " + timeOfWeek + " entry list.");
            }

            // Default the time to 0 seconds past midnight and the temp to
            // -1
            AccountThermostatScheduleEntry firstEntry = entryList.get(0);
            firstEntry.setStartTime(0);
            firstEntry.setCoolTemp(new FahrenheitTemperature(-1));
            firstEntry.setHeatTemp(new FahrenheitTemperature(-1));
            
            AccountThermostatScheduleEntry secondEntry = entryList.get(1);
            secondEntry.setStartTime(0);
            secondEntry.setCoolTemp(new FahrenheitTemperature(-1));
            secondEntry.setHeatTemp(new FahrenheitTemperature(-1));
        }
    }
	
	@Override
	public String generateDefaultNameForUnnamedSchdule(AccountThermostatSchedule ats, String thermostatLabel, YukonUserContext yukonUserContext) {
		
		String scheduleName = ats.getScheduleName();
		if (StringUtils.isBlank(scheduleName)) {
    		
			String newScheduleName;
    		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
    		String typeName = messageSourceAccessor.getMessage(ats.getThermostatType().getHardwareType().getDisplayKey());

    		// energy company default
    		if (ats.getAccountId() == 0) {
    			
    			String defaultWord = messageSourceAccessor.getMessage("yukon.web.defaults.default");
    			newScheduleName = typeName + " - " + defaultWord;
    		
    		// associated with a specific account
    		} else {
    			
    			if (StringUtils.isNotBlank(thermostatLabel)) {
    				newScheduleName = thermostatLabel;
    			} else {
    				newScheduleName = typeName;
    			}
    			
    			// avoid duplicate names among schedules for an account
    			Integer suffixDigit = null;
    			List<AccountThermostatSchedule> otherSchedules = accountThermostatScheduleDao.getAllSchedulesForAccountByType(ats.getAccountId(), ats.getThermostatType());
    			for (AccountThermostatSchedule otherSchedule : otherSchedules) {
    				
    				if (otherSchedule.getAccountThermostatScheduleId() != ats.getAccountThermostatScheduleId() &&
    					otherSchedule.getScheduleName().equals(newScheduleName + (suffixDigit == null ? "" : " " + suffixDigit))) {
    					
    					if (suffixDigit == null) {
    						suffixDigit = 1;
    					}
    					suffixDigit++;
    				}
    			}
    			if (suffixDigit != null) {
    				newScheduleName = newScheduleName + " " + suffixDigit;
    			}
    			
    		}
    		
    		return newScheduleName;
    	}
		
		return scheduleName;
	}
	
	@Autowired
	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
	
	@Autowired
	public void setHardwareUiService(HardwareUiService hardwareUiService) {
	    this.hardwareUiService = hardwareUiService;
	}
	
	@Autowired
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
	
	@Autowired
	public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
		this.accountThermostatScheduleDao = accountThermostatScheduleDao;
	}
}
