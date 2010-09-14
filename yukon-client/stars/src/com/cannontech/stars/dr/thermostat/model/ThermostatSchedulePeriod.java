package com.cannontech.stars.dr.thermostat.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.google.common.collect.ImmutableList;

public enum ThermostatSchedulePeriod {
    OCCUPIED(
             2,
             HardwareType.COMMERCIAL_EXPRESSSTAT
    ),
    UNOCCUPIED(
               3,
               HardwareType.COMMERCIAL_EXPRESSSTAT
    ),
    WAKE(
         0,
         HardwareType.EXPRESSSTAT,
         HardwareType.EXPRESSSTAT_HEAT_PUMP,
         HardwareType.UTILITY_PRO,
         HardwareType.ENERGYPRO
    ),
    LEAVE(
          1,
          HardwareType.EXPRESSSTAT,
          HardwareType.EXPRESSSTAT_HEAT_PUMP,
          HardwareType.UTILITY_PRO,
          HardwareType.ENERGYPRO
    ),
    RETURN(
           2,
           HardwareType.EXPRESSSTAT,
           HardwareType.EXPRESSSTAT_HEAT_PUMP,
           HardwareType.UTILITY_PRO,
           HardwareType.ENERGYPRO
    ),
    SLEEP(
          3,
          HardwareType.EXPRESSSTAT,
          HardwareType.EXPRESSSTAT_HEAT_PUMP,
          HardwareType.UTILITY_PRO,
          HardwareType.ENERGYPRO
    );
    
    private int entryIndex;
    private List<HardwareType> supportedHardwareTypes;
    
    private ThermostatSchedulePeriod(int entryIndex, HardwareType... hardwareTypes){
        this.entryIndex = entryIndex;
        this.supportedHardwareTypes = ImmutableList.of(hardwareTypes);
    }
    
    public int getEntryIndex(){
        return entryIndex;
    }
    
    public static List<ThermostatSchedulePeriod> commercialStyle(){
        return ImmutableList.of(OCCUPIED, UNOCCUPIED);
    }
    
    public static List<ThermostatSchedulePeriod> residentialStyle(){
        return ImmutableList.of(WAKE, LEAVE, RETURN, SLEEP);
    }
    
    public List<HardwareType> getSupportedHardwareTypes(){
        return supportedHardwareTypes;
    }
 
    public static List<ThermostatSchedulePeriod> getPeriodsForHardwareType(HardwareType type){
       ThermostatSchedulePeriod[] allPeriods = ThermostatSchedulePeriod.values();
       List<ThermostatSchedulePeriod> periodsForType = new ArrayList<ThermostatSchedulePeriod>();
       
       for(int i = 0; i < allPeriods.length; i++){
           if(allPeriods[i].getSupportedHardwareTypes().contains(type)){
               periodsForType.add(allPeriods[i]);
           }
       }
       
       return periodsForType;
    }
}
