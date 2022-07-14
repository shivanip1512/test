/*
package com.cannontech.common.schedules.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.holiday.DateOfHoliday;

public class HolidaySchedule implements DBPersistentConverter<com.cannontech.database.data.holiday.HolidaySchedule> {

    private int id;
    private String name;
    private List<Holiday> holidays;

    public HolidaySchedule() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Holiday> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void buildModel(com.cannontech.database.data.holiday.HolidaySchedule dbPersistanceSchedule) {
        setId(dbPersistanceSchedule.getHolidayScheduleID());
        setName(dbPersistanceSchedule.getHolidayScheduleName());
        if (CollectionUtils.isNotEmpty(dbPersistanceSchedule.getHolidayDatesVector())) {
            List<Holiday> holidays = new ArrayList<Holiday>();
            dbPersistanceSchedule.getHolidayDatesVector().stream().forEach(dbPersistanceHoliday -> {
                Holiday holiday = new Holiday();
                holiday.buildModel((DateOfHoliday) dbPersistanceHoliday);
                holidays.add(holiday);
            });
            setHolidays(holidays);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void buildDBPersistent(com.cannontech.database.data.holiday.HolidaySchedule dbPersistanceSchedule) {
        dbPersistanceSchedule.setHolidayScheduleName(getName());
        if (CollectionUtils.isNotEmpty(getHolidays())) {
            getHolidays().stream().forEach(holiday -> {
                DateOfHoliday dateOfHoliday = new DateOfHoliday();
                holiday.buildDBPersistent(dateOfHoliday);
                dbPersistanceSchedule.getHolidayDatesVector().add(dateOfHoliday);
            });
        }
    }
}
*/