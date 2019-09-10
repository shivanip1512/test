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

    public boolean displayCancel() {
        return status == DisplayableStatus.IN_PROGRESS;
    }

    public boolean displayRead() {
        return status == DisplayableStatus.CONFIRMING || programInfo.getSource() == MeterProgramSource.OLD_FIRMWARE;
    }

    public boolean displaySend() {
        return status == DisplayableStatus.FAILURE && programInfo.getSource() != MeterProgramSource.OLD_FIRMWARE;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE) + System.getProperty("line.separator");
    }
}