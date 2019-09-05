package com.cannontech.common.device.programming.model;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.amr.errors.dao.DeviceError;

public class MeterProgramStatus {
	private int deviceId;
	private UUID reportedGuid;
	private ProgrammingStatus status;
	private MeterProgramSource source;
	private Instant lastUpdate;
	private DeviceError error;
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public ProgrammingStatus getStatus() {
		return status;
	}
	public void setStatus(ProgrammingStatus status) {
		this.status = status;
	}
	public UUID getReportedGuid() {
		return reportedGuid;
	}
	public void setReportedGuid(UUID reportedGuid) {
		this.reportedGuid = reportedGuid;
	}
	public Instant getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Instant lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public MeterProgramSource getSource() {
		return source;
	}
	public void setSource(MeterProgramSource source) {
		this.source = source;
	}
	public DeviceError getError() {
		return error;
	}
	public void setError(DeviceError error) {
		this.error = error;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
				+ System.getProperty("line.separator");
	}
}
