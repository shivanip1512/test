package com.cannontech.amr.toggleProfiling.service.impl;

import java.util.Date;

public class RfnVoltageProfile {

    private Integer deviceID = null;
    private ProfilingStatus profilingStatus = ProfilingStatus.UNKNOWN;
    private Date enabledTill = null;
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

    public Date getEnabledTill() {
        return enabledTill;
    }

    public void setEnabledTill(Date enabledTill) {
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
