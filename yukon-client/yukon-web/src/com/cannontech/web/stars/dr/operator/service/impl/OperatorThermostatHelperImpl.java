package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sf.jsonOLD.JSONArray;
import net.sf.jsonOLD.JSONException;
import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleDisplay;
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
import com.google.common.collect.Multimap;

public class OperatorThermostatHelperImpl implements OperatorThermostatHelper {

	private InventoryDao inventoryDao;
	private HardwareUiService hardwareUiService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private AccountThermostatScheduleDao accountThermostatScheduleDao;
	private DateFormattingService dateFormattingService;
	
	@Override
	public List<Integer> setupModelMapForThermostats(String thermostatIds, AccountInfoFragment accountInfoFragment, ModelMap modelMap) {
		
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
	public JSONObject getJSONForSchedule(AccountThermostatSchedule schedule, boolean isFahrenheit) {

        JSONObject scheduleObject = new JSONObject();
        JSONObject seasonObject = new JSONObject();
        
        Multimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMap = schedule.getEntriesByTimeOfWeekMultimap();
        
        for (TimeOfWeek timeOfWeek : entriesByTimeOfWeekMap.keySet()) {

            Collection<AccountThermostatScheduleEntry> atsEntryList = entriesByTimeOfWeekMap.get(timeOfWeek);

            for (AccountThermostatScheduleEntry atsEntry : atsEntryList) {
            	
            	Integer time = atsEntry.getStartTimeMinutes();
            	Integer coolTemp = atsEntry.getCoolTemp();
            	Integer heatTemp = atsEntry.getHeatTemp();

                JSONObject timeTemp = new JSONObject();
                timeTemp.put("time", time);
                
                String tempUnit = (isFahrenheit) ? CtiUtilities.FAHRENHEIT_CHARACTER : CtiUtilities.CELSIUS_CHARACTER;
                
                coolTemp = (Integer)CtiUtilities.convertTemperature(coolTemp, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
                heatTemp = (Integer)CtiUtilities.convertTemperature(heatTemp, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
                
                timeTemp.put("coolTemp", coolTemp);
                timeTemp.put("heatTemp", heatTemp);

                seasonObject.accumulate(timeOfWeek.toString(), timeTemp);
            }
        }

        scheduleObject.put("season", seasonObject);


        return scheduleObject;
    }
	
    
	@Override
    public List<AccountThermostatScheduleEntry> getScheduleEntriesForJSON(String jsonString, int accountThermostatScheduleId, ThermostatScheduleMode mode, boolean isFahrenheit) {

		JSONObject scheduleObject = new JSONObject(jsonString);
        JSONObject seasonObject = scheduleObject.getJSONObject("season");

        AccountThermostatSchedule accountThermostatSchedule = accountThermostatScheduleDao.getById(accountThermostatScheduleId);

        List<AccountThermostatScheduleEntry> atsEntries = Lists.newArrayList();

        Set<TimeOfWeek> associatedTimeOfWeeks = mode.getAssociatedTimeOfWeeks();

        // Add the season entries (time/value pairs) for each of the
        // TimeOfWeeks
        for (TimeOfWeek timeOfWeek : associatedTimeOfWeeks) {

        	JSONArray timeOfWeekArray;
        	try {
				timeOfWeekArray = seasonObject.getJSONArray(timeOfWeek.toString());
        	} catch (JSONException e) {
        		continue; // this time of week doesn't exist - continue to the next
        	}

        	List timeOfWeekList = Lists.newArrayList(timeOfWeekArray.toArray());
            for (int i = 0; i < timeOfWeekList.size(); i++) {
            	
                if(accountThermostatSchedule.getThermostatType().getPeriodStyle() == ThermostatSchedulePeriodStyle.TWO_TIMES &&
                   i < 2) {
                   
                    AccountThermostatScheduleEntry entry = new AccountThermostatScheduleEntry();
                    entry.setAccountThermostatScheduleId(accountThermostatScheduleId);
                    entry.setTimeOfWeek(timeOfWeek);
                    atsEntries.add(entry);
                    continue;
                    
                }

                JSONObject jsonObject = (JSONObject) timeOfWeekList.get(i);

                int timeMinutes = jsonObject.getInt("time");
                int coolTemp = jsonObject.getInt("coolTemp");
                int heatTemp = jsonObject.getInt("heatTemp");

                // Convert celsius temp to fahrenheit if needed
                if (!isFahrenheit) {
                    coolTemp = (int)CtiUtilities.convertTemperature(coolTemp, CtiUtilities.CELSIUS_CHARACTER, CtiUtilities.FAHRENHEIT_CHARACTER);
                    heatTemp = (int)CtiUtilities.convertTemperature(heatTemp, CtiUtilities.CELSIUS_CHARACTER, CtiUtilities.FAHRENHEIT_CHARACTER);
                }

                AccountThermostatScheduleEntry entry = new AccountThermostatScheduleEntry();
                entry.setAccountThermostatScheduleId(accountThermostatScheduleId);
                entry.setStartTime(timeMinutes * 60); // stored as seconds in DB
                entry.setCoolTemp(coolTemp);
                entry.setHeatTemp(heatTemp);
                entry.setTimeOfWeek(timeOfWeek);
                
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
            firstEntry.setCoolTemp(-1);
            firstEntry.setHeatTemp(-1);
            
            AccountThermostatScheduleEntry secondEntry = entryList.get(1);
            secondEntry.setStartTime(0);
            secondEntry.setCoolTemp(-1);
            secondEntry.setHeatTemp(-1);
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
	
	@Override
	public List<ThermostatScheduleDisplay> getScheduleDisplays(
	           YukonUserContext yukonUserContext, String type, ThermostatScheduleMode thermostatScheduleMode,
	           AccountThermostatSchedule accountThermostatSchedule, boolean displayAsFahrenheit, String i18nKey){
	    
	    List<ThermostatScheduleDisplay> scheduleDisplays = Lists.newArrayList();
	    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
	    
	    for(TimeOfWeek timeOfWeek : thermostatScheduleMode.getAssociatedTimeOfWeeks()){
	        TimeOfWeek timeOfWeekForDisplay = timeOfWeek;
	        if (thermostatScheduleMode == ThermostatScheduleMode.ALL && timeOfWeek == TimeOfWeek.WEEKDAY) {
	            timeOfWeekForDisplay = TimeOfWeek.EVERYDAY;
	        }
	        YukonMessageSourceResolvable timeOfWeekResolvable = new YukonMessageSourceResolvable(timeOfWeekForDisplay.getDisplayKey());
	        String timeOfWeekString = messageSourceAccessor.getMessage(timeOfWeekResolvable);
	        ThermostatScheduleDisplay scheduleDisplay = new ThermostatScheduleDisplay();
	        scheduleDisplay.setTimeOfWeekString(timeOfWeekString);
	        
	        List<AccountThermostatScheduleEntry> entries = accountThermostatSchedule.getEntriesByTimeOfWeekMultimap().get(timeOfWeek);
	        
	        if(entries.size()==0){
                //No entries for this time of week on this schedule.  Copy entries from Weekday
                entries = accountThermostatSchedule.getEntriesByTimeOfWeekMultimap().get(TimeOfWeek.WEEKDAY);
            }
	        ThermostatSchedulePeriodStyle periodStyle = accountThermostatSchedule.getThermostatType().getPeriodStyle();
	        
	        for(ThermostatSchedulePeriod period : periodStyle.getRealPeriods()){
	            AccountThermostatScheduleEntry entry = entries.get(period.getEntryIndex());
	            LocalTime startTime = entry.getStartTimeLocalTime();
                String startDateString = dateFormattingService.format(startTime, DateFormatEnum.TIME, yukonUserContext);
                int coolTemp = entry.getCoolTemp();
                int heatTemp = entry.getHeatTemp();
                String tempUnit = (displayAsFahrenheit) ? CtiUtilities.FAHRENHEIT_CHARACTER : CtiUtilities.CELSIUS_CHARACTER;
                coolTemp = (int)CtiUtilities.convertTemperature(coolTemp, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
                heatTemp = (int)CtiUtilities.convertTemperature(heatTemp, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);

                String timeCoolHeatString = messageSourceAccessor.getMessage(i18nKey, startDateString, coolTemp, heatTemp);
                scheduleDisplay.addToEntryList(timeCoolHeatString);
	        }
	        scheduleDisplays.add(scheduleDisplay);
	    }
	    return scheduleDisplays;
	}
	
	public ThermostatScheduleMode getAdjustedScheduleMode(AccountThermostatSchedule schedule, boolean schedule52Enabled) {
        ThermostatScheduleMode scheduleMode = schedule.getThermostatScheduleMode();
        if (scheduleMode == ThermostatScheduleMode.WEEKDAY_WEEKEND && (schedule.getThermostatType() != SchedulableThermostatType.UTILITY_PRO || !schedule52Enabled)) {
            scheduleMode = ThermostatScheduleMode.WEEKDAY_SAT_SUN;
        }
        return scheduleMode;
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
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService){
	    this.dateFormattingService = dateFormattingService;
	}
}
