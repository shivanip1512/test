package com.cannontech.stars.dr.thermostat.dao.impl;

import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalTime;
import org.junit.Test;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.google.common.collect.ListMultimap;

public class DefaultAccountThermostatScheduleHelperTest {
    private DefaultAccountThermostatScheduleHelper datsHelper = new DefaultAccountThermostatScheduleHelper();
    
    @Test
    public void testResidentialExpressstatDefaultSchedule(){
        AccountThermostatSchedule schedule = datsHelper.getDefaultAccountThermostatSchedule(SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT);
        
        //check values for residential expressstat schedule
        Assert.assertEquals(0, schedule.getAccountId());
        Assert.assertEquals(SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT.name(), schedule.getScheduleName());
        Assert.assertEquals(SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT, schedule.getThermostatType());
        Assert.assertEquals(SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT.getDefaultThermostatScheduleMode(), schedule.getThermostatScheduleMode());
        
        
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> scheduleEntryMultimap = schedule.getEntriesByTimeOfWeekMultimap();

        //check values for WEEKDAY entries
        List<AccountThermostatScheduleEntry> weekdayscheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.WEEKDAY);
        
        AccountThermostatScheduleEntry wkEntry0 = weekdayscheduleEntryList.get(0);
        Assert.assertEquals(72, wkEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), wkEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry1 = weekdayscheduleEntryList.get(1);
        Assert.assertEquals(72, wkEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), wkEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry2 = weekdayscheduleEntryList.get(2);
        Assert.assertEquals(72, wkEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), wkEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry3 = weekdayscheduleEntryList.get(3);
        Assert.assertEquals(72, wkEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), wkEntry3.getStartTimeLocalTime());
        
        //Check values for SATURDAY entries
        List<AccountThermostatScheduleEntry> saturdayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SATURDAY);
        
        AccountThermostatScheduleEntry satEntry0 = saturdayScheduleEntryList.get(0);
        Assert.assertEquals(72, satEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), satEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry1 = saturdayScheduleEntryList.get(1);
        Assert.assertEquals(72, satEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), satEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry2 = saturdayScheduleEntryList.get(2);
        Assert.assertEquals(72, satEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), satEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry3 = saturdayScheduleEntryList.get(3);
        Assert.assertEquals(72, satEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), satEntry3.getStartTimeLocalTime());
        
        //Check values for SUNDAY entries
        List<AccountThermostatScheduleEntry> sundayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SUNDAY);
        
        AccountThermostatScheduleEntry sunEntry0 = sundayScheduleEntryList.get(0);
        Assert.assertEquals(72, sunEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), sunEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry1 = sundayScheduleEntryList.get(1);
        Assert.assertEquals(72, sunEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), sunEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry2 = sundayScheduleEntryList.get(2);
        Assert.assertEquals(72, sunEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), sunEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry3 = sundayScheduleEntryList.get(3);
        Assert.assertEquals(72, sunEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), sunEntry3.getStartTimeLocalTime());
    }
    
    @Test
    public void testCommercialExpressstatDefaultSchedule(){
        AccountThermostatSchedule schedule = datsHelper.getDefaultAccountThermostatSchedule(SchedulableThermostatType.COMMERCIAL_EXPRESSSTAT);
        
        //check values for residential expressstat schedule
        Assert.assertEquals(0, schedule.getAccountId());
        Assert.assertEquals(SchedulableThermostatType.COMMERCIAL_EXPRESSSTAT.name(), schedule.getScheduleName());
        Assert.assertEquals(SchedulableThermostatType.COMMERCIAL_EXPRESSSTAT, schedule.getThermostatType());
        Assert.assertEquals(SchedulableThermostatType.COMMERCIAL_EXPRESSSTAT.getDefaultThermostatScheduleMode(), schedule.getThermostatScheduleMode());
        
        
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> scheduleEntryMultimap = schedule.getEntriesByTimeOfWeekMultimap();

        //check values for WEEKDAY entries
        List<AccountThermostatScheduleEntry> weekdayscheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.WEEKDAY);
        
        AccountThermostatScheduleEntry wkEntry0 = weekdayscheduleEntryList.get(0);
        Assert.assertEquals(72, wkEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(1,0), wkEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry1 = weekdayscheduleEntryList.get(1);
        Assert.assertEquals(72, wkEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(2,0), wkEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry2 = weekdayscheduleEntryList.get(2);
        Assert.assertEquals(72, wkEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,0), wkEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry3 = weekdayscheduleEntryList.get(3);
        Assert.assertEquals(72, wkEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), wkEntry3.getStartTimeLocalTime());
        
        //Check values for SATURDAY entries
        List<AccountThermostatScheduleEntry> saturdayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SATURDAY);
        
        AccountThermostatScheduleEntry satEntry0 = saturdayScheduleEntryList.get(0);
        Assert.assertEquals(72, satEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(1,0), satEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry1 = saturdayScheduleEntryList.get(1);
        Assert.assertEquals(72, satEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(2,0), satEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry2 = saturdayScheduleEntryList.get(2);
        Assert.assertEquals(72, satEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,0), satEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry3 = saturdayScheduleEntryList.get(3);
        Assert.assertEquals(72, satEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), satEntry3.getStartTimeLocalTime());
        
        //Check values for SUNDAY entries
        List<AccountThermostatScheduleEntry> sundayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SUNDAY);
        
        AccountThermostatScheduleEntry sunEntry0 = sundayScheduleEntryList.get(0);
        Assert.assertEquals(72, sunEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(1,0), sunEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry1 = sundayScheduleEntryList.get(1);
        Assert.assertEquals(72, sunEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(2,0), sunEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry2 = sundayScheduleEntryList.get(2);
        Assert.assertEquals(72, sunEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,0), sunEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry3 = sundayScheduleEntryList.get(3);
        Assert.assertEquals(72, sunEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), sunEntry3.getStartTimeLocalTime());
    }    
        
    @Test
    public void testHeatPumpExpressstatDefaultSchedule(){
        AccountThermostatSchedule schedule = datsHelper.getDefaultAccountThermostatSchedule(SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT);
        
        //check values for residential expressstat schedule
        Assert.assertEquals(0, schedule.getAccountId());
        Assert.assertEquals(SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT.name(), schedule.getScheduleName());
        Assert.assertEquals(SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT, schedule.getThermostatType());
        Assert.assertEquals(SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT.getDefaultThermostatScheduleMode(), schedule.getThermostatScheduleMode());
        
        
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> scheduleEntryMultimap = schedule.getEntriesByTimeOfWeekMultimap();

        //check values for WEEKDAY entries
        List<AccountThermostatScheduleEntry> weekdayscheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.WEEKDAY);
        
        AccountThermostatScheduleEntry wkEntry0 = weekdayscheduleEntryList.get(0);
        Assert.assertEquals(72, wkEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), wkEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry1 = weekdayscheduleEntryList.get(1);
        Assert.assertEquals(72, wkEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), wkEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry2 = weekdayscheduleEntryList.get(2);
        Assert.assertEquals(72, wkEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), wkEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry3 = weekdayscheduleEntryList.get(3);
        Assert.assertEquals(72, wkEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), wkEntry3.getStartTimeLocalTime());
        
        //Check values for SATURDAY entries
        List<AccountThermostatScheduleEntry> saturdayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SATURDAY);
        
        AccountThermostatScheduleEntry satEntry0 = saturdayScheduleEntryList.get(0);
        Assert.assertEquals(72, satEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), satEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry1 = saturdayScheduleEntryList.get(1);
        Assert.assertEquals(72, satEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), satEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry2 = saturdayScheduleEntryList.get(2);
        Assert.assertEquals(72, satEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), satEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry3 = saturdayScheduleEntryList.get(3);
        Assert.assertEquals(72, satEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), satEntry3.getStartTimeLocalTime());
        
        //Check values for SUNDAY entries
        List<AccountThermostatScheduleEntry> sundayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SUNDAY);
        
        AccountThermostatScheduleEntry sunEntry0 = sundayScheduleEntryList.get(0);
        Assert.assertEquals(72, sunEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), sunEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry1 = sundayScheduleEntryList.get(1);
        Assert.assertEquals(72, sunEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), sunEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry2 = sundayScheduleEntryList.get(2);
        Assert.assertEquals(72, sunEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), sunEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry3 = sundayScheduleEntryList.get(3);
        Assert.assertEquals(72, sunEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), sunEntry3.getStartTimeLocalTime());
    }
        
    @Test
    public void testUtilityProDefaultSchedule(){
        AccountThermostatSchedule schedule = datsHelper.getDefaultAccountThermostatSchedule(SchedulableThermostatType.UTILITY_PRO);
        
        //check values for residential expressstat schedule
        Assert.assertEquals(0, schedule.getAccountId());
        Assert.assertEquals(SchedulableThermostatType.UTILITY_PRO.name(), schedule.getScheduleName());
        Assert.assertEquals(SchedulableThermostatType.UTILITY_PRO, schedule.getThermostatType());
        Assert.assertEquals(SchedulableThermostatType.UTILITY_PRO.getDefaultThermostatScheduleMode(), schedule.getThermostatScheduleMode());
        
        
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> scheduleEntryMultimap = schedule.getEntriesByTimeOfWeekMultimap();

        //check values for WEEKDAY entries
        List<AccountThermostatScheduleEntry> weekdayscheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.WEEKDAY);
        
        AccountThermostatScheduleEntry wkEntry0 = weekdayscheduleEntryList.get(0);
        Assert.assertEquals(72, wkEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), wkEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry1 = weekdayscheduleEntryList.get(1);
        Assert.assertEquals(72, wkEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), wkEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry2 = weekdayscheduleEntryList.get(2);
        Assert.assertEquals(72, wkEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), wkEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry wkEntry3 = weekdayscheduleEntryList.get(3);
        Assert.assertEquals(72, wkEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, wkEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), wkEntry3.getStartTimeLocalTime());
        
        //Check values for SATURDAY entries
        List<AccountThermostatScheduleEntry> saturdayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SATURDAY);
        
        AccountThermostatScheduleEntry satEntry0 = saturdayScheduleEntryList.get(0);
        Assert.assertEquals(72, satEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), satEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry1 = saturdayScheduleEntryList.get(1);
        Assert.assertEquals(72, satEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), satEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry2 = saturdayScheduleEntryList.get(2);
        Assert.assertEquals(72, satEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), satEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry satEntry3 = saturdayScheduleEntryList.get(3);
        Assert.assertEquals(72, satEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, satEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), satEntry3.getStartTimeLocalTime());
        
        //Check values for SUNDAY entries
        List<AccountThermostatScheduleEntry> sundayScheduleEntryList = scheduleEntryMultimap.get(TimeOfWeek.SUNDAY);
        
        AccountThermostatScheduleEntry sunEntry0 = sundayScheduleEntryList.get(0);
        Assert.assertEquals(72, sunEntry0.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry0.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(6,0), sunEntry0.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry1 = sundayScheduleEntryList.get(1);
        Assert.assertEquals(72, sunEntry1.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry1.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(8,30), sunEntry1.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry2 = sundayScheduleEntryList.get(2);
        Assert.assertEquals(72, sunEntry2.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry2.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(17,0), sunEntry2.getStartTimeLocalTime());
        
        AccountThermostatScheduleEntry sunEntry3 = sundayScheduleEntryList.get(3);
        Assert.assertEquals(72, sunEntry3.getCoolTemp().getIntValue());
        Assert.assertEquals(72, sunEntry3.getHeatTemp().getIntValue());
        Assert.assertEquals(new LocalTime(21,0), sunEntry3.getStartTimeLocalTime());
    }    
}