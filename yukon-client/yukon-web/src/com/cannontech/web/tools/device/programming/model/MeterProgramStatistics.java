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

    public boolean displayDelete() {
        return programInfo.getGuid() != null && deviceTotal == 0;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
