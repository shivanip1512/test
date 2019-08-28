package com.cannontech.common.device.programming.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

/**
 * If guid came from Yukon and Yukon has the same guid in the meter program
 * table UI will display name for the program otherwise UI will display value of
 * the enum 
 * 
 * 	R Yukon programmed 
 * 	P Optically programmed 
 * 	N Newly programmed 
 * 	U Unprogrammed
 *
 *If the source is R and there is no name, that means that meter program guid doesn't not match guid for device in that case "Unknown Yukon program" is displayed.
 *
 *The timestamp might be available for cases P,N,U and should be displayed next to the description, if available. Example: Optically programmed (timestamp)
 */
public class MeterProgramConfiguration {
	private int deviceId;
	private String guid;
	private String name;
	private MeterProgramSource source;
	private Instant timestamp;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Instant getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public MeterProgramSource getSource() {
		return source;
	}
	public void setSource(MeterProgramSource source) {
		this.source = source;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
