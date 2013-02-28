package com.cannontech.common.device.config.model.tou;

import java.util.ArrayList;
import java.util.List;

public class Tou {

    private List<Schedule> scheduleList = new ArrayList<Schedule>();

    private String defaultRate = null;
    private String monday = null;
    private String tuesday = null;
    private String wednesday = null;
    private String thursday = null;
    private String friday = null;
    private String saturday = null;
    private String sunday = null;
    private String holiday = null;

    public Tou() {
        this(5);
    }

    public Tou(final int numberOfTimeRate) {
        for (int i = 0; i < 4; i++) {
            scheduleList.add(new Schedule(numberOfTimeRate));
        }
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public String getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(String defaultRate) {
        this.defaultRate = defaultRate;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

}
