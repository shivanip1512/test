package com.cannontech.amr.macsscheduler.model;

import java.text.MessageFormat;
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
     * Returns time in 24 hour HH:mm:00 format as required by MACS messaging.
     */
    public String getTimeString() {
        int hours = getHours() % 12;
        
        if (getAmPm() == AmPmOptionEnum.PM) {
            hours += 12;
        }

        return MessageFormat.format("{0,number,00}:{1,number,00}:00", hours, getMinutes());
    }
    
    static MacsTimeField getTimeField(DateTime parsedDate){
        MacsTimeField timeField = new MacsTimeField();
        timeField.setAmPm(AmPmOptionEnum.valueOf(parsedDate.toString("a")));
        int hours = parsedDate.getHourOfDay();
        hours = (hours + 11) % 12 + 1;
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
