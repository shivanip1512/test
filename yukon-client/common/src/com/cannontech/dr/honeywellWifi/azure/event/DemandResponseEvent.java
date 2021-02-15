package com.cannontech.dr.honeywellWifi.azure.event;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.JsonSerializers.FROM_DATE_HONEYWELL;
import com.cannontech.common.util.JsonSerializers.FROM_PHASE;
import com.cannontech.common.util.JsonSerializers.TO_DATE_HONEYWELL;
import com.cannontech.common.util.JsonSerializers.TO_PHASE;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Honeywell Azure service bus demand response event. Contains info about what the device is doing in a DR event.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DemandResponseEvent extends AbstractHoneywellWifiData {
    private final Integer demandResponseId;
    private final EventPhase phase;
    private final Boolean optedOut;
    private final Double heatSetpointLimit;
    private final Double coolSetpointLimit;
    private final List<Interval> intervals;
    private final Instant startTime;
    private final Integer deviceId;
    private final String macId;
    
    @JsonCreator
    public DemandResponseEvent(@JsonProperty("demandResponseId") Integer demandResponseId,
                               @JsonDeserialize(using=FROM_PHASE.class) @JsonProperty("phase") EventPhase phase,
                               @JsonProperty("optedOut") Boolean optedOut,
                               @JsonProperty("heatSetpointLimit") Double heatSetpointLimit,
                               @JsonProperty("coolSetpointLimit") Double coolSetpointLimit,
                               @JsonProperty("intervals") List<Interval> intervals,
                               @JsonDeserialize(using=FROM_DATE_HONEYWELL.class) @JsonProperty("startTime") Instant startTime,
                               @JsonProperty("deviceId") Integer deviceId,
                               @JsonProperty("macId") String macId) {
        
        this.demandResponseId = demandResponseId;
        this.phase = phase;
        this.optedOut = optedOut;
        this.heatSetpointLimit = heatSetpointLimit;
        this.coolSetpointLimit = coolSetpointLimit;
        this.intervals = intervals;
        this.startTime = startTime;
        this.deviceId = deviceId;
        this.macId = macId;
    }
    
    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.DEMAND_RESPONSE_EVENT;
    }

    public Integer getDemandResponseId() {
        return demandResponseId;
    }

    @JsonSerialize(using=TO_PHASE.class)
    public EventPhase getPhase() {
        return phase;
    }

    public Boolean getOptedOut() {
        return optedOut;
    }

    public Double getHeatSetpointLimit() {
        return heatSetpointLimit;
    }

    public Double getCoolSetpointLimit() {
        return coolSetpointLimit;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    @JsonSerialize(using=TO_DATE_HONEYWELL.class)
    public Instant getStartTime() {
        return startTime;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    @Override
    public String getMacId() {
        return macId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coolSetpointLimit == null) ? 0 : coolSetpointLimit.hashCode());
        result = prime * result + ((demandResponseId == null) ? 0 : demandResponseId.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        result = prime * result + ((heatSetpointLimit == null) ? 0 : heatSetpointLimit.hashCode());
        result = prime * result + ((intervals == null) ? 0 : intervals.hashCode());
        result = prime * result + ((macId == null) ? 0 : macId.hashCode());
        result = prime * result + ((optedOut == null) ? 0 : optedOut.hashCode());
        result = prime * result + ((phase == null) ? 0 : phase.hashCode());
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
        DemandResponseEvent other = (DemandResponseEvent) obj;
        if (coolSetpointLimit == null) {
            if (other.coolSetpointLimit != null) {
                return false;
            }
        } else if (!coolSetpointLimit.equals(other.coolSetpointLimit)) {
            return false;
        }
        if (demandResponseId == null) {
            if (other.demandResponseId != null) {
                return false;
            }
        } else if (!demandResponseId.equals(other.demandResponseId)) {
            return false;
        }
        if (deviceId == null) {
            if (other.deviceId != null) {
                return false;
            }
        } else if (!deviceId.equals(other.deviceId)) {
            return false;
        }
        if (heatSetpointLimit == null) {
            if (other.heatSetpointLimit != null) {
                return false;
            }
        } else if (!heatSetpointLimit.equals(other.heatSetpointLimit)) {
            return false;
        }
        if (intervals == null) {
            if (other.intervals != null) {
                return false;
            }
        } else if (!intervals.equals(other.intervals)) {
            return false;
        }
        if (macId == null) {
            if (other.macId != null) {
                return false;
            }
        } else if (!macId.equals(other.macId)) {
            return false;
        }
        if (optedOut == null) {
            if (other.optedOut != null) {
                return false;
            }
        } else if (!optedOut.equals(other.optedOut)) {
            return false;
        }
        if (phase == null) {
            if (other.phase != null) {
                return false;
            }
        } else if (!phase.equals(other.phase)) {
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
    
}
