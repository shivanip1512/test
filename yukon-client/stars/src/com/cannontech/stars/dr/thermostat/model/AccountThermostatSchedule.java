package com.cannontech.stars.dr.thermostat.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.google.common.collect.Lists;

public class AccountThermostatSchedule {

	private int accountThermostatScheduleId = 0;
	private int accountId;
	private String scheduleName;
	private SchedulableThermostatType thermostatType;
	private ThermostatScheduleMode thermostatScheduleMode;
	private List<AccountThermostatScheduleEntry> scheduleEntries = Lists.newArrayList();
	
	public int getAccountThermostatScheduleId() {
		return accountThermostatScheduleId;
	}

	public void setAccountThermostatScheduleId(int accountThermostatScheduleId) {
		this.accountThermostatScheduleId = accountThermostatScheduleId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public SchedulableThermostatType getThermostatType() {
		return thermostatType;
	}

	public void setThermostatType(SchedulableThermostatType thermostatType) {
		this.thermostatType = thermostatType;
	}

	public ThermostatScheduleMode getThermostatScheduleMode() {
		return thermostatScheduleMode;
	}

	public void setThermostatScheduleMode(ThermostatScheduleMode thermostatScheduleMode) {
		this.thermostatScheduleMode = thermostatScheduleMode;
	}
	
	public List<AccountThermostatScheduleEntry> getScheduleEntries() {
		return scheduleEntries;
	}
	
	public void setScheduleEntries(List<AccountThermostatScheduleEntry> scheduleEntries) {
		this.scheduleEntries = scheduleEntries;
	}
	
	public Map<TimeOfWeek, List<AccountThermostatScheduleEntry>> getEntriesByTimeOfWeekMap() {

		// a bit more complicated than just creating a ArrayListMultimap, but to maintain consistency of order, we want the keys to ordered
		Map<TimeOfWeek, List<AccountThermostatScheduleEntry>> x = new LinkedHashMap<TimeOfWeek, List<AccountThermostatScheduleEntry>>();
		for (TimeOfWeek timeOfWeek : TimeOfWeek.values()) {
			for (AccountThermostatScheduleEntry atsEntry : this.getScheduleEntries()) {
				if (atsEntry.getTimeOfWeek() == timeOfWeek) {
					
					if (!x.containsKey(timeOfWeek)) {
						x.put(timeOfWeek, new ArrayList<AccountThermostatScheduleEntry>());
					}
					x.get(timeOfWeek).add(atsEntry);
				}
	    	}
		}
    	
    	return x;
	}
}
