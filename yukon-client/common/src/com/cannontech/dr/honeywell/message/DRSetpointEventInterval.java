package com.cannontech.dr.honeywell.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class DRSetpointEventInterval {

    private final int sequenceNumber;
    private final Double heatDeltaC;
    private final Double coolDeltaC;
    private final int durationSeconds;

    @JsonCreator
    public DRSetpointEventInterval(@JsonProperty("SequenceNumber") int sequenceNumber,
            @JsonProperty("DutyCycle") Double heatDeltaC,
            @JsonProperty("DutyCycle") Double coolDeltaC,
            @JsonProperty("DurationSeconds") int durationSeconds) {
        this.sequenceNumber = sequenceNumber;
        this.heatDeltaC = heatDeltaC;
        this.coolDeltaC = coolDeltaC;
        this.durationSeconds = durationSeconds;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public Double getHeatDeltaC() {
        return heatDeltaC;
    }

    public Double getCoolDeltaC() {
        return coolDeltaC;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

}
