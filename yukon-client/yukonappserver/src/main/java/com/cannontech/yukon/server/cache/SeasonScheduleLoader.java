package com.cannontech.yukon.server.cache;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteSeasonSchedule;
import com.cannontech.spring.YukonSpringHook;

public class SeasonScheduleLoader implements Runnable {
    private List<LiteSeasonSchedule> allSeasons = null;

    private static final YukonRowMapper<LiteSeasonSchedule> liteSeasonScheduleRowMapper = new YukonRowMapper<LiteSeasonSchedule>() {
        @Override
        public LiteSeasonSchedule mapRow(YukonResultSet rs) throws SQLException {
            int scheduleId = rs.getInt("ScheduleId");
            String scheduleName = rs.getString("ScheduleName").trim();
            LiteSeasonSchedule liteSeasonSchedule = new LiteSeasonSchedule(scheduleId, scheduleName);
            return liteSeasonSchedule;
        };
    };
    
    public SeasonScheduleLoader(List<LiteSeasonSchedule> allSeasons) {
        this.allSeasons = allSeasons;
    }

    @Override
    public void run() {
        Date timerStart = new Date();
        
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ScheduleId, ScheduleName");
        sql.append("FROM SeasonSchedule");
        sql.append("WHERE ScheduleId").gt(0);

        allSeasons.addAll(jdbcTemplate.query(sql, liteSeasonScheduleRowMapper));

        CTILogger.info((new Date().getTime() - timerStart.getTime()) * .001
                       + " Secs for SeasonScheduleLoader (" + allSeasons.size() + " loaded)");
    }

    public static LiteSeasonSchedule getForId(int scheduleId) {
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ScheduleId, ScheduleName");
        sql.append("FROM SeasonSchedule");
        sql.append("WHERE ScheduleId").eq(scheduleId);

        return jdbcTemplate.queryForObject(sql, liteSeasonScheduleRowMapper);
    }
}