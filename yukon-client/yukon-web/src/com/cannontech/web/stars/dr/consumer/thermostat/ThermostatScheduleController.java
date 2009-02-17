package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ScheduleDropDownItem;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Controller for Consumer-side Thermostat schedule operations
 */
@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatScheduleController extends AbstractThermostatController {

    private InventoryDao inventoryDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private CustomerDao customerDao;
    private ThermostatService thermostatService;
    private DateFormattingService dateFormattingService;

    @RequestMapping(value = "/consumer/thermostat/schedule/view", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount account, 
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            Integer scheduleId, YukonUserContext yukonUserContext, ModelMap map) {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkThermostatSchedule(user, scheduleId);
        accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        ThermostatSchedule schedule = null;
        ThermostatSchedule defaultSchedule = null;
        int accountId = account.getAccountId();
        if (scheduleId != null) {
            // Get the schedule that the user selected
            schedule = thermostatScheduleDao.getThermostatScheduleById(scheduleId,
                                                                       accountId);
        }

        HardwareType thermostatType = null;
        if (thermostatIds.size() == 1) {
            // single thermostat selected

            int thermostatId = thermostatIds.get(0);

            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.label",
                                                                                       thermostat.getLabel());
            map.addAttribute("thermostatLabel", resolvable);

            if (schedule == null) {
                // Get the current (or default if no current) schedule
                schedule = thermostatService.getThermostatSchedule(thermostat,
                                                                   account);

                if (StringUtils.isBlank(schedule.getName())) {
                    schedule.setName(thermostat.getLabel());
                }
            }

            defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId,
                                                                                    thermostat.getType());
            thermostatType = thermostat.getType();
        } else {
            // multiple thermostats selected

            // Get type of the first thermostat in the list
            int thermostatId = thermostatIds.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
			thermostatType = thermostat.getType();

            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.multipleLabel");
            map.addAttribute("thermostatLabel", resolvable);
            if (schedule == null) {
                schedule = thermostatService.getThermostatSchedule(thermostat, account);
            }

            defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId,
                                                                                    thermostatType);
        }

        // Add the temperature unit to model
        LiteCustomer customer = customerDao.getCustomerForUser(user.getUserID());
        String temperatureUnit = customer.getTemperatureUnit();
        map.addAttribute("temperatureUnit", temperatureUnit);

        // Get json string for schedule and add schedule and string to model
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = this.getJSONForSchedule(schedule, isFahrenheit);
        map.addAttribute("scheduleJSONString", scheduleJSON.toString());
        map.addAttribute("schedule", schedule);

        // Get json string for the default schedule and add to model
        JSONObject defaultScheduleJSON = this.getJSONForSchedule(defaultSchedule, isFahrenheit);
        map.addAttribute("defaultScheduleJSONString",
                         defaultScheduleJSON.toString());

        Locale locale = yukonUserContext.getLocale();
        map.addAttribute("localeString", locale.toString());

        map.addAttribute("thermostatType", thermostatType.toString());
        
        return "consumer/thermostatSchedule.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/confirm", method = RequestMethod.POST)
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
            String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
            @RequestParam(value="schedules", required=true) String scheduleString,
            ModelMap map) throws ServletRequestBindingException {
    	
    	boolean sendAndSave = "saveApply".equals(saveAction);
    	if(!sendAndSave) {
    		// Don't show confirm page if this is just a save not a save/send
    		return "forward:/spring/stars/consumer/thermostat/schedule/save";
    	}
    	map.addAttribute("saveAction", saveAction);
    	
    	// Create the confirm schedule text
    	boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
    	ThermostatSchedule schedule = this.getScheduleForJSON(scheduleString, isFahrenheit);
    	
    	MessageSource messageSource = messageSourceResolver.getMessageSource(yukonUserContext);
    	TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);
    	YukonMessageSourceResolvable timeOfWeekString = 
    		new YukonMessageSourceResolvable(scheduleTimeOfWeek.getDisplayKey());
    	
    	List<Object> argumentList = new ArrayList<Object>();
    	argumentList.add(timeOfWeekString);
    	
    	// Add the thermostat label to the model
    	Thermostat thermostat = null;
        int thermostatId = thermostatIds.get(0);

		thermostat = inventoryDao.getThermostatById(thermostatId);
        YukonMessageSourceResolvable resolvable = 
        	new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.label",
        									 thermostat.getLabel());
        map.addAttribute("thermostatLabel", resolvable);
    	
        String scheduleConfirmKey = "yukon.dr.consumer.thermostatScheduleConfirm.scheduleText";
    	if (HardwareType.COMMERCIAL_EXPRESSSTAT.equals(thermostat.getType())) {
    		this.setToTwoTimeTemps(schedule);
    		scheduleConfirmKey = 
    			"yukon.dr.consumer.thermostatScheduleConfirm.scheduleTextTwoTimeTemp";
    	}

    	List<ThermostatSeasonEntry> seasonEntries = 
    		schedule.getSeason().getSeasonEntries(scheduleTimeOfWeek);
    	
    	for(ThermostatSeasonEntry entry : seasonEntries) {
    		Date startDate = entry.getStartDate();
    		Integer coolTemperature = entry.getCoolTemperature();
    		Integer heatTemperature = entry.getHeatTemperature();
    		
    		String startDateString = 
    			dateFormattingService.formatDate(startDate, DateFormatEnum.TIME, yukonUserContext);

    		// Temperatures are only -1 if this is a 2 time temp thermostat type - ignore if -1
    		if(coolTemperature != -1 && heatTemperature != -1) {
	    		argumentList.add(startDateString);
	    		argumentList.add(coolTemperature);
	    		argumentList.add(heatTemperature);
    		}
    	}
    	
    	String scheduleConfirm = messageSource.getMessage(
						    			scheduleConfirmKey, 
						    			argumentList.toArray(), 
						    			yukonUserContext.getLocale());
    	map.addAttribute("scheduleConfirm", scheduleConfirm);
    	
    	// Pass all of the parameters through to confirm page
    	map.addAttribute("schedules", scheduleString);
    	map.addAttribute("timeOfWeek", timeOfWeek);
    	map.addAttribute("scheduleMode", scheduleMode);
    	map.addAttribute("temperatureUnit", temperatureUnit);
    	map.addAttribute("scheduleId", scheduleId);
    	map.addAttribute("scheduleName", scheduleName);
    	
    	return "consumer/thermostatScheduleConfirm.jsp";
    }    	

    @RequestMapping(value = "/consumer/thermostat/schedule/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("customerAccount") CustomerAccount account,
			@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
			String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
			String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
			@RequestParam(value="schedules", required=true) String scheduleString,
			ModelMap map) throws ServletRequestBindingException {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkThermostatSchedule(user, scheduleId);
        accountCheckerService.checkInventory(user, thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        boolean sendAndSave = "saveApply".equals(saveAction);

        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(account.getCustomerId(), escapedTempUnit);
        }

        // Create schedule from submitted JSON string
        ThermostatSchedule schedule = getScheduleForJSON(scheduleString,
                                                         isFahrenheit);
        schedule.setName(scheduleName);
        schedule.setAccountId(account.getAccountId());

        TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);
        ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);

        ThermostatScheduleUpdateResult message = null;

        boolean failed = false;
        for (Integer thermostatId : thermostatIds) {

        	ThermostatSchedule thermostatSchedule = 
        		thermostatScheduleDao.getThermostatScheduleByInventoryId(thermostatId);
        	if(thermostatSchedule != null) {
        		// Set id so schedule gets updated
        		schedule.setId(thermostatSchedule.getId());
        	} else {
        		// Set id so schedule gets created
        		schedule.setId(null);
        	}
        	
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

            HardwareType type = thermostat.getType();
            schedule.setThermostatType(type);
            if (type.equals(HardwareType.COMMERCIAL_EXPRESSSTAT)) {
                this.setToTwoTimeTemps(schedule);
            }

            boolean applyToAll = ThermostatScheduleMode.ALL.equals(thermostatScheduleMode);
			if (sendAndSave) {
                // Send the schedule to the thermsotat(s) and then update the
                // existing thermostat(s) schedule or create a new schedule for
                // the thermostat(s) if it has none

                schedule.setInventoryId(thermostatId);

                // Send schedule to thermsotat
                message = thermostatService.sendSchedule(account,
                                                         schedule,
                                                         scheduleTimeOfWeek,
                                                         thermostatScheduleMode,
                                                         applyToAll,
                                                         yukonUserContext);

                // Save changes to schedule
                thermostatService.updateSchedule(account,
                                                 schedule,
                                                 scheduleTimeOfWeek,
                                                 applyToAll ,
                                                 yukonUserContext);
                // If there are multiple thermostats to send schedule to and any
                // of the save/sends fail, the whole thing is failed
                if (message.isFailed()) {
                    failed = true;
                }
            } else {
                // Update the schedule if it exists already or create a new
                // schedule

                schedule.setInventoryId(thermostatId);

                // Save changes to schedule
                thermostatService.updateSchedule(account,
                                                 schedule,
                                                 scheduleTimeOfWeek,
                                                 applyToAll,
                                                 yukonUserContext);

                message = ThermostatScheduleUpdateResult.CONSUMER_SAVE_SCHEDULE_SUCCESS;
            }
			
        }

        // If there was a failure and we are processing multiple
        // thermostats, set error to generic multiple error
        if (failed && thermostatIds.size() > 1) {
            message = ThermostatScheduleUpdateResult.CONSUMER_MULTIPLE_ERROR;
        }

        map.addAttribute("message", message.toString());

        // Manually put thermsotatIds into model for redirect
        map.addAttribute("thermostatIds", thermostatIds.toString());

        return "redirect:/spring/stars/consumer/thermostat/schedule/complete";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/complete", method = RequestMethod.GET)
    public String updateComplete(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String message, LiteYukonUser user, ModelMap map) throws Exception {

        accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        ThermostatScheduleUpdateResult resultMessage = ThermostatScheduleUpdateResult.valueOf(message);
        String key = resultMessage.getDisplayKey();

        YukonMessageSourceResolvable resolvable;

        if (thermostatIds.size() == 1) {
            int thermostatId = thermostatIds.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

            resolvable = new YukonMessageSourceResolvable(key,
                                                          thermostat.getLabel());

        } else {
            resolvable = new YukonMessageSourceResolvable(key);
        }

        map.addAttribute("message", resolvable);

        map.addAttribute("thermostatIds", thermostatIds);

        map.addAttribute("viewUrl",
                         "/spring/stars/consumer/thermostat/schedule/view");

        return "consumer/actionComplete.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/hints", method = RequestMethod.GET)
    public String hints(ModelMap map) {
        return "consumer/scheduleHints.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/view/saved", method = RequestMethod.GET)
    public String viewSaved(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            LiteYukonUser user, ModelMap map) throws Exception {

        accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIds.get(0));
        HardwareType type = thermostat.getType();
        
        int accountId = account.getAccountId();
        List<ScheduleDropDownItem> schedules = thermostatScheduleDao.getSavedThermostatSchedulesByAccountId(accountId, type);
        map.addAttribute("schedules", schedules);

        return "consumer/savedSchedules.jsp";
    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/saved", method = RequestMethod.POST, params = "delete")
    public String delete(HttpServletRequest request, @ModelAttribute("customerAccount") CustomerAccount account,
    		String thermostatIds, Integer scheduleId, LiteYukonUser user, ModelMap map) throws Exception {
    	
    	List<Integer> thermostatIdsList = getThermostatIds(request);
    	
    	accountCheckerService.checkInventory(user, thermostatIdsList.toArray(new Integer[thermostatIdsList.size()]));
    	
    	thermostatScheduleDao.delete(scheduleId);

    	map.addAttribute("thermostatIds", thermostatIds);
    	
    	return "redirect:/spring/stars/consumer/thermostat/schedule/view/saved";
    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/saved", method = RequestMethod.POST, params = "view")
    public String viewSchedule(String thermostatIds, Integer scheduleId, ModelMap map) throws Exception {
    	map.addAttribute("scheduleId", scheduleId);
    	map.addAttribute("thermostatIds", thermostatIds);
    	return "redirect:/spring/stars/consumer/thermostat/schedule/view";
    }

    /**
     * Helper method to default the 2nd and 3rd time/temp values for a two
     * time/temp schedule
     * @param schedule - Schedule to default
     */
    private void setToTwoTimeTemps(ThermostatSchedule schedule) {

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
    private JSONObject getJSONForSchedule(ThermostatSchedule schedule, boolean isFahrenheit) {

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
    private JSONObject getJSONForSeason(ThermostatSeason season, boolean isFahrenheit) {

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
    private ThermostatSchedule getScheduleForJSON(String jsonString,
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
    private ThermostatSeason getSeasonForJSON(JSONObject seasonObject,
            boolean isFahrenheit) {

        ThermostatSeason season = new ThermostatSeason();

        List<TimeOfWeek> timeOfWeekList = new ArrayList<TimeOfWeek>();
        timeOfWeekList.add(TimeOfWeek.WEEKDAY);
        timeOfWeekList.add(TimeOfWeek.SATURDAY);
        timeOfWeekList.add(TimeOfWeek.SUNDAY);

        // Add the season entries (time/value pairs) for each of the
        // TimeOfWeeks
        for (TimeOfWeek timeOfWeek : timeOfWeekList) {

            JSONArray timeOfWeekArray = seasonObject.getJSONArray(timeOfWeek.toString());

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

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setThermostatScheduleDao(
            ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Autowired
    public void setThermostatService(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }
    
    @Autowired
    public void setDateFormattingService(
			DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}

}
