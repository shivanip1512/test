package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.db.holiday.HolidaySchedule;

public interface HolidayScheduleDao {

    public Integer getStrategyForPao(int paoId);
    
    public void saveHolidayScheduleStrategyAssigment(int paoId, int scheduleId, int strategyId);
    
    public HolidaySchedule getScheduleForPao(int paoId);
    
    public void deleteStrategyAssigment(int paoId);
    
    public List<HolidaySchedule> getAllHolidaySchedules();

}
