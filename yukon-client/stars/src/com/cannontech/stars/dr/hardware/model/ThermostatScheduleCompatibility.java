package com.cannontech.stars.dr.hardware.model;

import java.util.Set;

import com.cannontech.common.inventory.HardwareType;
import com.google.common.collect.Sets;

public enum ThermostatScheduleCompatibility {
    RESIDENTIAL_EXPRESSSTAT(HardwareType.EXPRESSSTAT,
                            Sets.immutableEnumSet(SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT,
                                                  SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT)),
                                                  
    HEAT_PUMP_EXPRESSSTAT(HardwareType.EXPRESSSTAT_HEAT_PUMP,
                          Sets.immutableEnumSet(SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT,
                                                SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT)),
                                                
    COMMERCIAL_EXPRESSSTAT(HardwareType.COMMERCIAL_EXPRESSSTAT,
                           Sets.immutableEnumSet(SchedulableThermostatType.COMMERCIAL_EXPRESSSTAT)),
                           
    UTILITY_PRO(HardwareType.UTILITY_PRO,
                Sets.immutableEnumSet(SchedulableThermostatType.UTILITY_PRO)),
                
    UTILITY_PRO_ZIGBEE(HardwareType.UTILITY_PRO_ZIGBEE,
                       Sets.immutableEnumSet(SchedulableThermostatType.UTILITY_PRO,
                                             SchedulableThermostatType.UTILITY_PRO_ZIGBEE)),
    UTILITY_PRO_G2(HardwareType.UTILITY_PRO_G2,
                   Sets.immutableEnumSet(SchedulableThermostatType.UTILITY_PRO,
                                         SchedulableThermostatType.UTILITY_PRO_ZIGBEE,
                                         SchedulableThermostatType.UTILITY_PRO_G2,
                                         SchedulableThermostatType.UTILITY_PRO_G3)),
                                         
    UTILITY_PRO_G3(HardwareType.UTILITY_PRO_G3,
                   Sets.immutableEnumSet(SchedulableThermostatType.UTILITY_PRO,
                                         SchedulableThermostatType.UTILITY_PRO_ZIGBEE,
                                         SchedulableThermostatType.UTILITY_PRO_G2,
                                         SchedulableThermostatType.UTILITY_PRO_G3)),;
    
    private HardwareType hardwareType;
    private Set<SchedulableThermostatType> compatibleScheduleTypes;
    
    ThermostatScheduleCompatibility(HardwareType hardwareType,
                                          Set<SchedulableThermostatType> compatibleScheduleTypes){
        this.hardwareType = hardwareType;
        this.compatibleScheduleTypes = compatibleScheduleTypes;
    }
    
    public HardwareType getHardwareType(){
        return this.hardwareType;
    }
    
    public Set<SchedulableThermostatType> getCompatibleScheduleTypes(){
        return this.compatibleScheduleTypes;
    }
    
    public static ThermostatScheduleCompatibility getByHardwareType(HardwareType hardwareType){
        for (ThermostatScheduleCompatibility schedulableThermostatType : ThermostatScheduleCompatibility.values()) {
            if (schedulableThermostatType.getHardwareType() == hardwareType) {
                return schedulableThermostatType;
            }
        }
        
        throw new IllegalArgumentException("Invalid hardwareType: " + hardwareType);
    }
}
