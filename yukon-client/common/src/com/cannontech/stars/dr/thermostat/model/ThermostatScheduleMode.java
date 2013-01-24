package com.cannontech.stars.dr.thermostat.model;

import java.util.Set;

import com.cannontech.common.util.xml.XmlRepresentation;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.google.common.collect.Sets;


/**
 * Enum which represents thermostat schedule mode
 */
public enum ThermostatScheduleMode {
	
    ALL(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY),
        YukonRoleProperty.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL),
    @XmlRepresentation("Weekday Saturday Sunday")
    WEEKDAY_SAT_SUN(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY, TimeOfWeek.SATURDAY, TimeOfWeek.SUNDAY),
                    YukonRoleProperty.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY),
    WEEKDAY_WEEKEND(Sets.immutableEnumSet(TimeOfWeek.WEEKDAY, TimeOfWeek.WEEKEND),
                    YukonRoleProperty.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND),
    SEVEN_DAY(Sets.immutableEnumSet(TimeOfWeek.MONDAY, TimeOfWeek.TUESDAY, TimeOfWeek.WEDNESDAY, TimeOfWeek.THURSDAY, TimeOfWeek.FRIDAY, TimeOfWeek.SATURDAY, TimeOfWeek.SUNDAY),
              YukonRoleProperty.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_7_DAY);

    private Set<TimeOfWeek> associatedTimeOfWeeks;
    private YukonRoleProperty associatedRoleProperty;
    
    ThermostatScheduleMode(Set<TimeOfWeek> associatedTimeofWeeks, YukonRoleProperty associatedRoleProperty) {
    	this.associatedTimeOfWeeks = associatedTimeofWeeks;
    	this.associatedRoleProperty = associatedRoleProperty;
    }
    
    public Set<TimeOfWeek> getAssociatedTimeOfWeeks() {
		return associatedTimeOfWeeks;
	}
    
    public YukonRoleProperty getAssociatedRoleProperty() {
        return associatedRoleProperty;
    }
}
