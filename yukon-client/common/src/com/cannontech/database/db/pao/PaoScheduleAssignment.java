package com.cannontech.database.db.pao;

import java.sql.Timestamp;

public class PaoScheduleAssignment implements Comparable<PaoScheduleAssignment> {

	int eventId;
	int scheduleId;
	int paoId;
	String deviceName;
	String commandName;
	String scheduleName;
	int commandId;
	Timestamp lastRunTime;
	Timestamp nextRunTime;
	
	
	public PaoScheduleAssignment() {
		
	}
	
	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getPaoId() {
		return paoId;
	}

	public void setPaoId(int paoId) {
		this.paoId = paoId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String command) {
		this.commandName = command;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String sched) {
		this.scheduleName = sched;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public Timestamp getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(Timestamp lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	public Timestamp getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Timestamp nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	@Override
	public int compareTo(PaoScheduleAssignment o) {	
		int ret = scheduleName.compareTo(o.getScheduleName());
		
		if(ret == 0) {
			return commandName.compareTo(o.getCommandName());
		} else {
			return ret;
		}
	}
	
	
}
