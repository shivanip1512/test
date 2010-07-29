package com.cannontech.stars.dr.hardware.model;

import java.util.Set;

import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.google.common.collect.Sets;

public enum SchedulableThermostatType {

	RESIDENTIAL_EXPRESSSTAT(HardwareType.EXPRESSSTAT,
				50, 90,
				50, 90,
				Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN)),
	COMMERCIAL_EXPRESSSTAT(HardwareType.COMMERCIAL_EXPRESSSTAT,
				50, 90,
				50, 90,
				Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN)),
	UTILITY_PRO(HardwareType.UTILITY_PRO,
				50, 99, //yes, you read it right. Cool has higher upper-limit than Heat, and Heat has a lower lower-limit than Cool. Go ask a Honeywell device engineer.
				40, 90,
				Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN, ThermostatScheduleMode.WEEKDAY_WEEKEND)),
	HEAT_PUMP_EXPRESSSTAT(HardwareType.EXPRESSSTAT_HEAT_PUMP,
                50, 90, 
                50, 90,
                Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN)),
    ENERGY_PRO(HardwareType.ENERGYPRO,
                50, 90, 
                50, 90,
                Sets.immutableEnumSet(ThermostatScheduleMode.ALL, ThermostatScheduleMode.WEEKDAY_SAT_SUN, ThermostatScheduleMode.WEEKDAY_WEEKEND)),             
	;
	
	private HardwareType hardwareType;
	int lowerLimitCool;
	int upperLimitCool;
	int lowerLimitHeat;
	int upperLimitHeat;
	private Set<ThermostatScheduleMode> supportedScheduleModes;
	
	SchedulableThermostatType(HardwareType hardwareType,
							  int lowerLimitCool, int upperLimitCool,
							  int lowerLimitHeat, int upperLimitHeat,
							  Set<ThermostatScheduleMode> supportedScheduleModes) {
		
		this.hardwareType = hardwareType;
		this.lowerLimitCool = lowerLimitCool;
		this.upperLimitCool = upperLimitCool;
		this.lowerLimitHeat = lowerLimitHeat;
		this.upperLimitHeat = upperLimitHeat;
		this.supportedScheduleModes = supportedScheduleModes;
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
	
	public int getLowerLimitCool() {
		return lowerLimitCool;
	}
	public int getUpperLimitCool() {
		return upperLimitCool;
	}
	public int getLowerLimitHeat() {
		return lowerLimitHeat;
	}
	public int getUpperLimitHeat() {
		return upperLimitHeat;
	}
	
	public static SchedulableThermostatType getByHardwareType(HardwareType hardwareType) {
		
		for (SchedulableThermostatType schedulableThermostatType : SchedulableThermostatType.values()) {
			if (schedulableThermostatType.getHardwareType() == hardwareType) {
				return schedulableThermostatType;
			}
		}
		
		throw new IllegalArgumentException("Invalid hardwareType: " + hardwareType);
	}
}
