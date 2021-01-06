package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWCommandParamsV1 implements Serializable {
    private final String vrelay;
    private final Integer cyclePercent;
    private final Integer cyclePeriod;
    private final Integer cycleCount;
    private final Long startTime;
    private final Integer eventId;
    private final Integer criticality;
    private final Integer randomization;
    private final Integer flags;

    @JsonCreator
    public PxMWCommandParamsV1(@JsonProperty("vrelay") String vrelay, @JsonProperty("cycle percent") Integer cyclePercent,
            @JsonProperty("cycle period") Integer cyclePeriod, @JsonProperty("cycle count") Integer cycleCount,
            @JsonProperty("start time") Long startTime,
            @JsonProperty("event ID") Integer eventId, @JsonProperty("criticality") Integer criticality,
            @JsonProperty("randomization") Integer randomization, @JsonProperty("flags") Integer flags) {
        this.vrelay = vrelay;
        this.cyclePercent = cyclePercent;
        this.cyclePeriod = cyclePeriod;
        this.cycleCount = cycleCount;
        this.startTime = startTime;
        this.eventId = eventId;
        this.criticality = criticality;
        this.randomization = randomization;
        this.flags = flags;
    }

    @JsonProperty("vrelay")
    public String getVrelay() {
        return vrelay;
    }

    @JsonProperty("cycle percent")
    public Integer getCyclePercent() {
        return cyclePercent;
    }
    
    @JsonProperty("cycle period")
    public Integer getCyclePeriod() {
        return cyclePeriod;
    }

    @JsonProperty("cycle count")
    public Integer getCycleCount() {
        return cycleCount;
    }

    @JsonProperty("start time")
    public Long getStartTime() {
        return startTime;
    }

    @JsonProperty("event ID")
    public Integer getEventId() {
        return eventId;
    }

    @JsonProperty("criticality")
    public Integer getCriticality() {
        return criticality;
    }

    @JsonProperty("randomization")
    public Integer getRandomization() {
        return randomization;
    }

    @JsonProperty("flags")
    public Integer getFlags() {
        return flags;
    }
}
