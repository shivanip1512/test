package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.MACScheduleDao;
import com.cannontech.database.YukonJdbcTemplate;

public final class MACScheduleDaoImpl implements MACScheduleDao{
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    /**
     * This method takes a holiday schedule id and checks the MACSchedule table to see if any
     * mac schedules are using this holiday schedule. Returns true if so, otherwise false.
     */
    @Override
    public boolean usesHolidaySchedule(int holidayScheduleId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM macschedule");
        sql.append("WHERE holidayscheduleid").eq(holidayScheduleId);
        int count = jdbcTemplate.queryForInt(sql);
        return count > 0;
    }
}
