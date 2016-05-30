package com.cannontech.yukon.api.consumer.endpoint.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.stars.model.SchedulePeriod;
import com.cannontech.yukon.api.stars.model.ThermostatSchedule;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ThermostatScheduleHelper {
    
    private static final int SCHEDULE_NAME_LENGTH = 60;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ThermostatService thermostatService;
    
    /**
     * Converts the thermostatSchedules over to accountThermostatSchedule that can be used the with thermostat schedules services 
     */
    public AccountThermostatSchedule convertToAccountThermostatSchedule(ThermostatSchedule schedule, int accountId) {
        AccountThermostatSchedule accountThermostatSchedule = new AccountThermostatSchedule();
        accountThermostatSchedule.setScheduleName(schedule.getScheduleName());
        accountThermostatSchedule.setThermostatScheduleMode(schedule.getThermostatScheduleMode());
        
        accountThermostatSchedule.setAccountId(accountId);
        accountThermostatSchedule.setThermostatType(schedule.getSchedulableThermostatType());

        // Getting the thermostat type 
        List<AccountThermostatScheduleEntry> accountThermostatScheduleEntrys = convertSchedulePeriodToAccountThermostatScheduleEntries(schedule);
        accountThermostatSchedule.setScheduleEntries(accountThermostatScheduleEntrys);
        
        return accountThermostatSchedule;
    }

    /**
     * Converts the thermostatSchedules over to accountThermostatScheduleEntries that can be used the with thermostat schedules services 
     */
    private List<AccountThermostatScheduleEntry> convertSchedulePeriodToAccountThermostatScheduleEntries(ThermostatSchedule thermostatSchedule) {
        List<AccountThermostatScheduleEntry> accountThermostatScheduleEntrys = Lists.newArrayList();

        for (Entry<TimeOfWeek, SchedulePeriod> timeOfWeekToSchedulePeriodEntry : thermostatSchedule.getSchedulePeriodContainer().entries()) {
            TimeOfWeek timeOfWeek = timeOfWeekToSchedulePeriodEntry.getKey();
            SchedulePeriod schedulePeriod = timeOfWeekToSchedulePeriodEntry.getValue();
            AccountThermostatScheduleEntry accountThermostatScheduleEntry = new AccountThermostatScheduleEntry();
            accountThermostatScheduleEntry.setTimeOfWeek(timeOfWeek);
            accountThermostatScheduleEntry.setStartTime(schedulePeriod.getPeriodStartTime());
            accountThermostatScheduleEntry.setCoolTemp(schedulePeriod.getCoolTemperature());
            accountThermostatScheduleEntry.setHeatTemp(schedulePeriod.getHeatTemperature());
            
            accountThermostatScheduleEntrys.add(accountThermostatScheduleEntry);
        }
        
        return accountThermostatScheduleEntrys;
    }
    
    public Element addThermostatScheduleResultNode(Namespace ns,  ThermostatSchedule schedule){
        Element thermostatScheduleResult = new Element("thermostatScheduleResult", ns);
        thermostatScheduleResult.setAttribute("scheduleName", schedule.getScheduleName());
        thermostatScheduleResult.setAttribute("thermostatScheduleMode", XmlUtils.toXmlRepresentation(schedule.getThermostatScheduleMode()));
        return thermostatScheduleResult;
    }
    
    public Element addThermostatScheduleResultNode(Namespace ns, String scheduleName){
        Element thermostatScheduleResult = new Element("thermostatScheduleResult", ns);
        thermostatScheduleResult.setAttribute("scheduleName", scheduleName);
        return thermostatScheduleResult;
    }
    
    /**
     * Validates thermostat schedule, if errors found adds them to errors.
     * 
     * @return true if no errors found
     */
    public boolean validateSchedule(int customerId, ThermostatSchedule schedule, Element errors,
                                   Namespace ns, boolean isUpdate) {
        return validateThermostatScheduleMode(customerId, schedule, errors, ns)
               & validateValidScheduleName(customerId, schedule, isUpdate, errors, ns)
               & validateTimesOfWeek(schedule, errors, ns)
               & validateSchedulePeriods(schedule, errors, ns);
    }

    /**
     * Validates thermostat schedule mode, if errors found adds them to errors.
     * 
     * @return true if no errors found
     */
    private boolean validateThermostatScheduleMode(int customerId, ThermostatSchedule schedule,
                                                  Element errors, Namespace ns) {
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByAccountId(customerId);
        Set<ThermostatScheduleMode> allowedThermostatScheduleModes =
            thermostatService.getAllowedThermostatScheduleModes(energyCompany);
        if (!allowedThermostatScheduleModes.contains(schedule.getThermostatScheduleMode())) {
            addScheduleError("Thermostat schedule mode is not allowed.", errors, ns);
            return false;
        }
        return true;
    }
    
    /**
     * Validates schedule name, if errors found adds them to errors.
     * 
     * @return true if no errors found
     */
    private boolean validateValidScheduleName(int customerId, ThermostatSchedule schedule, boolean isUpdate, Element errors, Namespace ns) {
        if (!isUpdate) {
            AccountThermostatSchedule duplicateSchedule =
                accountThermostatScheduleDao.findSchedulesForAccountByScheduleName(customerId, schedule.getScheduleName());
            if (duplicateSchedule != null) {
                addScheduleError("Duplicate schedule name.", errors, ns);
                return false;
            }
        }
        if (schedule.getScheduleName().length() > SCHEDULE_NAME_LENGTH) {
            addScheduleError("Schedule Name length cannot exceed " + SCHEDULE_NAME_LENGTH + " characters.", errors, ns);
            return false;
        }
        return true;
    }

    /**
     * Validates times of the week, if errors found adds them to errors.
     * 
     * @return true if no errors found
     */
    private boolean validateTimesOfWeek(ThermostatSchedule schedule, Element errors, Namespace ns) {
        Set<TimeOfWeek> timesOfWeek = Sets.newHashSet(schedule.getSchedulePeriodContainer().keySet());
        Set<TimeOfWeek> allowedTimesOfWeek = schedule.getThermostatScheduleMode().getAssociatedTimeOfWeeks();
        for(TimeOfWeek tow: allowedTimesOfWeek ){
            timesOfWeek.remove(tow);
        }
        if (!timesOfWeek.isEmpty() || schedule.getSchedulePeriodContainer().keySet().size() != allowedTimesOfWeek.size()) {
            List<String> allowedTimes = Lists.transform(new ArrayList<TimeOfWeek>(allowedTimesOfWeek), new Function<TimeOfWeek, String>() {
                @Override
                public String apply(TimeOfWeek t) {
                    return WordUtils.capitalize(t.getValue().toLowerCase());
                }});
            ;
            addScheduleError("Invalid times of the week entered. Valid times of the week: "
                                     + Joiner.on(", ").join(allowedTimes), errors, ns);
            return false;
        }
        return true;
    }

    /**
     * Validates schedule periods, if errors found adds them to errors.
     * 
     * @return true if no errors found
     */
    private boolean validateSchedulePeriods(ThermostatSchedule schedule, Element errors, Namespace ns) {
        boolean isValidResult = true;
        SchedulableThermostatType thermostatType = schedule.getSchedulableThermostatType();
        Set<TimeOfWeek> timesOfWeek = schedule.getSchedulePeriodContainer().keySet();
        for (TimeOfWeek timeOfWeek : timesOfWeek) {
            Element periodError = new Element("periodError", ns);
            periodError.setAttribute("timeOfWeek", XmlUtils.toXmlRepresentation(timeOfWeek));
            Element thermostatSchedulePeriodResult = new Element("thermostatSchedulePeriodResult", ns);
            thermostatSchedulePeriodResult.setAttribute("timeOfWeek", XmlUtils.toXmlRepresentation(timeOfWeek));
            Set<SchedulePeriod> schedulePeriods = schedule.getSchedulePeriodContainer().get(timeOfWeek);
            if (schedulePeriods.size() != thermostatType.getPeriodStyle().getAllPeriods().size()) {
                addPeriodError(periodError,
                               "Invalid number of periods for " + XmlUtils.toXmlRepresentation(timeOfWeek) + ".", ns);
            }

            Map<LocalTime, Integer> times = new HashMap<>();
            
            for (SchedulePeriod schedulePeriod : schedulePeriods) {
                //schedule with two settings for the exact same start time is invalid
                //count start times for each schedule period
                Integer value = times.get(schedulePeriod.getPeriodStartTime());
                if (value == null) {
                    value = new Integer(0);
                }
                times.put(schedulePeriod.getPeriodStartTime(), ++value);
                
                String info = buildTimeInfo(schedulePeriod);
                if (schedulePeriod.getCoolTemperature().compareTo(thermostatType.getLowerLimitCool()) < 0) {
                    addPeriodError(periodError,
                                   info + "The cooling temperature " + schedulePeriod.getCoolTemperature()
                                           + " is too low.", ns);
                }
                if (schedulePeriod.getCoolTemperature().compareTo(thermostatType.getUpperLimitCool()) > 0) {
                    addPeriodError(periodError,
                                   info + "The cooling temperature " + schedulePeriod.getCoolTemperature()
                                           + " is too high.", ns);
                }
                if (schedulePeriod.getHeatTemperature().compareTo(thermostatType.getLowerLimitHeat()) < 0) {
                    addPeriodError(periodError,
                                   info + "The heating temperature " + schedulePeriod.getHeatTemperature()
                                           + " is too low.", ns);
                }
                if (schedulePeriod.getHeatTemperature().compareTo(thermostatType.getUpperLimitHeat()) > 0) {
                    addPeriodError(periodError,
                                   info + "The heating temperature " + schedulePeriod.getHeatTemperature()
                                           + "is too high.", ns);
                }
            }   
            //remove all start times that were used only once
            times.values().removeAll(Collections.singleton(1));
            //create an error for the start times that were used more then once
            for (LocalTime time : times.keySet()) {
                addPeriodError(periodError,
                               "Multiple settings for the exact same time ("
                                       + time.toString(DateTimeFormat.forPattern("H:mm")) + ") are not allowed",
                               ns);
            }
            if (!periodError.getChildren().isEmpty()) {
                errors.addContent(periodError);
                isValidResult = false;
            }
        }
        return isValidResult;
    }

    /**
     * Builds start time info string.
     * 
     */
    private String buildTimeInfo(SchedulePeriod schedulePeriod) {
        StringBuilder info = new StringBuilder();
        info.append("[Start Time:");
        info.append(YukonXPathTemplate.PERIOD_START_TIME_FORMATTER.print(schedulePeriod.getPeriodStartTime()));
        info.append("] ");
        return info.toString();
    }
    
    /**
     * Adds schedule error
     * 
     */
    public void addScheduleError(String message, Element errors, Namespace ns){
        Element error = new Element("generalError", ns);
        error.setAttribute("message", message);
        errors.addContent(error); 
    }
    
    /**
     * Adds period error
     * 
     */
    private void addPeriodError(Element periodError, String message, Namespace ns){
        Element error = new Element("error", ns);
        error.setAttribute("message", message);
        periodError.addContent(error); 
    }
}
