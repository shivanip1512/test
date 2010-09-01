package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareService;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class OperatorThermostatHelperImpl implements OperatorThermostatHelper {

	private InventoryDao inventoryDao;
	private HardwareService hardwareService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private AccountThermostatScheduleDao accountThermostatScheduleDao;
	
	@Override
	public List<Integer> setupModelMapForThermostats(String thermostatIds, AccountInfoFragment accountInfoFragment, ModelMap modelMap) {
		
		List<Integer> thermostatIdsList = ServletUtil.getIntegerListFromString(thermostatIds);
		hardwareService.validateInventoryAgainstAccount(thermostatIdsList, accountInfoFragment.getAccountId());
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
            	
            	int time = atsEntry.getStartTimeMinutes();
                int coolTemp = atsEntry.getCoolTemp();
                int heatTemp = atsEntry.getHeatTemp();

                JSONObject timeTemp = new JSONObject();
                timeTemp.put("time", time);
                
                String tempUnit = (isFahrenheit) ? CtiUtilities.FAHRENHEIT_CHARACTER : CtiUtilities.CELSIUS_CHARACTER;
                
                coolTemp = (int)CtiUtilities.convertTemperature(coolTemp, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
                heatTemp = (int)CtiUtilities.convertTemperature(heatTemp, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
                
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

            for (Object object : timeOfWeekArray.toArray()) {
            	
                JSONObject jsonObject = (JSONObject) object;

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
		if (StringUtils.isBlank(scheduleName) || scheduleName.equals(CtiUtilities.STRING_NONE)) {
    		
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
	public void setHardwareService(HardwareService hardwareService) {
	    this.hardwareService = hardwareService;
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
