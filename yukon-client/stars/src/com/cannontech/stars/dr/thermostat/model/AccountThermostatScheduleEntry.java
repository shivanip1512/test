package com.cannontech.stars.dr.thermostat.model;

import java.util.Comparator;

import net.sf.jsonOLD.JSONObject;

import org.joda.time.LocalTime;

import com.cannontech.common.temperature.FahrenheitTemperature;


public class AccountThermostatScheduleEntry {

	private int accountThermostatScheduleEntryId = 0;
	private int accountThermostatScheduleId;
	private Integer startTime;            //in seconds
	private TimeOfWeek timeOfWeek;
	private FahrenheitTemperature	coolTemp;
	private FahrenheitTemperature	heatTemp;
	
	public AccountThermostatScheduleEntry() {
	}
	
	public AccountThermostatScheduleEntry(JSONObject obj){
	    this.startTime = obj.getInt("startTime");
	    this.timeOfWeek = TimeOfWeek.valueOf(obj.getString("timeOfWeek"));
        this.coolTemp = new FahrenheitTemperature(obj.getDouble("coolTemp"));
        this.heatTemp = new FahrenheitTemperature(obj.getDouble("heatTemp"));
	}

	public AccountThermostatScheduleEntry(int startTime, TimeOfWeek timeOfWeek, FahrenheitTemperature coolTemp, FahrenheitTemperature	heatTemp) {
		this.startTime = startTime;
		this.timeOfWeek = timeOfWeek;
		this.coolTemp = coolTemp;
		this.heatTemp = heatTemp;
	}
	
	public AccountThermostatScheduleEntry(LocalTime startTime, TimeOfWeek timeOfWeek, FahrenheitTemperature coolTemp, FahrenheitTemperature heatTemp){
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
	public FahrenheitTemperature getCoolTemp() {
		return coolTemp;
	}
	public void setCoolTemp(FahrenheitTemperature fahrenheitTemperature) {
		this.coolTemp = fahrenheitTemperature;
	}
	public FahrenheitTemperature getHeatTemp() {
		return heatTemp;
	}
	public void setHeatTemp(FahrenheitTemperature fahrenheitTemperature) {
		this.heatTemp = fahrenheitTemperature;
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
                       return (o1.getCoolTemp()).compareTo(o2.getCoolTemp()); 
                   }
                   return (o1.getHeatTemp()).compareTo(o2.getHeatTemp()); 
               }
       };
       return atsEntryComparator;
   }
}
