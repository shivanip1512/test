package com.cannontech.yukon.api.consumer.endpoint.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
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
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ThermostatScheduleHelper {
    
    private static final int SCHEDULE_NAME_LENGTH = 60;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
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
    
    public Element addThermostatScheduleResultNode(Namespace ns, Element thermostatScheduleResultList, ThermostatSchedule schedule){
        Element result = new Element("thermostatScheduleResult", ns);
        result.setAttribute("scheduleName", schedule.getScheduleName());
        result.setAttribute("thermostatScheduleMode", XmlUtils.toXmlRepresentation(schedule.getThermostatScheduleMode()));
        thermostatScheduleResultList.addContent(result);
        return result;
    }
    
    public Element addThermostatScheduleResultNode(Namespace ns, Element thermostatScheduleResultList, String scheduleName){
        Element result = new Element("thermostatScheduleResult", ns);
        result.setAttribute("scheduleName", scheduleName);
        thermostatScheduleResultList.addContent(result);
        return result;
    }
    
    /**
     * Validates thermostat schedule, if errors found adds them to thermostatScheduleResultNode.
     * 
     * @return true if no errors found
     */
    public boolean isValidSchedule(int customerId, ThermostatSchedule schedule, Element thermostatScheduleResultNode,
                                   Namespace ns, boolean isUpdate) {
        return isValidThermostatScheduleMode(customerId, schedule, thermostatScheduleResultNode)
               && isValidScheduleName(customerId, schedule, thermostatScheduleResultNode, isUpdate)
               && isValidTimesOfWeek(schedule, thermostatScheduleResultNode)
               && isValidSchedulePeriods(schedule, thermostatScheduleResultNode, ns);

    }

    private boolean isValidThermostatScheduleMode(int customerId, ThermostatSchedule schedule,
                                                  Element thermostatScheduleResultNode) {
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByAccountId(customerId);
        List<Integer> childEnergyCompanyIds =
            yukonEnergyCompanyService.getChildEnergyCompanies(energyCompany.getEnergyCompanyId());
        childEnergyCompanyIds.add(energyCompany.getEnergyCompanyId());

        Set<ThermostatScheduleMode> allowedThermostatScheduleModes =
            thermostatService.getAllowedThermostatScheduleModes(energyCompany);
        if (!allowedThermostatScheduleModes.contains(schedule.getThermostatScheduleMode())) {
            Element fe =
                XMLFailureGenerator.makeSimple("InvalidThermostatScheduleMode",
                                               "Thermostat schedule mode is not allowed.");
            thermostatScheduleResultNode.addContent(fe);
            return false;
        }
        return true;
    }

    private boolean isValidScheduleName(int customerId, ThermostatSchedule schedule,
                                        Element thermostatScheduleResultNode, boolean isUpdate) {
        if (!isUpdate) {
            AccountThermostatSchedule duplicateSchedule =
                accountThermostatScheduleDao.findSchedulesForAccountByScheduleName(customerId, schedule.getScheduleName());
            if (duplicateSchedule != null) {
                Element fe = XMLFailureGenerator.makeSimple("DuplicateScheduleName", "Duplicate schedule name.");
                thermostatScheduleResultNode.addContent(fe);
                return false;
            }
        }
        if (schedule.getScheduleName().length() > SCHEDULE_NAME_LENGTH) {
            Element fe =
                XMLFailureGenerator.makeSimple("InvalidScheduleNameLength", "Schedule Name length cannot exceed "
                                                                            + SCHEDULE_NAME_LENGTH + " characters.");
            thermostatScheduleResultNode.addContent(fe);
            return false;
        }
        return true;
    }

    private boolean isValidTimesOfWeek(ThermostatSchedule schedule, Element thermostatScheduleResultNode) {
        Set<TimeOfWeek> timesOfWeek = schedule.getSchedulePeriodContainer().keySet();
        Set<TimeOfWeek> allowedTimesOfWeek = schedule.getThermostatScheduleMode().getAssociatedTimeOfWeeks();
        if (allowedTimesOfWeek.size() != timesOfWeek.size()) {
            List<String> allowedTimes = Lists.transform(new ArrayList<TimeOfWeek>(allowedTimesOfWeek), new Function<TimeOfWeek, String>() {
                @Override
                public String apply(TimeOfWeek t) {
                    return WordUtils.capitalize(t.getValue().toLowerCase());
                }});
            ;
            Element fe =
                XMLFailureGenerator.makeSimple("InvalidTimesOfWeek",
                                               "Invalid Times of the week entered. Valid times of the week: " + Joiner.on(", ").join(allowedTimes));
            thermostatScheduleResultNode.addContent(fe);
            return false;
        }
        return true;
    }

    private boolean isValidSchedulePeriods(ThermostatSchedule schedule, Element thermostatScheduleResultNode,
                                           Namespace ns) {
        boolean isValidResult = true;
        SchedulableThermostatType thermostatType = schedule.getSchedulableThermostatType();
        Set<TimeOfWeek> timesOfWeek = schedule.getSchedulePeriodContainer().keySet();
        Set<TimeOfWeek> allowedTimesOfWeek = schedule.getThermostatScheduleMode().getAssociatedTimeOfWeeks();
        for (TimeOfWeek timeOfWeek : timesOfWeek) {
            boolean isValid = true;
            Element thermostatSchedulePeriodResult = new Element("thermostatSchedulePeriodResult", ns);
            thermostatSchedulePeriodResult.setAttribute("timeOfWeek", XmlUtils.toXmlRepresentation(timeOfWeek));
            Set<SchedulePeriod> schedulePeriods = schedule.getSchedulePeriodContainer().get(timeOfWeek);
            if (!allowedTimesOfWeek.contains(timeOfWeek)) {
                thermostatSchedulePeriodResult
                    .addContent(XMLFailureGenerator.makeSimple("InvalidTimeOfWeek",
                                                               XmlUtils.toXmlRepresentation(timeOfWeek)
                                                                       + " is invalid time of the week."));
                isValid = false;
            }
            if (schedulePeriods.size() != thermostatType.getPeriodStyle().getAllPeriods().size()) {
                thermostatSchedulePeriodResult.addContent(XMLFailureGenerator
                    .makeSimple("InvalidNumberOfPeriods",
                                "Invalid number of periods for " + XmlUtils.toXmlRepresentation(timeOfWeek))+".");
                isValid = false;
            }

            for (SchedulePeriod schedulePeriod : schedulePeriods) {
                String info = buildScheduleInfo(schedulePeriod, timeOfWeek);
                if (schedulePeriod.getCoolTemperature().compareTo(thermostatType.getLowerLimitCool()) < 0) {
                    thermostatSchedulePeriodResult.addContent(XMLFailureGenerator
                        .makeSimple("InvalidCoolingTemperature",
                                    info + "The cooling temperature " + schedulePeriod.getCoolTemperature()
                                            + " is too low."));
                    isValid = false;
                }
                if (schedulePeriod.getCoolTemperature().compareTo(thermostatType.getUpperLimitCool()) > 0) {
                    thermostatSchedulePeriodResult.addContent(XMLFailureGenerator
                        .makeSimple("InvalidCoolingTemperature", info + "The cooling temperature "
                                                                 + schedulePeriod.getCoolTemperature()
                                                                 + " is too high."));
                    isValid = false;
                }
                if (schedulePeriod.getHeatTemperature().compareTo(thermostatType.getLowerLimitHeat()) < 0) {
                    thermostatSchedulePeriodResult.addContent(XMLFailureGenerator
                        .makeSimple("InvalidHeatingTemperature",
                                    info + "The heating temperature " + schedulePeriod.getHeatTemperature()
                                            + " is too low."));
                    isValid = false;
                }
                if (schedulePeriod.getHeatTemperature().compareTo(thermostatType.getUpperLimitHeat()) > 0) {
                    thermostatSchedulePeriodResult.addContent(XMLFailureGenerator
                        .makeSimple("InvalidHeatingTemperature", info + "The heating temperature "
                                                                 + schedulePeriod.getHeatTemperature()
                                                                 + "is too high."));
                    isValid = false;
                }
            }
            if (!isValid) {
                thermostatScheduleResultNode.addContent(thermostatSchedulePeriodResult);
                isValidResult = false;
            }
        }
        return isValidResult;
    }

    private String buildScheduleInfo(SchedulePeriod schedulePeriod, TimeOfWeek timeOfWeek) {
        StringBuilder info = new StringBuilder();
        info.append("[");
        info.append(XmlUtils.toXmlRepresentation(timeOfWeek));
        info.append(":");
        info.append(YukonXPathTemplate.PERIOD_START_TIME_FORMATTER.print(schedulePeriod.getPeriodStartTime()));
        info.append("] ");
        return info.toString();
    }
}
