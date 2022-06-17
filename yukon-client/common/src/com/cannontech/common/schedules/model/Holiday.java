package com.cannontech.common.schedules.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.holiday.DateOfHoliday;

public class Holiday implements DBPersistentConverter<DateOfHoliday> {

    private String name;
    private Integer day = 1;
    private Integer month = 1;
    private Integer year = -1;

    public Holiday() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public void buildModel(DateOfHoliday dateOfHoliday) {
        setName(dateOfHoliday.getHolidayName());
        setDay(dateOfHoliday.getHolidayDay());
        setMonth(dateOfHoliday.getHolidayMonth());
        setYear(dateOfHoliday.getHolidayYear());
    }

    @Override
    public void buildDBPersistent(DateOfHoliday dateOfHoliday) {
        dateOfHoliday.setHolidayName(getName());
        dateOfHoliday.setHolidayDay(getDay());
        dateOfHoliday.setHolidayMonth(getMonth());
        dateOfHoliday.setHolidayYear(getYear());
    }
}