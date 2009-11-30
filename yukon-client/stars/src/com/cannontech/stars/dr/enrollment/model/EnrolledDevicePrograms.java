package com.cannontech.stars.dr.enrollment.model;

import java.util.List;

public class EnrolledDevicePrograms {
	
	private String serialNumber;
	private List<String> programNames;
	
	public EnrolledDevicePrograms(String serialNumber, List<String> programNames) {
		
		this.serialNumber = serialNumber;
		this.programNames = programNames;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public List<String> getProgramNames() {
		return programNames;
	}
}
