package com.cannontech.stars.dr.thermostat.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class AccountThermostatSchedule {

	private int accountThermostatScheduleId = -1;
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
	
	public void addScheduleEntries(List<AccountThermostatScheduleEntry> scheduleEntries) {
	    this.scheduleEntries.addAll(scheduleEntries);
	}
	
	public ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> getEntriesByTimeOfWeekMultimap() {
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> result = ArrayListMultimap.create();
        for (AccountThermostatScheduleEntry atsEntry : this.getScheduleEntries()) {
            result.put(atsEntry.getTimeOfWeek(), atsEntry);
        }
        return result;
    }
	
	public Map<TimeOfWeek, Collection<AccountThermostatScheduleEntry>> getEntriesByTimeOfWeekMultimapAsMap() {
	    Map<TimeOfWeek, Collection<AccountThermostatScheduleEntry>> result = new LinkedHashMap<TimeOfWeek, Collection<AccountThermostatScheduleEntry>>();
	    ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> multimap =  getEntriesByTimeOfWeekMultimap();
	    TimeOfWeek days[] = TimeOfWeek.values();

	    for (TimeOfWeek day : days) {
	        if(multimap.containsKey(day)){
	            result.put(day, multimap.get(day));
	        }
	    }
	    
	    return result;
    }

}
