package com.cannontech.stars.dr.thermostat.model;

import org.joda.time.LocalTime;


public enum ThermostatSchedulePeriod {
    FAKE_1(0, new LocalTime(1,0), true),
    FAKE_2(1, new LocalTime(2,0), true),
    
    OCCUPIED(2, new LocalTime(8,0)),
    UNOCCUPIED(3, new LocalTime(17,0)),
    
    WAKE(0, new LocalTime(6,0)),
    LEAVE(1, new LocalTime(8,30)),
    RETURN(2, new LocalTime(17,0)),
    SLEEP(3, new LocalTime(21,0)),
    ;
    
    private int entryIndex;
    private final boolean psuedo;
    private final LocalTime defaultStartTime;
    
    private ThermostatSchedulePeriod(int entryIndex, LocalTime defaultStartTime, boolean psuedo) {
        this.entryIndex = entryIndex;
        this.defaultStartTime = defaultStartTime;
        this.psuedo = psuedo;
    }
    
    private ThermostatSchedulePeriod(int entryIndex, LocalTime defaultStartTime) {
        this(entryIndex, defaultStartTime, false);
    }
    
    public boolean isPsuedo() {
        return psuedo;
    }
    
    public int getEntryIndex(){
        return entryIndex;
    }
    
    public LocalTime getDefaultStartTime() {
        return defaultStartTime;
    }
    
}
