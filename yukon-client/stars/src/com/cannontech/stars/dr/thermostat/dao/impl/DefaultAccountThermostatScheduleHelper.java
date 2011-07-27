package com.cannontech.stars.dr.thermostat.dao.impl;

import java.util.List;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriod;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.google.common.collect.Lists;

public class DefaultAccountThermostatScheduleHelper {
    
    public AccountThermostatSchedule getDefaultAccountThermostatSchedule(SchedulableThermostatType type) {
        AccountThermostatSchedule ats = new AccountThermostatSchedule();
        ats.setAccountId(0);
        ats.setScheduleName(type.name());
        ats.setThermostatType(type);

        ThermostatScheduleMode defaultModeForType = type.getDefaultThermostatScheduleMode();
        ats.setThermostatScheduleMode(defaultModeForType);
        
        List<AccountThermostatScheduleEntry> atsEntries = Lists.newArrayList();
        for (TimeOfWeek timeOfWeek : type.getDefaultThermostatScheduleMode().getAssociatedTimeOfWeeks()) {
            for (ThermostatSchedulePeriod period : type.getPeriodStyle().getAllPeriods()) {
                AccountThermostatScheduleEntry entry = new AccountThermostatScheduleEntry(period.getDefaultStartTime(), timeOfWeek, Temperature.fromFahrenheit(72), Temperature.fromFahrenheit(72));
                atsEntries.add(entry);
            }
        }
        ats.setScheduleEntries(atsEntries);
        
        return ats;
    }
}
