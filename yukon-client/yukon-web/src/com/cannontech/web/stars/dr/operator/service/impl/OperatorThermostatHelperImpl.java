package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
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

	@Autowired private InventoryDao inventoryDao;
	@Autowired private HardwareUiService hardwareUiService;
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	@Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
	@Autowired private ThermostatEventHistoryDao thermostatEventHistoryDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private CustomerEventDao customerEventDao;
    @Autowired private CustomerAccountDao customerAccountDao;
	
	private static final int DEFAULT_ITEMS_PER_PAGE = 10;
	
	@Override
	public List<Integer> setupModelMapForThermostats(String thermostatIds, AccountInfoFragment accountInfoFragment, ModelMap modelMap) throws IllegalArgumentException {
		
	    thermostatIds = thermostatIds.replace("[", "");
	    thermostatIds = thermostatIds.replace("]", "");
		List<Integer> thermostatIdsList = ServletUtil.getIntegerListFromString(thermostatIds);
		hardwareUiService.validateInventoryAgainstAccount(thermostatIdsList, accountInfoFragment.getAccountId());
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		modelMap.addAttribute("thermostatIds", thermostatIds);
		
		// thermostat names
		List<String> thermostatNames = Lists.newArrayListWithCapacity(thermostatIdsList.size());
		SchedulableThermostatType firstType = null;
		for (int thermostatId : thermostatIdsList) {
			Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
			thermostatNames.add(thermostat.getLabel());
			
			SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
			if(firstType == null){
			    firstType = type;
			}
			
			if(type != firstType){
			    throw new IllegalArgumentException("Thermostat types do not match. expected:["+ firstType +"] got:["+ type +"]");
			}
		}
		modelMap.addAttribute("thermostatNames", thermostatNames);
		modelMap.addAttribute("thermostatNameString", StringUtils.join(thermostatNames, ", "));
		
		return thermostatIdsList;
	}
	
	@Override
    public void setupModelMapForCommandHistory(ModelMap modelMap,
            HttpServletRequest request, List<Integer> thermostatIdsList, int accountId) {
	    setupModelMapForCommandHistory(modelMap, request, thermostatIdsList, accountId, DEFAULT_ITEMS_PER_PAGE);
    }
	
	@Override
    public void setupModelMapForCommandHistory(ModelMap modelMap,
            HttpServletRequest request, List<Integer> thermostatIdsList, 
            int accountId, int numPerPage) {
        
	    if (modelMap.get("thermostatIds") == null) {
            modelMap.addAttribute("thermostatIds", thermostatIdsList.get(0));
        }
	    
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIdsList.get(0));
        
        ThermostatManualEvent event;
        // single thermostat
        if (thermostatIdsList.size() == 1) {
            Integer inventoryId = thermostatIdsList.get(0);
            event = customerEventDao.getLastManualEvent(inventoryId);
            modelMap.addAttribute("thermostat", thermostat);
            modelMap.addAttribute("inventoryId", inventoryId);
            modelMap.addAttribute("pageNameSuffix", "single");
            modelMap.addAttribute("displayName", thermostat.getSerialNumber());
        // multiple thermostats
        } else {
            event = new ThermostatManualEvent();
            modelMap.addAttribute("pageNameSuffix", "multiple");
            modelMap.addAttribute("displayName", new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatManual.multipleLabel"));
        }
        modelMap.addAttribute("event", event);

        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        modelMap.addAttribute("temperatureUnit", customer.getTemperatureUnit());
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", numPerPage);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getEventsByThermostatIds(thermostatIdsList);
        SearchResults<ThermostatEvent> result = SearchResults.pageBasedForWholeList(currentPage, itemsPerPage, eventHistoryList);
        modelMap.addAttribute("eventHistoryList", result.getResultList());
        modelMap.addAttribute("searchResult", result);
        modelMap.addAttribute("moreResults", result.getHitCount() > numPerPage);
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
        
        for(ThermostatSchedulePeriod period : schedule.getThermostatType().getPeriodStyle().getAllPeriods()){
            AccountThermostatScheduleEntry entry = entries.get(period.getEntryIndex());
            JSONObject periodJSON = new JSONObject();

            if(period.isPsuedo()){
                periodJSON.put("pseudo", true);
            }else{
                periodJSON.put("pseudo", false);
            }
            
            periodJSON.put("timeOfWeek", entry.getTimeOfWeek().toString());
            periodJSON.put("secondsFromMidnight", entry.getStartTime());
            periodJSON.put("cool_F", entry.getCoolTemp().toFahrenheit().getValue());
            periodJSON.put("heat_F", entry.getHeatTemp().toFahrenheit().getValue());
            scheduleJSON.accumulate("periods", periodJSON);
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
          entry.setCoolTemp(Temperature.fromFahrenheit(period.getDouble("cool_F")));
          entry.setHeatTemp(Temperature.fromFahrenheit(period.getDouble("heat_F")));
          entry.setStartTime(period.getInt("secondsFromMidnight"));
          entries.add(entry);
	    }
	    
	    //add the entries to the schedule
	    ats.setScheduleEntries(entries);
	    return ats;
	}
	
	@Override
    public List<AccountThermostatScheduleEntry> getScheduleEntriesForJSON(String jsonString,
                                                                          int accountThermostatScheduleId, 
                                                                          SchedulableThermostatType schedulableThermostatType,
                                                                          ThermostatScheduleMode thermostatMode, 
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
                        entry.setCoolTemp(Temperature.fromCelsius(coolTemp));
                        entry.setHeatTemp(Temperature.fromCelsius(heatTemp));
                    }else{
                        entry.setCoolTemp(Temperature.fromFahrenheit(coolTemp));
                        entry.setHeatTemp(Temperature.fromFahrenheit(heatTemp));
                    }

                    entry.setStartTime(timeMinutes * 60); // stored as seconds in DB
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
            firstEntry.setCoolTemp(Temperature.fromFahrenheit(-1));
            firstEntry.setHeatTemp(Temperature.fromFahrenheit(-1));
            
            AccountThermostatScheduleEntry secondEntry = entryList.get(1);
            secondEntry.setStartTime(0);
            secondEntry.setCoolTemp(Temperature.fromFahrenheit(-1));
            secondEntry.setHeatTemp(Temperature.fromFahrenheit(-1));
        }
    }
	
	@Override
	public String generateDefaultNameForUnnamedSchdule(AccountThermostatSchedule ats, String thermostatLabel, YukonUserContext yukonUserContext) {
		
		String scheduleName = ats.getScheduleName();
		if (StringUtils.isBlank(scheduleName)) {
    		
			String newScheduleName;
    		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
    		String typeName = messageSourceAccessor.getMessage(ats.getThermostatType().getHardwareType());

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
}
