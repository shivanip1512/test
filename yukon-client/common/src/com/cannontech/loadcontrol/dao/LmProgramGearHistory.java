package com.cannontech.loadcontrol.dao;

import java.util.Date;


public class LmProgramGearHistory {

	private int programId;
	private String programName;
	
	private int programGearHistoryId;
    private int programHistoryId;
    private Date eventTime;
    private String action;
    private String userName;
    private String gearName;
    private int gearId;
    private String reason;
    
    public int getProgramId() {
    	return programId;
    }
    public void setProgramId(int programId) {
    	this.programId = programId;
    }
    public String getProgramName() {
    	return programName;
    }
    public void setProgramName(String programName) {
    	this.programName = programName;
    }
    
    public int getProgramGearHistoryId() {
		return programGearHistoryId;
	}
    public void setProgramGearHistoryId(int programGearHistoryId) {
		this.programGearHistoryId = programGearHistoryId;
	}
    public int getProgramHistoryId() {
		return programHistoryId;
	}
    public void setProgramHistoryId(int programHistoryId) {
		this.programHistoryId = programHistoryId;
	}
	public Date getEventTime() {
		return eventTime;
	}
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getGearName() {
		return gearName;
	}
	public void setGearName(String gearName) {
		this.gearName = gearName;
	}
	public int getGearId() {
		return gearId;
	}
	public void setGearId(int gearId) {
		this.gearId = gearId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
    
}
