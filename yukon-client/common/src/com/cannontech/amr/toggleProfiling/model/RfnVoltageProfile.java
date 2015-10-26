package com.cannontech.amr.toggleProfiling.model;

import org.joda.time.Instant;



public class RfnVoltageProfile {

    private Integer deviceID = null;
    private ProfilingStatus profilingStatus = ProfilingStatus.UNKNOWN;
    private Instant enabledTill = null;
    private long voltageProfilingRate = 0;

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

    public Instant getEnabledTill() {
        return enabledTill;
    }

    public void setEnabledTill(Instant enabledTill) {
        this.enabledTill = enabledTill;
    }
    
    public long getVoltageProfilingRate() {
        return voltageProfilingRate;
    }

    public void setVoltageProfilingRate(long voltageProfilingRate) {
        this.voltageProfilingRate = voltageProfilingRate;
    }

    public ProfilingStatus getProfilingStatus() {
        return profilingStatus;
    }

    public void setProfilingStatus(ProfilingStatus profilingStatus) {
        this.profilingStatus = profilingStatus;
    }

}
