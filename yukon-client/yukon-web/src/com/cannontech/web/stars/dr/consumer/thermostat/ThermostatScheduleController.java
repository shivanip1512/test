package com.cannontech.web.stars.dr.consumer.thermostat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.AbstractConsumerController;

/**
 * Controller for Thermostat schedule operations
 */
@Controller
public class ThermostatScheduleController extends AbstractConsumerController {

    private InventoryDao inventoryDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private CustomerDao customerDao;
    private ThermostatService thermostatService;

    private static final SimpleDateFormat SCHEDULE_TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    @RequestMapping(value = "/consumer/thermostat/schedule/view", method = RequestMethod.GET)
    public String view(HttpServletRequest request, ModelMap map,
            int thermostatId, @ModelAttribute("customerAccount")
            CustomerAccount account) {

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        map.addAttribute("thermostat", thermostat);

        // Get the current (or default if no current) schedule and add to model
        ThermostatSchedule schedule = thermostatService.getThermostatSchedule(thermostat,
                                                                              account);
        if (StringUtils.isBlank(schedule.getName())) {
            schedule.setName(thermostat.getLabel());
        }

        JSONObject scheduleJSON = this.getJSONForSchedule(schedule);
        map.addAttribute("scheduleJSONString", scheduleJSON.toString());
        map.addAttribute("schedule", schedule);

        // Get the default schedule and add to model
        ThermostatSchedule defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(account.getAccountId(),
                                                                                                   thermostat.getType());
        JSONObject defaultScheduleJSON = this.getJSONForSchedule(defaultSchedule);
        map.addAttribute("defaultScheduleJSONString",
                         defaultScheduleJSON.toString());

        // Add the mode and temperature unit to model
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        LiteCustomer customer = customerDao.getCustomerForUser(user.getUserID());
        String temperatureUnit = customer.getTemperatureUnit();
        map.addAttribute("temperatureUnit", temperatureUnit);

        map.addAttribute("mode", ThermostatMode.COOL);

        return "consumer/thermostatSchedule.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/save", method = RequestMethod.POST)
    public String save(HttpServletRequest request, ModelMap map,
            int thermostatId, String mode, String timeOfWeek,
            String temperatureUnit, @ModelAttribute("customerAccount")
            CustomerAccount account) throws ServletRequestBindingException {

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

        String scheduleString = ServletRequestUtils.getRequiredStringParameter(request,
                                                                               "schedules");
        Boolean applyToAll = ServletRequestUtils.getBooleanParameter(request,
                                                                     "applyToWeekend",
                                                                     false);

        String saveAction = ServletRequestUtils.getStringParameter(request,
                                                                   "saveAction",
                                                                   null);
        boolean sendSchedule = "saveApply".equals(saveAction);

        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);

        // Create schedule from submitted JSON string
        ThermostatSchedule schedule = getScheduleForJSON(scheduleString,
                                                         isFahrenheit);
        schedule.setInventoryId(thermostatId);
        schedule.setAccountId(account.getAccountId());
        HardwareType type = thermostat.getType();
        schedule.setThermostatType(type);

        if (type.equals(HardwareType.COMMERCIAL_EXPRESSSTAT)) {
            this.setToTwoTimeTemps(schedule);
        }

        ThermostatMode thermostatMode = ThermostatMode.valueOf(mode);
        TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // Save changes to schedule
        thermostatService.updateSchedule(account,
                                         schedule,
                                         thermostatMode,
                                         scheduleTimeOfWeek,
                                         applyToAll,
                                         userContext);

        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.CONSUMER_SAVE_SCHEDULE_SUCCESS;

        if (sendSchedule) {
            // Send schedule to thermsotat
            message = thermostatService.sendSchedule(account,
                                                     schedule,
                                                     thermostatMode,
                                                     scheduleTimeOfWeek,
                                                     false,
                                                     userContext);
        }

        map.addAttribute("message", message.toString());
        map.addAttribute("thermostatId", thermostatId);

        return "redirect:/spring/stars/consumer/thermostat/schedule/complete";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/complete", method = RequestMethod.GET)
    public String updateComplete(ModelMap map, int thermostatId, String message) {

        ThermostatScheduleUpdateResult resultMessage = ThermostatScheduleUpdateResult.valueOf(message);
        map.addAttribute("message", resultMessage);

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        map.addAttribute("thermostat", thermostat);

        map.addAttribute("viewUrl", "/spring/stars/consumer/thermostat/schedule/view");
        
        return "consumer/actionComplete.jsp";
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
     * @return JSON object
     */
    private JSONObject getJSONForSchedule(ThermostatSchedule schedule) {

        Map<ThermostatMode, ThermostatSeason> seasonMap = schedule.getSeasonMap();
        if (seasonMap.size() != 2) {
            throw new IllegalArgumentException("There should be 2 seasons in a thermostat schedule");
        }

        JSONObject scheduleObject = new JSONObject();
        for (ThermostatMode mode : seasonMap.keySet()) {

            ThermostatSeason season = seasonMap.get(mode);

            JSONObject seasonObject = this.getJSONForSeason(season);

            scheduleObject.put(mode.toString(), seasonObject);

        }

        return scheduleObject;
    }

    /**
     * Helper method to get a JSON object representation of a thermstat season
     * @param season - Season to get object for
     * @return JSON object
     */
    private JSONObject getJSONForSeason(ThermostatSeason season) {

        JSONObject object = new JSONObject();
        Map<TimeOfWeek, List<ThermostatSeasonEntry>> seasonEntryMap = season.getSeasonEntryMap();
        for (TimeOfWeek timeOfWeek : seasonEntryMap.keySet()) {

            List<ThermostatSeasonEntry> entryList = seasonEntryMap.get(timeOfWeek);

            for (ThermostatSeasonEntry entry : entryList) {
                Date time = entry.getStartDate();
                Integer temperature = entry.getTemperature();

                JSONObject timeTemp = new JSONObject();
                String format = SCHEDULE_TIME_FORMAT.format(time);
                timeTemp.put("time", format);
                timeTemp.put("temp", temperature);

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

                String timeString = jsonObject.getString("time");
                Integer temperature = jsonObject.getInt("temp");

                // Convert celsius temp to fahrenheit if needed
                if (!isFahrenheit) {
                    temperature = (int) CtiUtilities.convertTemperature(temperature,
                                                                        CtiUtilities.CELSIUS_CHARACTER,
                                                                        CtiUtilities.FAHRENHEIT_CHARACTER);
                }

                Date date = null;
                try {
                    date = SCHEDULE_TIME_FORMAT.parse(timeString);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Could not parse " + timeString + " into a valid time.");
                }

                ThermostatSeasonEntry entry = new ThermostatSeasonEntry();
                entry.setStartDate(date);
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
