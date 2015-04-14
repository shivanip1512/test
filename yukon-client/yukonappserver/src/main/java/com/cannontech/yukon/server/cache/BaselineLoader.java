package com.cannontech.yukon.server.cache;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.spring.YukonSpringHook;

public class BaselineLoader implements Runnable {
    private List<LiteBaseline> allBaselines = null;
    
    private static final YukonRowMapper<LiteBaseline> baselineRowMapper = new YukonRowMapper<LiteBaseline>() {
        @Override
        public LiteBaseline mapRow(YukonResultSet rs) throws SQLException {
            int baselineId = rs.getInt("BaselineId");
            String baselineName = rs.getString("BaselineName").trim();
            LiteBaseline liteBaseline = new LiteBaseline(baselineId, baselineName);
            return liteBaseline;
        }
    };

    public BaselineLoader(List<LiteBaseline> allBaselines) {
        this.allBaselines = allBaselines;
    }

    @Override
    public void run() {
        Date timerStart = new Date();
        
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BaselineId, BaselineName FROM Baseline");
        sql.append("WHERE BaseLineId").gte(0);

        allBaselines.addAll(jdbcTemplate.query(sql, baselineRowMapper));
        CTILogger.info((new Date().getTime() - timerStart.getTime()) * .001
            + " Secs for BaselineLoader (" + allBaselines.size() + " loaded)");
    }
    
    public static LiteBaseline getForId(int baselineId) {
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BaselineId, BaselineName FROM Baseline");
        sql.append("WHERE BaselineId").eq(baselineId);

        return jdbcTemplate.queryForObject(sql, baselineRowMapper);
    }
}