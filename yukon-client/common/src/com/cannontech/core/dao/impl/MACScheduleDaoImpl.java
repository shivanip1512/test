package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.core.dao.MACScheduleDao;

public final class MACScheduleDaoImpl implements MACScheduleDao{
    
    private JdbcTemplate jdbcOps;
    
    /**
     * This method takes a holiday schedule id and checks the MACSchedule table to see if any 
     * mac schedules are using this holiday schedule.  Returns true if so, otherwise false.
     */
    @Override
    public boolean usesHolidaySchedule(int holidayScheduleId) {
        String sql = "select count(*) from macschedule where holidayscheduleid = ?";
        int count = jdbcOps.queryForInt(sql, holidayScheduleId);
        return count > 0; 
    }
    
    @Required
    public void setJdbcOps(JdbcTemplate jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

}
