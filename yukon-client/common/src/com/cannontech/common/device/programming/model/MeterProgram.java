package com.cannontech.common.device.programming.model;

import com.cannontech.common.pao.PaoType;

public class MeterProgram {
	private String guid;
	private String name;
	private PaoType paoType;
	private String program;
	public PaoType getPaoType() {
		return paoType;
	}
	public void setPaoType(PaoType paoType) {
		this.paoType = paoType;
	}

	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
}
