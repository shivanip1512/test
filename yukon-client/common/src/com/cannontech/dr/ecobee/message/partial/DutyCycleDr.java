package com.cannontech.dr.ecobee.message.partial;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
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
        event = new DutyCycleEvent(name, dutyCyclePercentage, startDate, endDate);
    }

    @JsonCreator
    public DutyCycleDr(@JsonProperty("name") String name, @JsonProperty("message") String message,
            @JsonProperty("event") DutyCycleEvent event, 
            @JsonProperty("randomizeStartTime") boolean randomizeStartTime, 
            @JsonProperty("randomizeEndTime") boolean randomizeEndTime) {
        this.name = name;
        this.message = message;
        this.event = event;
        this.randomizeStartTime = randomizeStartTime;
        this.randomizeEndTime = randomizeEndTime;
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
