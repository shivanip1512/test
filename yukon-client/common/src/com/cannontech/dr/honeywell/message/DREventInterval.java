package com.cannontech.dr.honeywell.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DREventInterval {

    private final int sequenceNumber;
    private final Double dutyCycle;
    private final int durationSeconds;
    
    @JsonCreator
    public DREventInterval(@JsonProperty("SequenceNumber") int sequenceNumber,
            @JsonProperty("DutyCycle") Double dutyCycle,
            @JsonProperty("DurationSeconds") int durationSeconds) {
        this.sequenceNumber = sequenceNumber;
        this.dutyCycle = dutyCycle;
        this.durationSeconds = durationSeconds;
    }

    public Double getDutyCycle() {
        return dutyCycle;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + durationSeconds;
        result = prime * result + ((dutyCycle == null) ? 0 : dutyCycle.hashCode());
        result = prime * result + sequenceNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DREventInterval other = (DREventInterval) obj;
        if (durationSeconds != other.durationSeconds)
            return false;
        if (dutyCycle == null) {
            if (other.dutyCycle != null)
                return false;
        } else if (!dutyCycle.equals(other.dutyCycle))
            return false;
        if (sequenceNumber != other.sequenceNumber)
            return false;
        return true;
    }
    
}
