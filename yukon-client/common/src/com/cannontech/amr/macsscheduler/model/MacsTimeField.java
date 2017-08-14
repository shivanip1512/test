package com.cannontech.amr.macsscheduler.model;

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

}
