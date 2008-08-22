package com.cannontech.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.core.dao.MACScheduleDao;

public final class MACScheduleDaoImpl implements MACScheduleDao{
    
    JdbcTemplate jdbcOps;
    
    /**
     * This method takes a holiday schedule id and checks the MACSchedule table to see if any 
     * mac schedules are using this holiday schedule.  Returns true if so, otherwise false.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean usesHolidaySchedule(Integer holidayScheduleId) {
        String sql = "SELECT ScheduleId FROM MACSCHEDULE WHERE HolidayScheduleId = " + holidayScheduleId;
        List<Integer> list = jdbcOps.queryForList(sql);
        if(list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    @Required
    public void setJdbcOps(JdbcTemplate jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

}
