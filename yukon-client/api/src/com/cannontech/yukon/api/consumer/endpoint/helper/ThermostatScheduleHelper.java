package com.cannontech.yukon.api.consumer.endpoint.helper;

import java.util.List;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.yukon.api.stars.model.SchedulePeriod;
import com.cannontech.yukon.api.stars.model.ThermostatSchedule;
import com.google.common.collect.Lists;

public class ThermostatScheduleHelper {
    
    /**
     * Converts the thermostatSchedules over to accountThermostatSchedule that can be used the with thermostat schedules services 
     */
    public static AccountThermostatSchedule convertToAccountThermostatSchedule(ThermostatSchedule schedule, int accountId) {
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
    private static List<AccountThermostatScheduleEntry> convertSchedulePeriodToAccountThermostatScheduleEntries(ThermostatSchedule thermostatSchedule) {
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
    
    public static Element addThermostatScheduleResultNode(Namespace ns, Element thermostatScheduleResultList, ThermostatSchedule schedule){
        Element result = new Element("thermostatScheduleResult", ns);
        result.setAttribute("scheduleName", schedule.getScheduleName());
        result.setAttribute("thermostatScheduleMode", XmlUtils.toXmlRepresentation(schedule.getThermostatScheduleMode()));
        thermostatScheduleResultList.addContent(result);
        return result;
    }
    
    public static Element addThermostatScheduleResultNode(Namespace ns, Element thermostatScheduleResultList, String scheduleName){
        Element result = new Element("thermostatScheduleResult", ns);
        result.setAttribute("scheduleName", scheduleName);
        thermostatScheduleResultList.addContent(result);
        return result;
    }

}
