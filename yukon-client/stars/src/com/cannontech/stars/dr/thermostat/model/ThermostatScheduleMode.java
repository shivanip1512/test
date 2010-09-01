package com.cannontech.stars.dr.thermostat.model;

import java.util.Set;

import com.google.common.collect.Sets;


/**
 * Enum which represents thermostat schedule mode
 */
public enum ThermostatScheduleMode {
	
    ALL(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY)),
    WEEKDAY_SAT_SUN(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY, TimeOfWeek.SATURDAY, TimeOfWeek.SUNDAY)),
    WEEKDAY_WEEKEND(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY, TimeOfWeek.WEEKEND));
    //ANY NEW MODES SHOULD HAVE A CORRESPONDING SECTION IN 
    //SchedulableThermostatType.getDefaultAccountThermostatSchedule()
    
    private Set<TimeOfWeek> associatedTimeOfWeeks;
    
    ThermostatScheduleMode(Set<TimeOfWeek> associtaedTimeofWeeks) {
    	this.associatedTimeOfWeeks = associtaedTimeofWeeks;
    }
    
    public Set<TimeOfWeek> getAssociatedTimeOfWeeks() {
		return associatedTimeOfWeeks;
	}
}
