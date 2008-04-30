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

    // Seconds from midnight
    private Integer startTime;
    private Integer temperature;

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

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Date getStartDate() {

        Date date = null;

        // Convert seconds from midnight into a date
        if (this.getStartTime() != null) {

            int totalMinutes = this.getStartTime() / 60;

            int hours = totalMinutes / 60;
            int minutes = totalMinutes - (hours * 60);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);

            date = calendar.getTime();
        }

        return date;
    }

    public void setStartDate(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Convert a date into seconds from midnight
        this.startTime = (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (calendar.get(Calendar.MINUTE) * 60);

    }

}
