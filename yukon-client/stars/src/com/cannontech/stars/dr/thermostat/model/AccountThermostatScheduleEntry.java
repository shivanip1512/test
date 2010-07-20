package com.cannontech.stars.dr.thermostat.model;

import org.joda.time.LocalTime;


public class AccountThermostatScheduleEntry {

	private int accountThermostatScheduleEntryId = 0;
	private int accountThermostatScheduleId;
	private int startTime;
	private TimeOfWeek timeOfWeek;
	private int	coolTemp;
	private int	heatTemp;
	
	public AccountThermostatScheduleEntry() {
	}

	public AccountThermostatScheduleEntry(int startTime, TimeOfWeek timeOfWeek, int coolTemp, int	heatTemp) {
		
		this.startTime = startTime;
		this.timeOfWeek = timeOfWeek;
		this.coolTemp = coolTemp;
		this.heatTemp = heatTemp;
	}
	
	public int getAccountThermostatScheduleEntryId() {
		return accountThermostatScheduleEntryId;
	}
	public void setAccountThermostatScheduleEntryId(
			int accountThermostatScheduleEntryId) {
		this.accountThermostatScheduleEntryId = accountThermostatScheduleEntryId;
	}
	public int getAccountThermostatScheduleId() {
		return accountThermostatScheduleId;
	}
	public void setAccountThermostatScheduleId(int accountThermostatScheduleId) {
		this.accountThermostatScheduleId = accountThermostatScheduleId;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public TimeOfWeek getTimeOfWeek() {
		return timeOfWeek;
	}
	public void setTimeOfWeek(TimeOfWeek timeOfWeek) {
		this.timeOfWeek = timeOfWeek;
	}
	public int getCoolTemp() {
		return coolTemp;
	}
	public void setCoolTemp(int coolTemp) {
		this.coolTemp = coolTemp;
	}
	public int getHeatTemp() {
		return heatTemp;
	}
	public void setHeatTemp(int heatTemp) {
		this.heatTemp = heatTemp;
	}
	
	public LocalTime getStartTimeLocalTime() {
		
		int startMinutes = startTime / 60;
        int startTimeHours = startMinutes / 60;
        int startTimeMinutes = startMinutes % 60;
        if(startTimeMinutes < 0) {
        	startTimeMinutes = 0;
        }
        return new LocalTime(startTimeHours, startTimeMinutes);
	}
	
	public int getStartTimeMinutes() {
		
		return startTime / 60;
	}

	public boolean isEqualStartTimeAndTemps(AccountThermostatScheduleEntry other) {
		
		if (this.getStartTime() == other.getStartTime() &&
			this.getCoolTemp() == other.getCoolTemp() &&
			this.getHeatTemp() == other.getHeatTemp()) {
			return true;
		}
		
		return false;
	}
	
	
}
