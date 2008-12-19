package com.cannontech.loadcontrol.dao;

import java.util.Date;


public class LMProgramGearHistory {

	private int programGearhistoryId;
    private int programHistoryId;
    private String programName;
    private Date eventTime;
    private String action;
    private String userName;
    private String gearName;
    private int gearId;
    private String reason;
    
    public int getProgramGearhistoryId() {
		return programGearhistoryId;
	}
    public void setProgramGearhistoryId(int programGearhistoryId) {
		this.programGearhistoryId = programGearhistoryId;
	}
    public int getProgramHistoryId() {
		return programHistoryId;
	}
    public void setProgramHistoryId(int programHistoryId) {
		this.programHistoryId = programHistoryId;
	}
    public String getProgramName() {
		return programName;
	}
    public void setProgramName(String programName) {
		this.programName = programName;
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
