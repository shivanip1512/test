package com.cannontech.stars.dr.thermostat.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Model object which represents a schedule season entry for a thermostat
 */
public class ThermostatSeasonEntry {

    private Integer id;
    private Integer seasonId;
    private TimeOfWeek timeOfWeek;

    // Minutes from midnight
    private Integer startTime;
    private Integer coolTemperature;
    private Integer heatTemperature;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }

    public TimeOfWeek getTimeOfWeek() {
        return timeOfWeek;
    }

    public void setTimeOfWeek(TimeOfWeek timeOfWeek) {
        this.timeOfWeek = timeOfWeek;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getCoolTemperature() {
        return coolTemperature;
    }

    public void setCoolTemperature(Integer coolTemperature) {
        this.coolTemperature = coolTemperature;
    }
    
    public Integer getHeatTemperature() {
		return heatTemperature;
	}
    
    public void setHeatTemperature(Integer heatTemperature) {
		this.heatTemperature = heatTemperature;
	}

    public Date getStartDate() {

        Date date = null;

        // Convert minutes from midnight into a date
        if (this.getStartTime() != null) {

            int totalMinutes = this.getStartTime();

            int hours = totalMinutes / 60;
            int minutes = totalMinutes - (hours * 60);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);

            date = calendar.getTime();
        }

        return date;
    }

    // This is a temporary workaround which is intended to be replaced with
    // more majors changes (see YUK-7069).
	public boolean equalsIgnoringTimeOfWeek(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThermostatSeasonEntry other = (ThermostatSeasonEntry) obj;
		if (coolTemperature == null) {
			if (other.coolTemperature != null)
				return false;
		} else if (!coolTemperature.equals(other.coolTemperature))
			return false;
		if (heatTemperature == null) {
			if (other.heatTemperature != null)
				return false;
		} else if (!heatTemperature.equals(other.heatTemperature))
			return false;
		if (seasonId == null) {
			if (other.seasonId != null)
				return false;
		} else if (!seasonId.equals(other.seasonId))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}
}
