package com.cannontech.amr.toggleProfiling.model;

import org.joda.time.Duration;
import org.joda.time.Instant;



public class RfnVoltageProfile {

    private Integer deviceID;
    private ProfilingStatus profilingStatus = ProfilingStatus.UNKNOWN;
    private Instant stopDate;
    private Duration voltageProfilingRate;

    public enum ProfilingStatus {
        ENABLED,
        DISABLED,
        UNKNOWN
    }
    public Integer getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(Integer deviceID) {
        this.deviceID = deviceID;
    }

    public Instant getStopDate() {
        return stopDate;
    }

    public void setStopDate(Instant stopDate) {
        this.stopDate = stopDate;
    }
    
    public Duration getVoltageProfilingRate() {
        return voltageProfilingRate;
    }

    public void setVoltageProfilingRate(Duration voltageProfilingRate) {
        this.voltageProfilingRate = voltageProfilingRate;
    }

    public ProfilingStatus getProfilingStatus() {
        return profilingStatus;
    }

    public void setProfilingStatus(ProfilingStatus profilingStatus) {
        this.profilingStatus = profilingStatus;
    }

}
