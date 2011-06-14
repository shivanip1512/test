package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
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
	private RolePropertyDao rolePropertyDao;
	
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
	public JSONObject getJSONForSchedule(AccountThermostatSchedule schedule, boolean isFahrenheit) {

        JSONObject scheduleObject = new JSONObject();
        JSONObject seasonObject = new JSONObject();
        
        Multimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMap = schedule.getEntriesByTimeOfWeekMultimap();
        
        for (TimeOfWeek timeOfWeek : entriesByTimeOfWeekMap.keySet()) {

            Collection<AccountThermostatScheduleEntry> atsEntryList = entriesByTimeOfWeekMap.get(timeOfWeek);

            for (AccountThermostatScheduleEntry atsEntry : atsEntryList) {
            	
            	Integer time = atsEntry.getStartTimeMinutes();
            	Integer coolTemp = atsEntry.getCoolTemp().getIntValue();
            	Integer heatTemp = atsEntry.getHeatTemp().getIntValue();

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
            if (entry.getCoolTemp().getIntValue() == -1 && entry.getHeatTemp().getIntValue() == -1) {
                // temp of -1 means ignore this time/temp pair - used when only
                // sending two time/temp values
                period.put("pseudo", true);
            }else{
                period.put("pseudo", false);
            }
            
            period.put("timeOfWeek", entry.getTimeOfWeek().toString());
            period.put("secondsFromMidnight", entry.getStartTime());
            period.put("cool_F", entry.getCoolTemp().getValue());
            period.put("heat_F", entry.getHeatTemp().getValue());
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
	    List<AccountThermostatScheduleEntry> entries = new ArrayList();
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
	public JSONObject ThermostatToJSON(Thermostat thermostat, YukonUserContext yukonUserContext){
	    JSONObject thermostatJson = new JSONObject();
	    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);

        //all supported schedule modes: ALL, WEEKDAY_WEEKEND, etc...
        JSONObject modesJson = new JSONObject();
        JSONObject dayLabelJson = new JSONObject();
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        Set<ThermostatScheduleMode> modes = type.getSupportedScheduleModes();
        for(ThermostatScheduleMode mode : modes){
            //check if user can actually use this mode
            if(isModeAllowed(mode, yukonUserContext.getYukonUser())){
                modesJson.put(mode.toString(), messageSourceAccessor.getMessage("yukon.dr.consumer.thermostat."+mode.toString()));
                
//                Set<TimeOfWeek>days = mode.getAssociatedTimeOfWeeks();
//                for(TimeOfWeek day : days) {
//                    if(!dayLabelJson.containsKey(day.toString())){
//                        JSONObject label = new JSONObject();
//                        label.put("name", messageSourceAccessor.getMessage("yukon.dr.consumer.thermostat.schedule."+day.toString()));
//                        label.put("abbr", messageSourceAccessor.getMessage("yukon.dr.consumer.thermostat.schedule."+day.toString()+"_abbr"));
//                        dayLabelJson.put(day.toString(), label);
//                    }
//                }
            }
        }
        
        TimeOfWeek days[] = TimeOfWeek.values();
        for(TimeOfWeek day : days){
            if(!dayLabelJson.containsKey(day.toString())){
                JSONObject label = new JSONObject();
                label.put("name", messageSourceAccessor.getMessage("yukon.dr.consumer.thermostat.schedule."+day.toString()));
                label.put("abbr", messageSourceAccessor.getMessage("yukon.dr.consumer.thermostat.schedule."+day.toString()+"_abbr"));
                dayLabelJson.put(day.toString(), label);
            }
        }
        
        thermostatJson.put("modes", modesJson);
        thermostatJson.put("dayLabels", dayLabelJson);
        
        //limits
        thermostatJson.put("lowerCoolF", type.getLowerLimitCoolInFahrenheit());
        thermostatJson.put("upperCoolF", type.getUpperLimitCoolInFahrenheit());
        thermostatJson.put("lowerHeatF", type.getLowerLimitHeatInFahrenheit());
        thermostatJson.put("upperHeatF", type.getUpperLimitHeatInFahrenheit());
        
        //get the heading labels into a page var: WAKE, SLEEP, etc...
        JSONObject periodsJson = new JSONObject();
        List<ThermostatSchedulePeriod> periods = type.getPeriodStyle().getRealPeriods();
        for(ThermostatSchedulePeriod period : periods){
            JSONObject periodJson = new JSONObject();
            periodJson.put("name", messageSourceAccessor.getMessage("yukon.dr.consumer.thermostatSchedule."+period.toString()));
            periodJson.put("order", period.getEntryIndex());
            periodsJson.put(period.toString(), periodJson);
        }
        thermostatJson.put("periods", periodsJson);
        thermostatJson.put("supportedPeriods", type.getPeriodStyle().getRealPeriods().size());
        
        AccountThermostatSchedule thermostatCurrentSchedule = accountThermostatScheduleDao.findByInventoryId(thermostat.getId());
        if (thermostatCurrentSchedule != null) {
            thermostatJson.put("currentScheduleId", thermostatCurrentSchedule.getAccountThermostatScheduleId());
        }
	    
	    return thermostatJson;
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
	
	@Override
	public List<ThermostatScheduleDisplay> getScheduleDisplays(YukonUserContext yukonUserContext, 
	                                                           String type, 
	                                                           ThermostatScheduleMode thermostatScheduleMode,
	                                                           AccountThermostatSchedule accountThermostatSchedule, 
	                                                           boolean displayAsFahrenheit, 
	                                                           String i18nKey){
	    
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
                int coolTemp = entry.getCoolTemp().getIntValue();
                int heatTemp = entry.getHeatTemp().getIntValue();
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
	
	public Set<ThermostatScheduleMode> getAllowedModes(LiteYukonUser user) {
	    Set<ThermostatScheduleMode> allowedModes = EnumSet.allOf(ThermostatScheduleMode.class);
	    boolean schedule52Enabled = rolePropertyDao.checkAnyProperties(user, YukonRoleProperty.RESIDENTIAL_THERMOSTAT_SCHEDULE_5_2, YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2);
	    if(!schedule52Enabled) {
	        allowedModes.remove(ThermostatScheduleMode.WEEKDAY_WEEKEND);
	    }
	    return allowedModes;
	}
	
	@Override
	public List<ThermostatScheduleMode> getAllowedModesForUserAndType(LiteYukonUser user, SchedulableThermostatType type) {
        List<ThermostatScheduleMode> modes = Lists.newArrayList();
        modes.addAll(type.getSupportedScheduleModes());
        boolean schedule52Enabled = rolePropertyDao.checkAnyProperties(user, YukonRoleProperty.RESIDENTIAL_THERMOSTAT_SCHEDULE_5_2, YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2);
        if(!schedule52Enabled) {
            modes.remove(ThermostatScheduleMode.WEEKDAY_WEEKEND);
        }
        boolean schedule7Enabled = rolePropertyDao.checkAnyProperties(user, YukonRoleProperty.RESIDENTIAL_THERMOSTAT_SCHEDULE_7, YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_7);
        if(!schedule7Enabled) {
            modes.remove(ThermostatScheduleMode.SINGLE);
        }
        
        return modes;
    }
	
	@Override
	public List<SchedulableThermostatType> getCompatibleSchedulableThermostatTypes(Thermostat thermostat) {
	    List<SchedulableThermostatType> types = Lists.newArrayList();
	    SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        
        // add similar types? eg. utilityProG2 is compatible with existing utilitypro schedules
        types.add(type);
        
        switch(thermostat.getType()){
        case UTILITY_PRO_G3:
            //both UtilityPro and UtilityPro G2 are valid
            types.add(SchedulableThermostatType.getByHardwareType(HardwareType.UTILITY_PRO_G2));
        case UTILITY_PRO_G2:
            //UtilityPro is also valid
            types.add(SchedulableThermostatType.getByHardwareType(HardwareType.UTILITY_PRO));
            break;
        default:
            break;
        }
        
	    return types;
	}
	
	public boolean isModeAllowed(ThermostatScheduleMode mode, LiteYukonUser user) {
	    return getAllowedModes(user).contains(mode);
	}
	
	public ThermostatScheduleMode getAdjustedScheduleMode(AccountThermostatSchedule schedule, LiteYukonUser user) {
	    ThermostatScheduleMode scheduleMode = schedule.getThermostatScheduleMode();
	    if(!isModeAllowed(scheduleMode, user)) {
	        scheduleMode = schedule.getThermostatType().getFirstSupportedThermostatScheduleMode(getAllowedModes(user));
	    }
	    return scheduleMode;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
	    this.rolePropertyDao = rolePropertyDao;
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
