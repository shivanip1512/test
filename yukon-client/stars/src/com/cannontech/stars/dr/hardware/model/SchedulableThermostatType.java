package com.cannontech.stars.dr.hardware.model;

import java.util.Set;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.google.common.collect.Sets;

public enum SchedulableThermostatType {

	RESIDENTIAL_EXPRESSSTAT(HardwareType.EXPRESSSTAT,
                            45, 88,
                            45, 88,
                            Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                                  ThermostatScheduleMode.ALL),
                            ThermostatSchedulePeriodStyle.FOUR_TIMES
	),
	HEAT_PUMP_EXPRESSSTAT(HardwareType.EXPRESSSTAT_HEAT_PUMP,
	                      45, 88, 
	                      45, 88,
	                      Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
	                                            ThermostatScheduleMode.ALL),
	                      ThermostatSchedulePeriodStyle.FOUR_TIMES
	),
    COMMERCIAL_EXPRESSSTAT(HardwareType.COMMERCIAL_EXPRESSSTAT,
                           45, 88,
                           45, 88,
                           Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                                 ThermostatScheduleMode.ALL),
                           ThermostatSchedulePeriodStyle.TWO_TIMES
    ),
	UTILITY_PRO(HardwareType.UTILITY_PRO,
				50, 99, //yes, you read it right. Cool has higher upper-limit than Heat, and Heat has a lower lower-limit than Cool. Go ask a Honeywell device engineer.
				40, 90,
				Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
				                      ThermostatScheduleMode.ALL, 
				                      ThermostatScheduleMode.WEEKDAY_WEEKEND),
                ThermostatSchedulePeriodStyle.FOUR_TIMES
	),
	UTILITY_PRO_G2(HardwareType.UTILITY_PRO_G2,
                   50, 99,
                   40, 90,
                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN,
                                         ThermostatScheduleMode.ALL,
                                         ThermostatScheduleMode.WEEKDAY_WEEKEND,
                                         ThermostatScheduleMode.SEVEN_DAY),
                   ThermostatSchedulePeriodStyle.FOUR_TIMES
    ),
    UTILITY_PRO_G3(HardwareType.UTILITY_PRO_G3,
                   50, 99,
                   40, 90,
                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                         ThermostatScheduleMode.ALL, 
                                         ThermostatScheduleMode.WEEKDAY_WEEKEND, 
                                         ThermostatScheduleMode.SEVEN_DAY),
                   ThermostatSchedulePeriodStyle.FOUR_TIMES
    ),
	UTILITY_PRO_ZIGBEE(HardwareType.UTILITY_PRO_ZIGBEE,
	                   50, 99,
	                   40, 90,
	                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
	                                         ThermostatScheduleMode.ALL, 
	                                         ThermostatScheduleMode.WEEKDAY_WEEKEND, 
	                                         ThermostatScheduleMode.SEVEN_DAY),
	                   ThermostatSchedulePeriodStyle.FOUR_TIMES
	),
	;
	
	private HardwareType hardwareType;
	private FahrenheitTemperature lowerLimitCoolInFahrenheit;
	private FahrenheitTemperature upperLimitCoolInFahrenheit;
	private FahrenheitTemperature lowerLimitHeatInFahrenheit;
	private FahrenheitTemperature upperLimitHeatInFahrenheit;
	private Set<ThermostatScheduleMode> supportedScheduleModes;
	private final ThermostatSchedulePeriodStyle periodStyle;
	
	SchedulableThermostatType(HardwareType hardwareType,
							  int lowerLimitCoolInFahrenheit, int upperLimitCoolInFahrenheit,
							  int lowerLimitHeatInFahrenheit, int upperLimitHeatInFahrenheit,
							  Set<ThermostatScheduleMode> supportedScheduleModes,
							  ThermostatSchedulePeriodStyle periodStyle) {
		
		this.hardwareType = hardwareType;
		this.lowerLimitCoolInFahrenheit = new FahrenheitTemperature(lowerLimitCoolInFahrenheit);
		this.upperLimitCoolInFahrenheit = new FahrenheitTemperature(upperLimitCoolInFahrenheit);
		this.lowerLimitHeatInFahrenheit = new FahrenheitTemperature(lowerLimitHeatInFahrenheit);
		this.upperLimitHeatInFahrenheit = new FahrenheitTemperature(upperLimitHeatInFahrenheit);
		this.supportedScheduleModes = supportedScheduleModes;
		this.periodStyle = periodStyle;
	}
	
	public HardwareType getHardwareType() {
		return hardwareType;
	}
	
	public ThermostatSchedulePeriodStyle getPeriodStyle(){
	    return periodStyle;
	}
	
	/**
	 * Modes that this thermostat type supports.
	 * Note: Utility Pro <i>supports</i> WEEKDAY_WEEKEND, however there is a role property that is  used to determine if the option is made available in the UI (ConsumerInfoRole.THERMOSTAT_SCHEDULE_5_2).
	 */
	public Set<ThermostatScheduleMode> getSupportedScheduleModes() {
		return supportedScheduleModes;
	}
	
	/**
	 * Modes that this thermostat type supports minus modes disallowed by the energy company
	 * @param yukonEnergyCompany
	 * @return
	 */
	public Set<ThermostatScheduleMode> getAllowedModes(Set<ThermostatScheduleMode> allModesAllowedByEnergyCompany) {
	    //we want a subset of supported schedule modes
	    return Sets.intersection(supportedScheduleModes, allModesAllowedByEnergyCompany);
	}
	
	public FahrenheitTemperature getLowerLimitCoolInFahrenheit() {
		return lowerLimitCoolInFahrenheit;
	}
	public FahrenheitTemperature getUpperLimitCoolInFahrenheit() {
		return upperLimitCoolInFahrenheit;
	}
	public FahrenheitTemperature getLowerLimitHeatInFahrenheit() {
		return lowerLimitHeatInFahrenheit;
	}
	public FahrenheitTemperature getUpperLimitHeatInFahrenheit() {
		return upperLimitHeatInFahrenheit;
	}
	
	public FahrenheitTemperature getLowerLimitInFahrenheit(HeatCoolSettingType heatCoolSettingType){
	    if(heatCoolSettingType == HeatCoolSettingType.HEAT) {
	        return getLowerLimitHeatInFahrenheit();
	    } else if(heatCoolSettingType == HeatCoolSettingType.COOL) {
	        return getLowerLimitCoolInFahrenheit();
	    } else {
	        //HeatCoolSetting == OTHER.  Nothing we can do with this.
	        throw new IllegalArgumentException("HeatCoolSettingType must be HEAT or COOL.");
	    }
	}
	
	public FahrenheitTemperature getUpperLimitInFahrenheit(HeatCoolSettingType heatCoolSettingType){
	    if(heatCoolSettingType == HeatCoolSettingType.HEAT) {
	        return getUpperLimitHeatInFahrenheit();
	    } else if(heatCoolSettingType == HeatCoolSettingType.COOL) {
	        return getUpperLimitCoolInFahrenheit();
	    }else {
            //HeatCoolSetting == OTHER.  Nothing we can do with this.
            throw new IllegalArgumentException("HeatCoolSettingType must be HEAT or COOL.");
        }
    }
	
	public ThermostatScheduleMode getDefaultThermostatScheduleMode() {
	    //get the first mode in the supported modes list
	    return supportedScheduleModes.iterator().next();
    }
	
    public static SchedulableThermostatType getByHardwareType(HardwareType hardwareType) {
        
        for (SchedulableThermostatType schedulableThermostatType : SchedulableThermostatType.values()) {
            if (schedulableThermostatType.getHardwareType() == hardwareType) {
                return schedulableThermostatType;
            }
        }
        
        throw new IllegalArgumentException("Invalid hardwareType: " + hardwareType);
    }
	
	public static boolean isSchedulableThermostatType(HardwareType hardwareType){
	    for (SchedulableThermostatType schedulableThermostatType : SchedulableThermostatType.values()) {
            if (schedulableThermostatType.getHardwareType() == hardwareType) {
                return true;
            }
        }
	    return false;
	}
}