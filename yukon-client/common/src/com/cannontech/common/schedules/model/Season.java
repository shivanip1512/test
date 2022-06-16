package com.cannontech.common.schedules.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.season.DateOfSeason;

public class Season implements DBPersistentConverter<DateOfSeason> {
    private String name;
    private Integer startMonth;
    private Integer startDay;
    private Integer endMonth;
    private Integer endDay;

    public Season() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
    }

    public Integer getEndDay() {
        return endDay;
    }

    public void setEndDay(Integer endDay) {
        this.endDay = endDay;
    }

    @Override
    public void buildModel(DateOfSeason dateOfSeason) {
        setName(dateOfSeason.getSeasonName());
        setStartDay(dateOfSeason.getSeasonStartDay());
        setStartMonth(dateOfSeason.getSeasonStartMonth());
        setEndDay(dateOfSeason.getSeasonEndDay());
        setEndMonth(dateOfSeason.getSeasonEndMonth());

    }

    @Override
    public void buildDBPersistent(DateOfSeason dateOfSeason) {
        dateOfSeason.setSeasonName(getName());
        dateOfSeason.setSeasonStartDay(getStartDay());
        dateOfSeason.setSeasonStartMonth(getStartMonth());
        dateOfSeason.setSeasonEndDay(getEndDay());
        dateOfSeason.setSeasonEndMonth(getEndMonth());
    }

}
