package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ScheduleDropDownItem;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Controller for Thermostat schedule operations
 */
@CheckRole(ResidentialCustomerRole.ROLEID)
@CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatScheduleController extends AbstractThermostatController {

    private InventoryDao inventoryDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private CustomerDao customerDao;
    private ThermostatService thermostatService;

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

        if (thermostatIds.size() == 1) {
            // single thermsotat selected

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
        } else {
            // multiple thermostats selected

            // Get type of the first thermostat in the list
            int thermostatId = thermostatIds.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            HardwareType type = thermostat.getType();

            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.multipleLabel");
            map.addAttribute("thermostatLabel", resolvable);
            if (schedule == null) {
                schedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId,
                                                                                 type);
            }

            defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId,
                                                                                    type);
        }

        // Add the mode and temperature unit to model
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


        map.addAttribute("mode", ThermostatMode.COOL);
        
        Locale locale = yukonUserContext.getLocale();
        map.addAttribute("localeString", locale.toString());
        
        return "consumer/thermostatSchedule.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String mode, String timeOfWeek, String temperatureUnit, Integer scheduleId,
            String scheduleName, YukonUserContext yukonUserContext, 
            HttpServletRequest request, ModelMap map) throws ServletRequestBindingException {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkThermostatSchedule(user, scheduleId);
        accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        String scheduleString = ServletRequestUtils.getRequiredStringParameter(request,
                                                                               "schedules");
        Boolean applyToAll = ServletRequestUtils.getBooleanParameter(request,
                                                                     "applyToWeekend",
                                                                     false);

        String saveAction = ServletRequestUtils.getStringParameter(request,
                                                                   "saveAction",
                                                                   null);
        boolean sendAndSave = "saveApply".equals(saveAction);

        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) 
                && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(account.getCustomerId(), escapedTempUnit);
        }

        // Create schedule from submitted JSON string
        ThermostatSchedule schedule = getScheduleForJSON(scheduleString,
                                                         isFahrenheit);
        schedule.setName(scheduleName);
        schedule.setAccountId(account.getAccountId());

        ThermostatMode thermostatMode = ThermostatMode.valueOf(mode);
        TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        ThermostatScheduleUpdateResult message = null;

        boolean failed = false;
        for (Integer thermostatId : thermostatIds) {

            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

            HardwareType type = thermostat.getType();
            schedule.setThermostatType(type);
            if (type.equals(HardwareType.COMMERCIAL_EXPRESSSTAT)) {
                this.setToTwoTimeTemps(schedule);
            }

            if (sendAndSave) {
                // Send the schedule to the thermsotat(s) and then update the
                // existing thermostat(s) schedule or create a new schedule for
                // the thermostat(s) if it has none

                schedule.setInventoryId(thermostatId);

                // Send schedule to thermsotat
                message = thermostatService.sendSchedule(account,
                                                         schedule,
                                                         thermostatMode,
                                                         scheduleTimeOfWeek,
                                                         false,
                                                         userContext);

                // Save changes to schedule
                thermostatService.updateSchedule(account,
                                                 schedule,
                                                 thermostatMode,
                                                 scheduleTimeOfWeek,
                                                 applyToAll,
                                                 userContext);
                // If there are multiple thermostats to send schedule to and any
                // of the save/sends fail, the whole thing is failed
                if (message.isFailed()) {
                    failed = true;
                }
            } else {
                // Update the schedule if it exists already or create a new
                // schedule

                schedule.setId(scheduleId);
                schedule.setInventoryId(thermostatId);

                // Save changes to schedule
                thermostatService.updateSchedule(account,
                                                 schedule,
                                                 thermostatMode,
                                                 scheduleTimeOfWeek,
                                                 applyToAll,
                                                 userContext);

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

        Map<ThermostatMode, ThermostatSeason> seasonMap = schedule.getSeasonMap();
        for (ThermostatSeason season : seasonMap.values()) {

            Map<TimeOfWeek, List<ThermostatSeasonEntry>> seasonEntryMap = season.getSeasonEntryMap();
            for (Map.Entry<TimeOfWeek, List<ThermostatSeasonEntry>> entry : seasonEntryMap.entrySet()) {

                TimeOfWeek key = entry.getKey();
                List<ThermostatSeasonEntry> entryList = entry.getValue();

                // There should always be 4 season entries per season
                if (entryList.size() != 4) {
                    throw new IllegalArgumentException("There should be 4 season entries in the " + season.getThermostatMode() + ", " + key.toString() + " season entry list.");
                }

                // Default the time to 0 seconds past midnight and the temp to
                // -1
                ThermostatSeasonEntry firstEntry = entryList.get(1);
                firstEntry.setStartTime(0);
                firstEntry.setTemperature(-1);
                ThermostatSeasonEntry lastEntry = entryList.get(2);
                lastEntry.setStartTime(0);
                lastEntry.setTemperature(-1);
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

        Map<ThermostatMode, ThermostatSeason> seasonMap = schedule.getSeasonMap();
        if (seasonMap.size() != 2) {
            throw new IllegalArgumentException("There should be 2 seasons in a thermostat schedule");
        }

        JSONObject scheduleObject = new JSONObject();
        for (ThermostatMode mode : seasonMap.keySet()) {

            ThermostatSeason season = seasonMap.get(mode);

            JSONObject seasonObject = this.getJSONForSeason(season, isFahrenheit);

            scheduleObject.put(mode.toString(), seasonObject);

        }

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
                Integer temperature = entry.getTemperature();

                JSONObject timeTemp = new JSONObject();
                timeTemp.put("time", time);
                if(isFahrenheit) {
                	timeTemp.put("temp", temperature);
                } else {
                	int celsiusTemperature = (int) CtiUtilities.convertTemperature(
                			temperature,
							CtiUtilities.FAHRENHEIT_CHARACTER,
							CtiUtilities.CELSIUS_CHARACTER);
					timeTemp.put("temp", celsiusTemperature);
                }

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

        JSONObject coolObject = object.getJSONObject(ThermostatMode.COOL.toString());
        ThermostatSeason coolSeason = this.getSeasonForJSON(coolObject,
                                                            isFahrenheit);
        coolSeason.setThermostatMode(ThermostatMode.COOL);
        schedule.addSeason(coolSeason);

        JSONObject heatObject = object.getJSONObject(ThermostatMode.HEAT.toString());
        ThermostatSeason heatSeason = this.getSeasonForJSON(heatObject,
                                                            isFahrenheit);
        heatSeason.setThermostatMode(ThermostatMode.HEAT);
        schedule.addSeason(heatSeason);

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
                Integer temperature = jsonObject.getInt("temp");

                // Convert celsius temp to fahrenheit if needed
                if (!isFahrenheit) {
                    temperature = (int) CtiUtilities.convertTemperature(temperature,
                                                                        CtiUtilities.CELSIUS_CHARACTER,
                                                                        CtiUtilities.FAHRENHEIT_CHARACTER);
                }

                ThermostatSeasonEntry entry = new ThermostatSeasonEntry();
                entry.setStartTime(time);
                entry.setTemperature(temperature);
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
}
