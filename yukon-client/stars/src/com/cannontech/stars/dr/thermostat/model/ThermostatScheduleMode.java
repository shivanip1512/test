package com.cannontech.stars.dr.thermostat.model;

import java.util.Set;

import com.google.common.collect.Sets;


/**
 * Enum which represents thermostat schedule mode
 */
public enum ThermostatScheduleMode {
	
    ALL(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY)),
    WEEKDAY_SAT_SUN(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY, TimeOfWeek.SATURDAY, TimeOfWeek.SUNDAY)),
    WEEKDAY_WEEKEND(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY, TimeOfWeek.WEEKEND)),
    SEVEN_DAY(Sets.immutableEnumSet(TimeOfWeek.MONDAY, TimeOfWeek.TUESDAY, TimeOfWeek.WEDNESDAY, TimeOfWeek.THURSDAY, TimeOfWeek.FRIDAY, TimeOfWeek.SATURDAY, TimeOfWeek.SUNDAY));

    private Set<TimeOfWeek> associatedTimeOfWeeks;
    
    ThermostatScheduleMode(Set<TimeOfWeek> associatedTimeofWeeks) {
    	this.associatedTimeOfWeeks = associatedTimeofWeeks;
    }
    
    public Set<TimeOfWeek> getAssociatedTimeOfWeeks() {
		return associatedTimeOfWeeks;
	}
}
