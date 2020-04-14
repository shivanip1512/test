package com.cannontech.dr.honeywell.message;

import java.util.Arrays;

import org.joda.time.Instant;

import com.cannontech.dr.JsonSerializers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DRSetpointEventRequest {

    private final int eventID;
    private final Instant startTime;
    private final boolean optOutable;
    private final int randomizationInterval;
    private final DutyCyclePeriod dutyCyclePeriod;
    private final DRSetpointEventInterval[] intervals;

    public DRSetpointEventRequest(int eventID, Instant startTime, boolean optOutable,
            int randomizationInterval, DutyCyclePeriod dutyCyclePeriod, int sequenceNumber,
            Double heatDeltaC, Double coolDeltaC, int durationSeconds) {
        this.eventID = eventID;
        this.startTime = startTime;
        this.optOutable = optOutable;
        this.randomizationInterval = randomizationInterval;
        this.dutyCyclePeriod = dutyCyclePeriod;
        this.intervals = new DRSetpointEventInterval[] {
                new DRSetpointEventInterval(sequenceNumber, heatDeltaC, coolDeltaC, durationSeconds) };
    }

    @JsonCreator
    public DRSetpointEventRequest(@JsonProperty("EventID") int eventID,
            @JsonProperty("StartTime") Instant startTime,
            @JsonProperty("OptOutable") boolean optOutable,
            @JsonProperty("RandomizationInterval") int randomizationInterval,
            @JsonProperty("DutyCyclePeriod") DutyCyclePeriod dutyCyclePeriod,
            @JsonProperty("DREventInterval") DRSetpointEventInterval[] intervals) {

        this.eventID = eventID;
        this.startTime = startTime;
        this.optOutable = optOutable;
        this.randomizationInterval = randomizationInterval;
        this.dutyCyclePeriod = dutyCyclePeriod;
        this.intervals = intervals;
    }

    public int getEventID() {
        return eventID;
    }

    @JsonSerialize(using = JsonSerializers.TO_DATE_HONEYWELL_WRAPPER.class)
    @JsonDeserialize(using = JsonSerializers.FROM_DATE_HONEYWELL_WRAPPER.class)
    public Instant getStartTime() {
        return startTime;
    }

    public boolean isOptOutable() {
        return optOutable;
    }

    public int getRandomizationInterval() {
        return randomizationInterval;
    }

    public DutyCyclePeriod getDutyCyclePeriod() {
        return dutyCyclePeriod;
    }

    public DRSetpointEventInterval[] getIntervals() {
        return intervals;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dutyCyclePeriod == null) ? 0 : dutyCyclePeriod.hashCode());
        result = prime * result + eventID;
        result = prime * result + (optOutable ? 1231 : 1237);
        result = prime * result + randomizationInterval;
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
        DRSetpointEventRequest other = (DRSetpointEventRequest) obj;
        if (dutyCyclePeriod != other.dutyCyclePeriod) {
            return false;
        }
        if (eventID != other.eventID) {
            return false;
        }
        if (optOutable != other.optOutable) {
            return false;
        }
        if (randomizationInterval != other.randomizationInterval) {
            return false;
        }
        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DRSetpointEventRequest [eventID=" + eventID + ", startTime=" + startTime + ", optOutable=" + optOutable
                + ", randomizationInterval=" + randomizationInterval + ", dutyCyclePeriod=" + dutyCyclePeriod + ", intervals="
                + Arrays.toString(intervals) + "]";
    }

}
