package com.cannontech.web.tools.device.programming.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

public class MeterProgramWidgetDisplay {
	private int deviceId;
	private MeterProgramInfo programInfo;
	private Instant timestamp;

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public MeterProgramInfo getProgramInfo() {
		return programInfo;
	}

	public void setProgramInfo(MeterProgramInfo programInfo) {
		this.programInfo = programInfo;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
				+ System.getProperty("line.separator");
	}
}
