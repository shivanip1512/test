package com.cannontech.stars.dr.thermostat.model;

import java.util.List;

import com.google.common.collect.Lists;

public class ThermostatScheduleDisplay {
    private String timeOfWeekString;
    private List<String> entryList;

    public ThermostatScheduleDisplay(){
        entryList = Lists.newArrayList();
    }
    
    public String getTimeOfWeekString() {
        return timeOfWeekString;
    }

    public void setTimeOfWeekString(String timeOfWeekString) {
        this.timeOfWeekString = timeOfWeekString;
    }
    
    public void addToEntryList(String entry){
        entryList.add(entry);
    }
    
    public void setEntryList(List<String> entryList){
        this.entryList = entryList;
    }
    
    public List<String> getEntryList(){
        return entryList;
    }
}
