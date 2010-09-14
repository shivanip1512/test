package com.cannontech.stars.dr.thermostat.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public enum ThermostatSchedulePeriodStyle {
    TWO_TIMES(
              ThermostatSchedulePeriod.OCCUPIED, 
              ThermostatSchedulePeriod.UNOCCUPIED
    ),
    FOUR_TIMES(
               ThermostatSchedulePeriod.WAKE,
               ThermostatSchedulePeriod.LEAVE,
               ThermostatSchedulePeriod.RETURN,
               ThermostatSchedulePeriod.SLEEP
    );
    
    private ImmutableList<ThermostatSchedulePeriod> periods;
    
    private ThermostatSchedulePeriodStyle(ThermostatSchedulePeriod... periods){
        this.periods = ImmutableList.of(periods);
    }
    
    public List<ThermostatSchedulePeriod> getPeriods(){
        return periods;
    }
    
    public ThermostatSchedulePeriod getPeriod(int index){
        return getPeriods().get(index);
    }
    
    public int getCount(){
        return periods.size();
    }
    
    public boolean containsPeriod(ThermostatSchedulePeriod period){
        return getPeriods().contains(period);
    }
    
    public boolean containsPeriodEntryIndex(int index){
        List<ThermostatSchedulePeriod> periods = getPeriods();
        for(ThermostatSchedulePeriod period : periods){
            if(period.getEntryIndex() == index){
                return true;
            }
        }
        return false;
    }
}
