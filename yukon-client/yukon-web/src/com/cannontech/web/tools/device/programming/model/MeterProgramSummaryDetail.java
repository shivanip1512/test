package com.cannontech.web.tools.device.programming.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.device.programming.model.MeterProgramSource;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter.DisplayableStatus;

public class MeterProgramSummaryDetail {
    private DisplayableDevice device;
    private String meterNumber;
    private MeterProgramInfo programInfo;
    private DisplayableStatus status;
    private DeviceError error;
    private Instant lastUpdate;
    private String assignedProgramName;
    private String assignedGuid;

    public DisplayableDevice getDevice() {
        return device;
    }

    public void setDevice(DisplayableDevice device) {
        this.device = device;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public MeterProgramInfo getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(MeterProgramInfo programInfo) {
        this.programInfo = programInfo;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public DisplayableStatus getStatus() {
        return status;
    }

    public void setStatus(DisplayableStatus status) {
        this.status = status;
    }

    public DeviceError getError() {
        return error;
    }

    public void setError(DeviceError error) {
        this.error = error;
    }
    
    public String getAssignedProgramName() {
        return assignedProgramName;
    }

    public void setAssignedProgramName(String assignedProgramName) {
        this.assignedProgramName = assignedProgramName;
    }

    public String getAssignedGuid() {
        return assignedGuid;
    }

    public void setAssignedGuid(String assignedGuid) {
        this.assignedGuid = assignedGuid;
    }

    public boolean displayCancel() {
        return status == DisplayableStatus.IN_PROGRESS && programInfo.getSource().isActionable();
    }
    
    public boolean displayProgressBar() {
        return status == DisplayableStatus.IN_PROGRESS;
    }

    public boolean displayRead() {
        return (status == DisplayableStatus.CONFIRMING &&  programInfo.getSource().isActionable()) ||  programInfo.getSource() == MeterProgramSource.OLD_FIRMWARE ;
    }

    public boolean displaySend() {
        return status == DisplayableStatus.FAILURE && programInfo.getSource().isActionable();
    }
    
    public boolean displayAccept() {
        return status == DisplayableStatus.FAILURE && programInfo.getSource().isActionable();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
