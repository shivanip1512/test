package com.cannontech.amr.macsscheduler.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MacsTimeField {
    
    private int hours;
    private int minutes;
    private AmPmOptionEnum amPm;


    public enum AmPmOptionEnum {
        AM,
        PM;
    }


    public int getHours() {
        return hours;
    }


    public void setHours(int hours) {
        this.hours = hours;
    }


    public int getMinutes() {
        return minutes;
    }


    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }


    public AmPmOptionEnum getAmPm() {
        return amPm;
    }


    public void setAmPm(AmPmOptionEnum amPm) {
        this.amPm = amPm;
    }

    /**
     * Returns time in 00:00:00 format.
     */
    public String getTimeString() {
        NumberFormat formatter = new DecimalFormat("00");
        // 00:00:00 AM
        String timeString = formatter.format(getHours()) + ":" + formatter.format(getMinutes()) + ":00 " + getAmPm();
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss a");
        String dateTimeString = DateTime.now().toString("MM/dd/yyyy") + " " + timeString;
        DateTime date = dateFormatter.parseDateTime(dateTimeString);
        return date.toString("HH:mm:ss");
    }
    
    static MacsTimeField getTimeField(DateTime parsedDate){
        MacsTimeField timeField = new MacsTimeField();
        timeField.setAmPm(AmPmOptionEnum.valueOf(parsedDate.toString("a")));
        int hours = parsedDate.getHourOfDay();
        hours = hours > 12 ? hours - 12 : hours;
        hours = hours == 0 ? hours = 12 : hours;
        timeField.setHours(hours);
        timeField.setMinutes(parsedDate.getMinuteOfHour());
        return timeField;
    }
    
    static DateTime parseDate(int year, int month, int day, String time) {
        int maxDateOfTheCurrentMonth = new DateTime(year, month, 1, 0, 0, 0, 0).dayOfMonth().getMaximumValue();
        day = day > maxDateOfTheCurrentMonth ? maxDateOfTheCurrentMonth : day;
        String date = new DateTime(year, month, day, 00, 00, 00).toString("MM/dd/yyyy") + " " + time;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
        return formatter.parseDateTime(date);
    }
}
