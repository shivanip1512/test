package com.cannontech.stars.dr.thermostat.model;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public enum ThermostatSchedulePeriodStyle {
    TWO_TIMES(
              //all schedules have four periods, but commercial t-stats only use two
              ThermostatSchedulePeriod.FAKE_1, 
              ThermostatSchedulePeriod.FAKE_2, 
              ThermostatSchedulePeriod.OCCUPIED, 
              ThermostatSchedulePeriod.UNOCCUPIED
    ),
    FOUR_TIMES(
               ThermostatSchedulePeriod.WAKE,
               ThermostatSchedulePeriod.LEAVE,
               ThermostatSchedulePeriod.RETURN,
               ThermostatSchedulePeriod.SLEEP
    );
    
    private ImmutableList<ThermostatSchedulePeriod> allPeriods;
    private ImmutableList<ThermostatSchedulePeriod> realPeriods;
    
    private ThermostatSchedulePeriodStyle(ThermostatSchedulePeriod... periods){
        this.allPeriods = ImmutableList.copyOf(periods);
        Builder<ThermostatSchedulePeriod> realPeriodsBuilder = ImmutableList.builder();
        for (ThermostatSchedulePeriod thermostatSchedulePeriod : allPeriods) {
            if (!thermostatSchedulePeriod.isPsuedo()) {
                realPeriodsBuilder.add(thermostatSchedulePeriod);
            }
        }
        realPeriods = realPeriodsBuilder.build();
    }
    
    public List<ThermostatSchedulePeriod> getRealPeriods() {
        return realPeriods;
    }
    
    public List<ThermostatSchedulePeriod> getAllPeriods() {
        return allPeriods;
    }
    
    public ThermostatSchedulePeriod getPeriod(int index){
        return getRealPeriods().get(index);
    }
    
    public int getCount(){
        return allPeriods.size();
    }
    
    public boolean containsPeriodEntryIndex(int index){
        List<ThermostatSchedulePeriod> periods = getRealPeriods();
        for(ThermostatSchedulePeriod period : periods){
            if(period.getEntryIndex() == index){
                return true;
            }
        }
        return false;
    }
}
