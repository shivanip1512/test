package com.cannontech.web.tools.device.programming.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MeterProgramStatistics {
    private MeterProgramInfo programInfo;
    private int deviceTotal;
    private int inProgressTotal;

    public MeterProgramInfo getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(MeterProgramInfo programInfo) {
        this.programInfo = programInfo;
    }

    public int getDeviceTotal() {
        return deviceTotal;
    }

    public void setDeviceTotal(int deviceTotal) {
        this.deviceTotal = deviceTotal;
    }

    public int getInProgressTotal() {
        return inProgressTotal;
    }

    public void setInProgressTotal(int inProgressTotal) {
        this.inProgressTotal = inProgressTotal;
    }

    public boolean isUnused() {
        return programInfo.getGuid() != null && deviceTotal == 0 && inProgressTotal == 0 ;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + deviceTotal;
        result = prime * result + inProgressTotal;
        result = prime * result + ((programInfo == null) ? 0 : programInfo.hashCode());
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
        MeterProgramStatistics other = (MeterProgramStatistics) obj;
        if (deviceTotal != other.deviceTotal)
            return false;
        if (inProgressTotal != other.inProgressTotal)
            return false;
        if (programInfo == null) {
            if (other.programInfo != null)
                return false;
        } else if (!programInfo.equals(other.programInfo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
