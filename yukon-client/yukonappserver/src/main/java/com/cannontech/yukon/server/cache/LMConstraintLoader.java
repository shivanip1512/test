package com.cannontech.yukon.server.cache;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.spring.YukonSpringHook;

public class LMConstraintLoader implements Runnable {
    private List<LiteLMConstraint> allLMConstraints = null;

    private static final YukonRowMapper<LiteLMConstraint> liteLmConstraintRowMapper = new YukonRowMapper<LiteLMConstraint>() {
        @Override
        public LiteLMConstraint mapRow(YukonResultSet rs) throws SQLException {
            int constraintId = rs.getInt("ConstraintId");
            String constraintName = rs.getString("ConstraintName").trim();
            LiteLMConstraint lmConstraint = new LiteLMConstraint(constraintId, constraintName);
            return lmConstraint;
        };
    };
    
    public LMConstraintLoader(List<LiteLMConstraint> allLMConstraints) {
        this.allLMConstraints = allLMConstraints;
    }

    @Override
    public void run() {
        Date timerStart = new Date();
        
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ConstraintId, ConstraintName");
        sql.append("FROM LmProgramConstraints");
        sql.append("WHERE ConstraintId").gte(0);
        sql.append("ORDER BY ConstraintName");

        allLMConstraints.addAll(jdbcTemplate.query(sql, liteLmConstraintRowMapper));

        CTILogger.info((new Date().getTime() - timerStart.getTime()) * .001
                       + " Secs for LMConstraintLoader (" + allLMConstraints.size() + " loaded)");
    }

    public static LiteLMConstraint getForId(int constraintId) {
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ConstraintId, ConstraintName");
        sql.append("FROM LmProgramConstraints");
        sql.append("WHERE ConstraintId").eq(constraintId);

        return jdbcTemplate.queryForObject(sql, liteLmConstraintRowMapper);
    }
}
