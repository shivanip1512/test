package com.cannontech.stars.dr.hardware.model;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalTime;

import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum SchedulableThermostatType {

	RESIDENTIAL_EXPRESSSTAT(HardwareType.EXPRESSSTAT,
				50, 90,
				50, 90,
				Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN),
				ThermostatScheduleMode.WEEKDAY_SAT_SUN
	),
	COMMERCIAL_EXPRESSSTAT(HardwareType.COMMERCIAL_EXPRESSSTAT,
				50, 90,
				50, 90,
				Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN),
				ThermostatScheduleMode.WEEKDAY_SAT_SUN
	),
	UTILITY_PRO(HardwareType.UTILITY_PRO,
				50, 99, //yes, you read it right. Cool has higher upper-limit than Heat, and Heat has a lower lower-limit than Cool. Go ask a Honeywell device engineer.
				40, 90,
				Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN, ThermostatScheduleMode.WEEKDAY_WEEKEND),
				ThermostatScheduleMode.WEEKDAY_SAT_SUN
	),
	HEAT_PUMP_EXPRESSSTAT(HardwareType.EXPRESSSTAT_HEAT_PUMP,
                50, 90, 
                50, 90,
                Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN),
                ThermostatScheduleMode.WEEKDAY_SAT_SUN
	),
    ENERGY_PRO(HardwareType.ENERGYPRO,
                50, 90, 
                50, 90,
                Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN, ThermostatScheduleMode.WEEKDAY_WEEKEND),
                ThermostatScheduleMode.WEEKDAY_SAT_SUN
    ),             
	;
	
	private HardwareType hardwareType;
	private int lowerLimitCoolInFahrenheit;
	private int upperLimitCoolInFahrenheit;
	private int lowerLimitHeatInFahrenheit;
	private int upperLimitHeatInFahrenheit;
	private Set<ThermostatScheduleMode> supportedScheduleModes;
	private ThermostatScheduleMode defaultThermostatScheduleMode;
	
	SchedulableThermostatType(HardwareType hardwareType,
							  int lowerLimitCoolInFahrenheit, int upperLimitCoolInFahrenheit,
							  int lowerLimitHeatInFahrenheit, int upperLimitHeatInFahrenheit,
							  Set<ThermostatScheduleMode> supportedScheduleModes,
							  ThermostatScheduleMode defaultThermostatScheduleMode) {
		
		this.hardwareType = hardwareType;
		this.lowerLimitCoolInFahrenheit = lowerLimitCoolInFahrenheit;
		this.upperLimitCoolInFahrenheit = upperLimitCoolInFahrenheit;
		this.lowerLimitHeatInFahrenheit = lowerLimitHeatInFahrenheit;
		this.upperLimitHeatInFahrenheit = upperLimitHeatInFahrenheit;
		this.supportedScheduleModes = supportedScheduleModes;
		this.defaultThermostatScheduleMode = defaultThermostatScheduleMode;
	}
	
	public HardwareType getHardwareType() {
		return hardwareType;
	}
	
	/**
	 * Modes that this thermostat type supports.
	 * Note: Utility Pro <i>supports</i> WEEKDAY_WEEKEND, however there is a role property that is used to determine if the option is made available in the UI (ConsumerInfoRole.THERMOSTAT_SCHEDULE_5_2).
	 */
	public Set<ThermostatScheduleMode> getSupportedScheduleModes() {
		return supportedScheduleModes;
	}
	
	public int getLowerLimitCoolInFahrenheit() {
		return lowerLimitCoolInFahrenheit;
	}
	public int getUpperLimitCoolInFahrenheit() {
		return upperLimitCoolInFahrenheit;
	}
	public int getLowerLimitHeatInFahrenheit() {
		return lowerLimitHeatInFahrenheit;
	}
	public int getUpperLimitHeatInFahrenheit() {
		return upperLimitHeatInFahrenheit;
	}
	
	public static SchedulableThermostatType getByHardwareType(HardwareType hardwareType) {
		
		for (SchedulableThermostatType schedulableThermostatType : SchedulableThermostatType.values()) {
			if (schedulableThermostatType.getHardwareType() == hardwareType) {
				return schedulableThermostatType;
			}
		}
		
		throw new IllegalArgumentException("Invalid hardwareType: " + hardwareType);
	}
	
	public AccountThermostatSchedule getDefaultAccountThermostatSchedule(){
	    AccountThermostatSchedule ats = new AccountThermostatSchedule();
	    ats.setAccountId(0);
	    ats.setScheduleName(this.name());
	    ats.setThermostatType(this);

	    ThermostatScheduleMode defaultModeForType = defaultThermostatScheduleMode;
	    ats.setThermostatScheduleMode(defaultModeForType);

	    //set up the schedule entries based on mode
	    List<AccountThermostatScheduleEntry> atsEntries = Lists.newArrayList();
	    if(defaultModeForType == ThermostatScheduleMode.WEEKDAY_SAT_SUN){
	        AccountThermostatScheduleEntry weekday1 = new AccountThermostatScheduleEntry(new LocalTime(6), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry weekday2 = new AccountThermostatScheduleEntry(new LocalTime(8,30), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry weekday3 = new AccountThermostatScheduleEntry(new LocalTime(17), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry weekday4 = new AccountThermostatScheduleEntry(new LocalTime(21), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry sat1 = new AccountThermostatScheduleEntry(new LocalTime(6), TimeOfWeek.SATURDAY, 72, 72);
	        AccountThermostatScheduleEntry sat2 = new AccountThermostatScheduleEntry(new LocalTime(8,30), TimeOfWeek.SATURDAY, 72, 72);
	        AccountThermostatScheduleEntry sat3 = new AccountThermostatScheduleEntry(new LocalTime(17), TimeOfWeek.SATURDAY, 72, 72);
	        AccountThermostatScheduleEntry sat4 = new AccountThermostatScheduleEntry(new LocalTime(21), TimeOfWeek.SATURDAY, 72, 72);
	        AccountThermostatScheduleEntry sun1 = new AccountThermostatScheduleEntry(new LocalTime(6), TimeOfWeek.SUNDAY, 72, 72);
	        AccountThermostatScheduleEntry sun2 = new AccountThermostatScheduleEntry(new LocalTime(8,30), TimeOfWeek.SUNDAY, 72, 72);
	        AccountThermostatScheduleEntry sun3 = new AccountThermostatScheduleEntry(new LocalTime(17), TimeOfWeek.SUNDAY, 72, 72);
	        AccountThermostatScheduleEntry sun4 = new AccountThermostatScheduleEntry(new LocalTime(21), TimeOfWeek.SUNDAY, 72, 72);
	        atsEntries.add(weekday1);
	        atsEntries.add(weekday2);
	        atsEntries.add(weekday3);
	        atsEntries.add(weekday4);
	        atsEntries.add(sat1);
	        atsEntries.add(sat2);
	        atsEntries.add(sat3);
	        atsEntries.add(sat4);
	        atsEntries.add(sun1);
	        atsEntries.add(sun2);
	        atsEntries.add(sun3);
	        atsEntries.add(sun4);
	    } else if(defaultModeForType == ThermostatScheduleMode.WEEKDAY_WEEKEND){
	        AccountThermostatScheduleEntry weekday1 = new AccountThermostatScheduleEntry(new LocalTime(6), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry weekday2 = new AccountThermostatScheduleEntry(new LocalTime(8,30), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry weekday3 = new AccountThermostatScheduleEntry(new LocalTime(17), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry weekday4 = new AccountThermostatScheduleEntry(new LocalTime(21), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry weekend1 = new AccountThermostatScheduleEntry(new LocalTime(6), TimeOfWeek.SATURDAY, 72, 72);
	        AccountThermostatScheduleEntry weekend2 = new AccountThermostatScheduleEntry(new LocalTime(8,30), TimeOfWeek.SATURDAY, 72, 72);
	        AccountThermostatScheduleEntry weekend3 = new AccountThermostatScheduleEntry(new LocalTime(17), TimeOfWeek.SATURDAY, 72, 72);
	        AccountThermostatScheduleEntry weekend4 = new AccountThermostatScheduleEntry(new LocalTime(21), TimeOfWeek.SATURDAY, 72, 72);
	        atsEntries.add(weekday1);
	        atsEntries.add(weekday2);
	        atsEntries.add(weekday3);
	        atsEntries.add(weekday4);
	        atsEntries.add(weekend1);
	        atsEntries.add(weekend2);
	        atsEntries.add(weekend3);
	        atsEntries.add(weekend4);
	    } else if(defaultModeForType == ThermostatScheduleMode.ALL){
	        AccountThermostatScheduleEntry all1 = new AccountThermostatScheduleEntry(new LocalTime(6), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry all2 = new AccountThermostatScheduleEntry(new LocalTime(8,30), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry all3 = new AccountThermostatScheduleEntry(new LocalTime(17), TimeOfWeek.WEEKDAY, 72, 72);
	        AccountThermostatScheduleEntry all4 = new AccountThermostatScheduleEntry(new LocalTime(21), TimeOfWeek.WEEKDAY, 72, 72);
	        atsEntries.add(all1);
	        atsEntries.add(all2);
	        atsEntries.add(all3);
	        atsEntries.add(all4);
	    } else {
	        throw new IllegalArgumentException("No default schedule for mode: " + defaultModeForType);
	    }

	    ats.setScheduleEntries(atsEntries);
	    return ats;
	}

}
