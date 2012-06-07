package com.cannontech.stars.dr.hardware.model;

import java.util.Set;

import com.cannontech.common.inventory.HardwareType;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSetMultimap.Builder;
import com.google.common.collect.SetMultimap;

public class ThermostatScheduleCompatibility {
    
    private static final SetMultimap<HardwareType, SchedulableThermostatType> map;
    
    static {
        Builder<HardwareType, SchedulableThermostatType> builder = ImmutableSetMultimap.builder();

        builder.putAll(HardwareType.EXPRESSSTAT,
                       SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT,
                       SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT);
        builder.putAll(HardwareType.EXPRESSSTAT_HEAT_PUMP,
                       SchedulableThermostatType.RESIDENTIAL_EXPRESSSTAT,
                       SchedulableThermostatType.HEAT_PUMP_EXPRESSSTAT);
        builder.putAll(HardwareType.COMMERCIAL_EXPRESSSTAT,
                       SchedulableThermostatType.COMMERCIAL_EXPRESSSTAT);
        builder.putAll(HardwareType.UTILITY_PRO,
                       SchedulableThermostatType.UTILITY_PRO);
        builder.putAll(HardwareType.UTILITY_PRO_ZIGBEE,
                       SchedulableThermostatType.UTILITY_PRO,
                       SchedulableThermostatType.UTILITY_PRO_ZIGBEE,
                       SchedulableThermostatType.UTILITY_PRO_G2,
                       SchedulableThermostatType.UTILITY_PRO_G3);
        builder.putAll(HardwareType.UTILITY_PRO_G2,
                       SchedulableThermostatType.UTILITY_PRO,
                       SchedulableThermostatType.UTILITY_PRO_ZIGBEE,
                       SchedulableThermostatType.UTILITY_PRO_G2,
                       SchedulableThermostatType.UTILITY_PRO_G3);
        builder.putAll(HardwareType.UTILITY_PRO_G3,
                       SchedulableThermostatType.UTILITY_PRO,
                       SchedulableThermostatType.UTILITY_PRO_ZIGBEE,
                       SchedulableThermostatType.UTILITY_PRO_G2,
                       SchedulableThermostatType.UTILITY_PRO_G3);
        
        map = builder.build();
    }
    
    public static Set<SchedulableThermostatType> getCompatibleTypes(HardwareType hardwareType) throws IllegalArgumentException {
        Set<SchedulableThermostatType> types = map.get(hardwareType);
        
        if(types.size() == 0){
            throw new IllegalArgumentException("Expected a 'Thermostat' HardwareType. hardwareType = [" + hardwareType + "]");
        }
        
        return map.get(hardwareType);
    }
}
