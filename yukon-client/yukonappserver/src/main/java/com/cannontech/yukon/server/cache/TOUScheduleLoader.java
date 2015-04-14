package com.cannontech.yukon.server.cache;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.spring.YukonSpringHook;

public class TOUScheduleLoader implements Runnable {
    private List<LiteTOUSchedule> allTOUSchedules = null;

    private static final YukonRowMapper<LiteTOUSchedule> liteTouScheduleRowMapper = new YukonRowMapper<LiteTOUSchedule>() {
        @Override
        public LiteTOUSchedule mapRow(YukonResultSet rs) throws SQLException {
            int scheduleId = rs.getInt("TOUScheduleID");
            String scheduleName = rs.getString("TOUScheduleName").trim();
            String defaultRate = rs.getString("TOUDefaultRate").trim();
            LiteTOUSchedule liteTOUSchedule = new LiteTOUSchedule(scheduleId, scheduleName, defaultRate);
            return liteTOUSchedule;
        };
    };
    
    public TOUScheduleLoader(List<LiteTOUSchedule> allTOUSchedules) {
        this.allTOUSchedules = allTOUSchedules;
    }

    @Override
    public void run() {
        Date timerStart = new Date();

        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TOUScheduleID, TOUScheduleName, TOUDefaultRate");
        sql.append("FROM TOUSchedule");
        sql.append("WHERE TOUScheduleId").gt(0);
        sql.append("ORDER BY TOUScheduleName");

        allTOUSchedules.addAll(jdbcTemplate.query(sql,  liteTouScheduleRowMapper));

        CTILogger.info((new Date().getTime() - timerStart.getTime()) * .001
                       + " Secs for TOUScheduleLoader (" + allTOUSchedules.size() + " loaded)");
    }
    
    public static LiteTOUSchedule getForId(int scheduleId) {
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TOUScheduleID, TOUScheduleName, TOUDefaultRate");
        sql.append("FROM TOUSchedule");
        sql.append("WHERE TOUScheduleId").eq(scheduleId);
        return jdbcTemplate.queryForObject(sql,  liteTouScheduleRowMapper);
    }
}