package com.cannontech.dr.ecobee.message.partial;

import org.joda.time.Instant;


public class DutyCycleDr {
    private final String name;
    private final String message;
    private final DutyCycleEvent event;
    private final boolean randomizeStartTime;
    private final boolean randomizeEndTime;
    
    public DutyCycleDr(String name, String message, int dutyCyclePercentage, Instant startDate, 
                       boolean randomizeStartTime, Instant endDate, boolean randomizeEndTime) {
        this.name = name;
        this.message = message;
        this.randomizeStartTime = randomizeStartTime;
        this.randomizeEndTime = randomizeEndTime;
        this.event = new DutyCycleEvent(name, dutyCyclePercentage, startDate, endDate);
    }

    public String getName() {
        return name;
    }
    
    public String getMessage() {
        return message;
    }
    
    public DutyCycleEvent getEvent() {
        return event;
    }
    
    public boolean getRandomizeStartTime() {
        return randomizeStartTime;
    }
    
    public boolean getRandomizeEndTime() {
        return randomizeEndTime;
    }
}
