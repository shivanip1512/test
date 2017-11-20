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
    private final boolean sendEmail;
    
    
    public DutyCycleDr(String name, String message, int dutyCyclePercentage, Instant startDate, 
                       boolean randomizeStartTime, Instant endDate, boolean randomizeEndTime, boolean isOptional,
                       boolean sendEmail) {
        this.name = name;
        this.message = message;
        this.randomizeStartTime = randomizeStartTime;
        this.randomizeEndTime = randomizeEndTime;
        event = new DutyCycleEvent(name, dutyCyclePercentage, startDate, endDate, isOptional);
        this.sendEmail = sendEmail;
    }

    @JsonCreator
    public DutyCycleDr(@JsonProperty("name") String name, @JsonProperty("message") String message,
            @JsonProperty("event") DutyCycleEvent event, 
            @JsonProperty("randomizeStartTime") boolean randomizeStartTime, 
            @JsonProperty("randomizeEndTime") boolean randomizeEndTime,
            @JsonProperty("sendEmail") boolean sendEmail) {
        this.name = name;
        this.message = message;
        this.event = event;
        this.randomizeStartTime = randomizeStartTime;
        this.randomizeEndTime = randomizeEndTime;
        this.sendEmail = sendEmail;
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

    public boolean getSendEmail() {
        return sendEmail;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (randomizeEndTime ? 1231 : 1237);
        result = prime * result + (randomizeStartTime ? 1231 : 1237);
        result = prime * result + (sendEmail ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DutyCycleDr other = (DutyCycleDr) obj;
        if (event == null) {
            if (other.event != null) {
                return false;
            }
        } else if (!event.equals(other.event)) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (randomizeEndTime != other.randomizeEndTime) {
            return false;
        }
        if (randomizeStartTime != other.randomizeStartTime) {
            return false;
        }
        if (sendEmail != other.sendEmail) {
            return false;
        }
        return true;
    }
    
}
