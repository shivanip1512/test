package com.cannontech.common.device.programming.model;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;

public class MeterProgram {
	private UUID guid;
	private String name;
	private PaoType paoType;
	private byte[] program;
	public PaoType getPaoType() {
		return paoType;
	}
	public void setPaoType(PaoType paoType) {
		this.paoType = paoType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getProgram() {
		return program;
	}
	public void setProgram(byte[] program) {
		this.program = program;
	}
	public UUID getGuid() {
		return guid;
	}
	public void setGuid(UUID guid) {
		this.guid = guid;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
				+ System.getProperty("line.separator");
	}
}
