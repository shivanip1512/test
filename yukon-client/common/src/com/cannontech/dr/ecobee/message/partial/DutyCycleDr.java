package com.cannontech.dr.ecobee.message.partial;


public class DutyCycleDr {
    private final String name;
    private final String message;
    private final DutyCycleEvent event;
    private final boolean randomizeStartTime;
    private final boolean randomizeEndTime;
    
    public DutyCycleDr(String name, String message, int dutyCyclePercentage, String startDate, 
            String startTime, boolean randomizeStartTime, String endDate, String endTime, boolean randomizeEndTime) {
        this.name = name;
        this.message = message;
        this.randomizeStartTime = randomizeStartTime;
        this.randomizeEndTime = randomizeEndTime;
        this.event = new DutyCycleEvent(name, dutyCyclePercentage, startDate, startTime, endDate, endTime);
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
