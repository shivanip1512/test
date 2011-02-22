package com.cannontech.stars.dr.thermostat.model;

import java.util.Comparator;

import org.joda.time.LocalTime;


public class AccountThermostatScheduleEntry {

	private int accountThermostatScheduleEntryId = 0;
	private int accountThermostatScheduleId;
	private Integer startTime;            //in seconds
	private TimeOfWeek timeOfWeek;
	private Integer	coolTemp;
	private Integer	heatTemp;
	
	public AccountThermostatScheduleEntry() {
	}

	public AccountThermostatScheduleEntry(int startTime, TimeOfWeek timeOfWeek, int coolTemp, int	heatTemp) {
		this.startTime = startTime;
		this.timeOfWeek = timeOfWeek;
		this.coolTemp = coolTemp;
		this.heatTemp = heatTemp;
	}
	
	public AccountThermostatScheduleEntry(LocalTime startTime, TimeOfWeek timeOfWeek, int coolTemp, int heatTemp){
	    setStartTime(startTime);
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
	public void setStartTime(LocalTime startTime){
	    this.startTime = startTime.getMillisOfDay() / 1000;
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
        return LocalTime.fromMillisOfDay(startTime*1000);
	}
	
	public Integer getStartTimeMinutes() {
	    if (startTime == null) {
	        return null;
	    }
	    return startTime / 60;
	}

	public boolean isEqualStartTimeAndTemps(AccountThermostatScheduleEntry other) {
		return getComparator().compare(this, other)==0;
	}
	
	public static Comparator<AccountThermostatScheduleEntry> getComparator(){
       Comparator<AccountThermostatScheduleEntry> atsEntryComparator = 
           new Comparator<AccountThermostatScheduleEntry>() {
               @Override
               public int compare(AccountThermostatScheduleEntry o1, AccountThermostatScheduleEntry o2) {
                   if(o1.getStartTime() != o2.getStartTime()){
                       return (new Integer(o1.getStartTime())).compareTo(new Integer(o2.getStartTime())); 
                   }
                   if(o1.getCoolTemp() != o2.getCoolTemp()){
                       return (new Integer(o1.getCoolTemp())).compareTo(new Integer(o2.getCoolTemp())); 
                   }
                   return (new Integer(o1.getHeatTemp())).compareTo(new Integer(o2.getHeatTemp())); 
               }
       };
       return atsEntryComparator;
   }
}
