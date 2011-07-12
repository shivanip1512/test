package com.cannontech.stars.dr.hardware.model;

import java.util.Set;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.google.common.collect.Sets;

public enum SchedulableThermostatType {

	RESIDENTIAL_EXPRESSSTAT(HardwareType.EXPRESSSTAT,
                            45, 88,
                            45, 88,
                            ThermostatSchedulePeriodStyle.FOUR_TIMES,
                            Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                                  ThermostatScheduleMode.ALL),
                            Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                                  ThermostatFanState.ON)
	),
	HEAT_PUMP_EXPRESSSTAT(HardwareType.EXPRESSSTAT_HEAT_PUMP,
	                      45, 88, 
	                      45, 88,
	                      ThermostatSchedulePeriodStyle.FOUR_TIMES,
	                      Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
	                                            ThermostatScheduleMode.ALL),
                          Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                                ThermostatFanState.ON)
	),
    COMMERCIAL_EXPRESSSTAT(HardwareType.COMMERCIAL_EXPRESSSTAT,
                           45, 88,
                           45, 88,
                           ThermostatSchedulePeriodStyle.TWO_TIMES,
                           Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                                 ThermostatScheduleMode.ALL),
                           Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                                 ThermostatFanState.ON)
    ),
	UTILITY_PRO(HardwareType.UTILITY_PRO,
				50, 99, //yes, you read it right. Cool has higher upper-limit than Heat, and Heat has a lower lower-limit than Cool. Go ask a Honeywell device engineer.
				40, 90,
				ThermostatSchedulePeriodStyle.FOUR_TIMES,
				Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
				                      ThermostatScheduleMode.ALL, 
				                      ThermostatScheduleMode.WEEKDAY_WEEKEND),
                Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                      ThermostatFanState.ON,
                                      ThermostatFanState.CIRCULATE)
	),
	UTILITY_PRO_G2(HardwareType.UTILITY_PRO_G2,
                   50, 99,
                   40, 90,
                   ThermostatSchedulePeriodStyle.FOUR_TIMES,
                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN,
                                         ThermostatScheduleMode.ALL,
                                         ThermostatScheduleMode.WEEKDAY_WEEKEND,
                                         ThermostatScheduleMode.SEVEN_DAY),
                   Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                         ThermostatFanState.CIRCULATE,
                                         ThermostatFanState.ON)
    ),
    UTILITY_PRO_G3(HardwareType.UTILITY_PRO_G3,
                   50, 99,
                   40, 90,
                   ThermostatSchedulePeriodStyle.FOUR_TIMES,
                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                         ThermostatScheduleMode.ALL, 
                                         ThermostatScheduleMode.WEEKDAY_WEEKEND, 
                                         ThermostatScheduleMode.SEVEN_DAY),
                   Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                         ThermostatFanState.CIRCULATE,
                                         ThermostatFanState.ON)
    ),
	UTILITY_PRO_ZIGBEE(HardwareType.UTILITY_PRO_ZIGBEE,
	                   50, 99,
	                   40, 90,
	                   ThermostatSchedulePeriodStyle.FOUR_TIMES,
	                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
	                                         ThermostatScheduleMode.ALL, 
	                                         ThermostatScheduleMode.WEEKDAY_WEEKEND, 
	                                         ThermostatScheduleMode.SEVEN_DAY),
                       Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                             ThermostatFanState.CIRCULATE,
                                             ThermostatFanState.ON)
	)
	;
	
	private HardwareType hardwareType;
	private FahrenheitTemperature lowerLimitCoolInFahrenheit;
	private FahrenheitTemperature upperLimitCoolInFahrenheit;
	private FahrenheitTemperature lowerLimitHeatInFahrenheit;
	private FahrenheitTemperature upperLimitHeatInFahrenheit;
	private Set<ThermostatScheduleMode> supportedScheduleModes;
	private Set<ThermostatFanState> supportedFanStates;
	private final ThermostatSchedulePeriodStyle periodStyle;
	
	SchedulableThermostatType(HardwareType hardwareType,
							  int lowerLimitCoolInFahrenheit, int upperLimitCoolInFahrenheit,
							  int lowerLimitHeatInFahrenheit, int upperLimitHeatInFahrenheit,
							  ThermostatSchedulePeriodStyle periodStyle,
							  Set<ThermostatScheduleMode> supportedScheduleModes,
							  Set<ThermostatFanState> supportedFanStates) {
		
		this.hardwareType = hardwareType;
		this.lowerLimitCoolInFahrenheit = new FahrenheitTemperature(lowerLimitCoolInFahrenheit);
		this.upperLimitCoolInFahrenheit = new FahrenheitTemperature(upperLimitCoolInFahrenheit);
		this.lowerLimitHeatInFahrenheit = new FahrenheitTemperature(lowerLimitHeatInFahrenheit);
		this.upperLimitHeatInFahrenheit = new FahrenheitTemperature(upperLimitHeatInFahrenheit);
		this.periodStyle = periodStyle;
		this.supportedScheduleModes = supportedScheduleModes;
		this.supportedFanStates = supportedFanStates;
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
	
	public Set<ThermostatFanState> getSupportedFanStates() {
	    return this.supportedFanStates;
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