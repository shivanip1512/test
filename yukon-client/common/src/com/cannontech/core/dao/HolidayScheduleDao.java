package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.model.Holiday;
import com.cannontech.database.db.holiday.HolidaySchedule;

public interface HolidayScheduleDao {

    public List<Holiday> getHolidaysForSchedule(Integer scheduleId);
    
    public Integer getStrategyForPao(int paoId);
    
    public void saveHolidayScheduleStrategyAssigment(int paoId, int scheduleId, int strategyId);
    
    public HolidaySchedule getScheduleForPao(int paoId);
    
    public void saveDefaultHolidayScheduleStrategyAssigment(int paoId);
    
    public void deleteStrategyAssigment(int paoId);
    
    public HolidaySchedule[] getAllHolidaySchedules();

}
