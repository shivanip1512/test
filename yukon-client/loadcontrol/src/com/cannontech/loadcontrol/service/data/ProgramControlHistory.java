package com.cannontech.loadcontrol.service.data;

import java.util.Date;

public class ProgramControlHistory {

	private String programName;
	private Date startDateTime;
	private Date stopDateTime;
	private String gearName;
	
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public Date getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}
	public Date getStopDateTime() {
		return stopDateTime;
	}
	public void setStopDateTime(Date stopDateTime) {
		this.stopDateTime = stopDateTime;
	}
	public String getGearName() {
		return gearName;
	}
	public void setGearName(String gearName) {
		this.gearName = gearName;
	}
	
	
}
