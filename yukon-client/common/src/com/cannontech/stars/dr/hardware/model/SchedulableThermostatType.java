package com.cannontech.stars.dr.hardware.model;

import java.util.Set;

import org.joda.time.Duration;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.google.common.collect.Sets;

public enum SchedulableThermostatType {

	RESIDENTIAL_EXPRESSSTAT(HardwareType.EXPRESSSTAT,
                            Temperature.fromFahrenheit(45), Temperature.fromFahrenheit(88),
                            Temperature.fromFahrenheit(45), Temperature.fromFahrenheit(88),
                            Duration.standardMinutes(15),
                            Duration.standardMinutes(15),
                            ThermostatSchedulePeriodStyle.FOUR_TIMES,
                            Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                                  ThermostatScheduleMode.ALL),
                            Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                                  ThermostatFanState.ON),
                            Sets.immutableEnumSet(ThermostatMode.COOL,
                                                  ThermostatMode.HEAT,
                                                  ThermostatMode.OFF)
	),
	HEAT_PUMP_EXPRESSSTAT(HardwareType.EXPRESSSTAT_HEAT_PUMP,
	                      Temperature.fromFahrenheit(45), Temperature.fromFahrenheit(88), 
	                      Temperature.fromFahrenheit(45), Temperature.fromFahrenheit(88),
	                      Duration.standardMinutes(15),
	                      Duration.standardMinutes(15),
	                      ThermostatSchedulePeriodStyle.FOUR_TIMES,
	                      Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
	                                            ThermostatScheduleMode.ALL),
                          Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                                ThermostatFanState.ON),
                          Sets.immutableEnumSet(ThermostatMode.COOL,
                                                ThermostatMode.HEAT,
                                                ThermostatMode.EMERGENCY_HEAT,
                                                ThermostatMode.OFF)
	),
    COMMERCIAL_EXPRESSSTAT(HardwareType.COMMERCIAL_EXPRESSSTAT,
                           Temperature.fromFahrenheit(45), Temperature.fromFahrenheit(88),
                           Temperature.fromFahrenheit(45), Temperature.fromFahrenheit(88),
                           Duration.standardMinutes(15),
                           Duration.standardMinutes(15),
                           ThermostatSchedulePeriodStyle.TWO_TIMES,
                           Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                                 ThermostatScheduleMode.ALL),
                           Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                                 ThermostatFanState.ON),
                           Sets.immutableEnumSet(ThermostatMode.COOL,
                                                 ThermostatMode.HEAT,
                                                 ThermostatMode.OFF)
    ),
	UTILITY_PRO(HardwareType.UTILITY_PRO,
				Temperature.fromFahrenheit(50), Temperature.fromFahrenheit(99),
				Temperature.fromFahrenheit(40), Temperature.fromFahrenheit(90),
				Duration.standardMinutes(15),
				Duration.standardMinutes(15),
				ThermostatSchedulePeriodStyle.FOUR_TIMES,
				Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
				                      ThermostatScheduleMode.ALL, 
				                      ThermostatScheduleMode.WEEKDAY_WEEKEND),
                Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                      ThermostatFanState.CIRCULATE,
                                      ThermostatFanState.ON),
                Sets.immutableEnumSet(ThermostatMode.COOL,
                                      ThermostatMode.HEAT,
                                      ThermostatMode.OFF)
	),
	UTILITY_PRO_G2(HardwareType.UTILITY_PRO_G2,
                   Temperature.fromFahrenheit(50), Temperature.fromFahrenheit(99),
                   Temperature.fromFahrenheit(40), Temperature.fromFahrenheit(90),
                   Duration.standardMinutes(15),
                   Duration.standardMinutes(15),
                   ThermostatSchedulePeriodStyle.FOUR_TIMES,
                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN,
                                         ThermostatScheduleMode.ALL,
                                         ThermostatScheduleMode.WEEKDAY_WEEKEND,
                                         ThermostatScheduleMode.SEVEN_DAY),
                   Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                         ThermostatFanState.CIRCULATE,
                                         ThermostatFanState.ON),
                   Sets.immutableEnumSet(ThermostatMode.AUTO,
                                         ThermostatMode.COOL,
                                         ThermostatMode.HEAT,
                                         ThermostatMode.OFF)
    ),
    UTILITY_PRO_G3(HardwareType.UTILITY_PRO_G3,
                   Temperature.fromFahrenheit(50), Temperature.fromFahrenheit(99),
                   Temperature.fromFahrenheit(40), Temperature.fromFahrenheit(90),
                   Duration.standardMinutes(15),
                   Duration.standardMinutes(15),
                   ThermostatSchedulePeriodStyle.FOUR_TIMES,
                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
                                         ThermostatScheduleMode.ALL, 
                                         ThermostatScheduleMode.WEEKDAY_WEEKEND, 
                                         ThermostatScheduleMode.SEVEN_DAY),
                   Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                         ThermostatFanState.CIRCULATE,
                                         ThermostatFanState.ON),
                   Sets.immutableEnumSet(ThermostatMode.AUTO,
                                         ThermostatMode.COOL,
                                         ThermostatMode.HEAT,
                                         ThermostatMode.OFF)
    ),
	UTILITY_PRO_ZIGBEE(HardwareType.UTILITY_PRO_ZIGBEE,
	                   Temperature.fromFahrenheit(50), Temperature.fromFahrenheit(99),
	                   Temperature.fromFahrenheit(40), Temperature.fromFahrenheit(90),
	                   Duration.standardMinutes(15),
	                   Duration.standardMinutes(15),
	                   ThermostatSchedulePeriodStyle.FOUR_TIMES,
	                   Sets.immutableEnumSet(ThermostatScheduleMode.WEEKDAY_SAT_SUN, 
	                                         ThermostatScheduleMode.ALL, 
	                                         ThermostatScheduleMode.WEEKDAY_WEEKEND, 
	                                         ThermostatScheduleMode.SEVEN_DAY),
                       Sets.immutableEnumSet(ThermostatFanState.AUTO,
                                             ThermostatFanState.CIRCULATE,
                                             ThermostatFanState.ON),
                       Sets.immutableEnumSet(ThermostatMode.COOL,
                                             ThermostatMode.HEAT,
                                             ThermostatMode.OFF)
	)
	;
	
	private HardwareType hardwareType;
	private Temperature lowerLimitCool;
	private Temperature upperLimitCool;
	private Temperature lowerLimitHeat;
	private Temperature upperLimitHeat;
	private Duration resolution;
	private Duration minimumTimeBetweenPeriods;
	private Set<ThermostatScheduleMode> supportedScheduleModes;
	private Set<ThermostatFanState> supportedFanStates;
	private Set<ThermostatMode> supportedThermostatModes;
	private final ThermostatSchedulePeriodStyle periodStyle;
	
	SchedulableThermostatType(HardwareType hardwareType,
							  Temperature lowerLimitCool, Temperature upperLimitCool,
							  Temperature lowerLimitHeat, Temperature upperLimitHeat,
							  Duration resoultion,
							  Duration minimumTimeBetweenPeriods,
							  ThermostatSchedulePeriodStyle periodStyle,
							  Set<ThermostatScheduleMode> supportedScheduleModes,
							  Set<ThermostatFanState> supportedFanStates,
							  Set<ThermostatMode> supportedThermostatModes) {
		
		this.hardwareType = hardwareType;
		this.lowerLimitCool = lowerLimitCool;
		this.upperLimitCool = upperLimitCool;
		this.lowerLimitHeat = lowerLimitHeat;
		this.upperLimitHeat = upperLimitHeat;
		this.resolution = resoultion;
		this.minimumTimeBetweenPeriods = minimumTimeBetweenPeriods;
		this.periodStyle = periodStyle;
		this.supportedScheduleModes = supportedScheduleModes;
		this.supportedFanStates = supportedFanStates;
		this.supportedThermostatModes = supportedThermostatModes;
	}
	
	public HardwareType getHardwareType() {
		return hardwareType;
	}
	
	public ThermostatSchedulePeriodStyle getPeriodStyle(){
	    return periodStyle;
	}
	
	/**
	 * Modes that this thermostat type supports.
	 * Note: Utility Pro <i>supports</i> WEEKDAY_WEEKEND, however, there are energycompany role properties for each schedule mode to
	 * determine availability of the schedule in the UI
	 */
	public Set<ThermostatScheduleMode> getSupportedScheduleModes() {
		return supportedScheduleModes;
	}
	
	public Set<ThermostatFanState> getSupportedFanStates() {
	    return this.supportedFanStates;
	}
	
	public Set<ThermostatMode> getSupportedModes() {
	    return this.supportedThermostatModes;
	}
	
	/**
	 * The intersection of modes allowed by this SchedulableThermostatType and the set of modes allowed by an EnergyCompany
	 */
	public Set<ThermostatScheduleMode> getAllowedModes(Set<ThermostatScheduleMode> allModesAllowedByEnergyCompany) {
	    //we want a subset of supported schedule modes
	    return Sets.intersection(supportedScheduleModes, allModesAllowedByEnergyCompany);
	}
		
	public Temperature getLowerLimitCool() {
		return lowerLimitCool;
	}
	public Temperature getUpperLimitCool() {
		return upperLimitCool;
	}
	public Temperature getLowerLimitHeat() {
		return lowerLimitHeat;
	}
	public Temperature getUpperLimitHeat() {
		return upperLimitHeat;
	}
	
	public Temperature getLowerLimit(HeatCoolSettingType heatCoolSettingType){
        if(heatCoolSettingType == HeatCoolSettingType.HEAT) {
            return getLowerLimitHeat();
        } else if(heatCoolSettingType == HeatCoolSettingType.COOL) {
            return getLowerLimitCool();
        } else {
            //HeatCoolSetting == OTHER.  Nothing we can do with this.
            throw new IllegalArgumentException("HeatCoolSettingType must be HEAT or COOL.");
        }
    }
    
    public Temperature getUpperLimit(HeatCoolSettingType heatCoolSettingType){
        if(heatCoolSettingType == HeatCoolSettingType.HEAT) {
            return getUpperLimitHeat();
        } else if(heatCoolSettingType == HeatCoolSettingType.COOL) {
            return getUpperLimitCool();
        }else {
            //HeatCoolSetting == OTHER.  Nothing we can do with this.
            throw new IllegalArgumentException("HeatCoolSettingType must be HEAT or COOL.");
        }
    }
	
	public Duration getResolution() {  
	    return this.resolution;
	}
	
	public Duration getMinimumTimeBetweenPeriods() {
	    return this.minimumTimeBetweenPeriods;
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