package com.cannontech.yukon.server.cache;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteHolidaySchedule;
import com.cannontech.spring.YukonSpringHook;

public class HolidayScheduleLoader implements Runnable {
    private List<LiteHolidaySchedule> allHolidaySchedules = null;

    public HolidayScheduleLoader(List<LiteHolidaySchedule> allHolidaySchedules) {
        this.allHolidaySchedules = allHolidaySchedules;
    }

    private static final YukonRowMapper<LiteHolidaySchedule> liteHolidayScheduleRowMapper = new YukonRowMapper<LiteHolidaySchedule>() {
        @Override
        public LiteHolidaySchedule mapRow(YukonResultSet rs) throws SQLException {
            int holidayScheduleID = rs.getInt("HolidayScheduleId");
            String holidayScheduleName = rs.getString("HolidayScheduleName").trim();

            LiteHolidaySchedule liteHolidaySchedule = new LiteHolidaySchedule(holidayScheduleID, holidayScheduleName);
            return liteHolidaySchedule;
        };
    }; 
    @Override
    public void run() {
        Date timerStart = new Date();
        
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT HolidayScheduleId, HolidayScheduleName");
        sql.append("FROM HolidaySchedule");
        sql.append("WHERE HolidayScheduleId").gte(0);
        sql.append("ORDER BY HolidayScheduleName");

        allHolidaySchedules.addAll(jdbcTemplate.query(sql, liteHolidayScheduleRowMapper));

        CTILogger.info((new Date().getTime() - timerStart.getTime()) * .001
                       + " Secs for HolidayScheduleLoader (" + allHolidaySchedules.size() + " loaded)");
    }
    
    public static LiteHolidaySchedule getForId(int holidayScheduleId) {
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT HolidayScheduleId, HolidayScheduleName");
        sql.append("FROM HolidaySchedule");
        sql.append("WHERE HolidayScheduleId").eq(holidayScheduleId);

        return jdbcTemplate.queryForObject(sql, liteHolidayScheduleRowMapper);
    }
}
