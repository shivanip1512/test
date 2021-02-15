package com.cannontech.dr.honeywellWifi.azure.event;

import org.joda.time.Duration;

import com.cannontech.common.util.JsonSerializers.FROM_DURATION;
import com.cannontech.common.util.JsonSerializers.TO_DURATION;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Interval {
    private final Integer sequenceNumber;
    private final Double heatSetpointC;
    private final Double coolSetpointC;
    private final Double heatDeltaC;
    private final Double coolDeltaC;
    private final Double load;
    private final Duration durationSeconds;
    
    @JsonCreator
    public Interval(@JsonProperty("sequenceNumber") Integer sequenceNumber,
                    @JsonProperty("heatSetpointC") Double heatSetpointC,
                    @JsonProperty("coolSetpointC") Double coolSetpointC,
                    @JsonProperty("heatDeltaC") Double heatDeltaC,
                    @JsonProperty("coolDeltaC") Double coolDeltaC,
                    @JsonProperty("load") Double load,
                    @JsonDeserialize(using=FROM_DURATION.class) @JsonProperty("durationSeconds") Duration durationSeconds) {
        
        this.sequenceNumber = sequenceNumber;
        this.heatSetpointC = heatSetpointC;
        this.coolSetpointC = coolSetpointC;
        this.heatDeltaC = heatDeltaC;
        this.coolDeltaC = coolDeltaC;
        this.load = load;
        this.durationSeconds = durationSeconds;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Double getHeatSetpointC() {
        return heatSetpointC;
    }

    public Double getCoolSetpointC() {
        return coolSetpointC;
    }

    public Double getHeatDeltaC() {
        return heatDeltaC;
    }

    public Double getCoolDeltaC() {
        return coolDeltaC;
    }

    public Double getLoad() {
        return load;
    }
    
    @JsonSerialize(using=TO_DURATION.class)
    public Duration getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coolDeltaC == null) ? 0 : coolDeltaC.hashCode());
        result = prime * result + ((coolSetpointC == null) ? 0 : coolSetpointC.hashCode());
        result = prime * result + ((durationSeconds == null) ? 0 : durationSeconds.hashCode());
        result = prime * result + ((heatDeltaC == null) ? 0 : heatDeltaC.hashCode());
        result = prime * result + ((heatSetpointC == null) ? 0 : heatSetpointC.hashCode());
        result = prime * result + ((load == null) ? 0 : load.hashCode());
        result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
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
        Interval other = (Interval) obj;
        if (coolDeltaC == null) {
            if (other.coolDeltaC != null) {
                return false;
            }
        } else if (!coolDeltaC.equals(other.coolDeltaC)) {
            return false;
        }
        if (coolSetpointC == null) {
            if (other.coolSetpointC != null) {
                return false;
            }
        } else if (!coolSetpointC.equals(other.coolSetpointC)) {
            return false;
        }
        if (durationSeconds == null) {
            if (other.durationSeconds != null) {
                return false;
            }
        } else if (!durationSeconds.equals(other.durationSeconds)) {
            return false;
        }
        if (heatDeltaC == null) {
            if (other.heatDeltaC != null) {
                return false;
            }
        } else if (!heatDeltaC.equals(other.heatDeltaC)) {
            return false;
        }
        if (heatSetpointC == null) {
            if (other.heatSetpointC != null) {
                return false;
            }
        } else if (!heatSetpointC.equals(other.heatSetpointC)) {
            return false;
        }
        if (load == null) {
            if (other.load != null) {
                return false;
            }
        } else if (!load.equals(other.load)) {
            return false;
        }
        if (sequenceNumber == null) {
            if (other.sequenceNumber != null) {
                return false;
            }
        } else if (!sequenceNumber.equals(other.sequenceNumber)) {
            return false;
        }
        return true;
    }
    
}
