package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWCommandParamsV1 implements Serializable {
    private final String vrelay;
    private final String cyclePercent;
    private final String cyclePeriod;
    private final String cycleCount;
    private final String startTime;
    private final String eventId;
    private final String criticality;
    private final String randomization;
    private final String flags;

    public PxMWCommandParamsV1(String vrelay, String cyclePercent, String cyclePeriod, String cycleCount, String startTime,
            String eventId, String criticality, String randomization, String flags) {
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

    public String getVrelay() {
        return vrelay;
    }

    public String getCyclePercent() {
        return cyclePercent;
    }

    public String getCyclePeriod() {
        return cyclePeriod;
    }

    public String getCycleCount() {
        return cycleCount;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEventId() {
        return eventId;
    }

    public String getCriticality() {
        return criticality;
    }

    public String getRandomization() {
        return randomization;
    }

    public String getFlags() {
        return flags;
    }
}
