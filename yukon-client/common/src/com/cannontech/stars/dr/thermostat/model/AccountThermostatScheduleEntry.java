package com.cannontech.stars.dr.thermostat.model;

import java.util.Comparator;

import org.joda.time.LocalTime;

import com.cannontech.common.temperature.Temperature;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountThermostatScheduleEntry {

    private int accountThermostatScheduleEntryId = 0;
    private int accountThermostatScheduleId;
    // Conflicting setter definitions for property startTime cause Jackson to fail to deserialize this.
    // even though its supposed to globally ignore unknown properties which is why we need this annotation
    @JsonIgnore
    private Integer startTime;            //in seconds
	private TimeOfWeek timeOfWeek;
	private Temperature	coolTemp;
	private Temperature	heatTemp;

	public AccountThermostatScheduleEntry() {}

	/**
	 * Required Constructor for jackson unmarshalling
	 */
	@JsonCreator
    public AccountThermostatScheduleEntry(@JsonProperty("secondsFromMidnight") int startTime,
                                          @JsonProperty("timeOfWeek") TimeOfWeek timeOfWeek,
                                          @JsonProperty("cool_F") double coolTemp,
                                          @JsonProperty("heat_F") double heatTemp) {
	    this(startTime, timeOfWeek, 
	         Temperature.fromFahrenheit(coolTemp), 
	         Temperature.fromFahrenheit(heatTemp));
	}

	public AccountThermostatScheduleEntry(int startTime, TimeOfWeek timeOfWeek,
	                                      Temperature coolTemp, Temperature heatTemp) {
		this.startTime = startTime;
		this.timeOfWeek = timeOfWeek;
		this.coolTemp = coolTemp;
		this.heatTemp = heatTemp;
	}

	public AccountThermostatScheduleEntry(LocalTime startTime, TimeOfWeek timeOfWeek,
	                                      Temperature coolTemp, Temperature heatTemp){
	    setStartTime(startTime);
	    this.timeOfWeek = timeOfWeek;
	    this.coolTemp = coolTemp;
	    this.heatTemp = heatTemp;
	}

	public int getAccountThermostatScheduleEntryId() {
		return accountThermostatScheduleEntryId;
	}

	public void setAccountThermostatScheduleEntryId(int accountThermostatScheduleEntryId) {
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

	public Temperature getCoolTemp() {
		return coolTemp;
	}

	public void setCoolTemp(Temperature fahrenheitTemperature) {
		this.coolTemp = fahrenheitTemperature;
	}

	public Temperature getHeatTemp() {
		return heatTemp;
	}

	public void setHeatTemp(Temperature fahrenheitTemperature) {
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
                       return (Integer.valueOf(o1.getStartTime())).compareTo(Integer.valueOf(o2.getStartTime())); 
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
