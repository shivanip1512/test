package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.device.lm.LMControlHistory;
import com.cannontech.database.incrementer.NextValueHelper;

public class LMControlHistoryDaoImpl implements LMControlHistoryDao {
    private static final String selectAllSql;
    private static final ParameterizedRowMapper<LMControlHistory> rowMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<LMControlHistory> template;
    
    private static final String TABLE_NAME = "LMControlHistory";
    
    static {
        
        selectAllSql = "SELECT LMCtrlHistId, PAObjectId, StartDateTime, SOE_Tag, ControlDuration, ControlType, CurrentDailyTime, " +
                "CurrentMonthlyTime, CurrentSeasonalTime, CurrentAnnualTime, ActiveRestore, ReductionValue, StopDateTime from " + TABLE_NAME;

        rowMapper = LMControlHistoryDaoImpl.createRowMapper();
    }
    
    /*No adds, removes, or updates should be performed on this table by the clients, which is why those methods
     * are not implemented here.
     */

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMControlHistory> getAll() {
        List<LMControlHistory> list = yukonJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
        return list;
    }
    
    private static final ParameterizedRowMapper<LMControlHistory> createRowMapper() {
        final ParameterizedRowMapper<LMControlHistory> rowMapper = new ParameterizedRowMapper<LMControlHistory>() {
            @Override
            public LMControlHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
                LMControlHistory lmControlHistory = new LMControlHistory();
                lmControlHistory.setLmCtrlHistID(rs.getInt("LMCtrlHistId"));
                lmControlHistory.setPaObjectID(rs.getInt("PAObjectId"));
                lmControlHistory.setStartDateTime(rs.getTimestamp("StartDateTime"));
                lmControlHistory.setSoeTag(rs.getInt("SOE_Tag"));
                lmControlHistory.setControlDuration(rs.getInt("ControlDuration"));
                lmControlHistory.setControlType(rs.getString("ControlType"));
                lmControlHistory.setCurrentDailyTime(rs.getInt("CurrentDailyTime"));
                lmControlHistory.setCurrentMonthlyTime(rs.getInt("CurrentMonthlyTime"));
                lmControlHistory.setCurrentSeasonalTime(rs.getInt("CurrentSeasonalTime"));
                lmControlHistory.setCurrentAnnualTime(rs.getInt("CurrentAnnualTime"));
                lmControlHistory.setActiveRestore(rs.getString("ActiveRestore"));
                lmControlHistory.setReductionValue(rs.getDouble("ActiveRestore"));
                lmControlHistory.setStopDateTime(rs.getTimestamp("StopDateTime"));
                return lmControlHistory;
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<LMControlHistory> lmControlHistoryFieldMapper = new FieldMapper<LMControlHistory>() {
        @Override
        public void extractValues(MapSqlParameterSource p, LMControlHistory lmControlHistory) {
            p.addValue("PAObjectId", lmControlHistory.getPaObjectID());
            p.addValue("StartDateTime", lmControlHistory.getStartDateTime(), Types.TIMESTAMP);
            p.addValue("SOE_Tag", lmControlHistory.getSoeTag());
            p.addValue("ControlDuration", lmControlHistory.getControlDuration());
            p.addValue("ControlType", lmControlHistory.getControlType());
            p.addValue("CurrentDailyTime", lmControlHistory.getCurrentDailyTime());
            p.addValue("CurrentMonthlyTime", lmControlHistory.getCurrentMonthlyTime());
            p.addValue("CurrentSeasonalTime", lmControlHistory.getCurrentSeasonalTime());
            p.addValue("CurrentAnnualTime", lmControlHistory.getCurrentAnnualTime());
            p.addValue("ActiveRestore", lmControlHistory.getActiveRestore());
            p.addValue("ReductionValue", lmControlHistory.getReductionValue());
            p.addValue("StopDateTime", lmControlHistory.getStopDateTime(), Types.TIMESTAMP);
        }
        @Override
        public Number getPrimaryKey(LMControlHistory lmControlHistory) {
            return lmControlHistory.getLmCtrlHistID();
        }
        @Override
        public void setPrimaryKey(LMControlHistory lmControlHistory, int value) {
            lmControlHistory.setLmCtrlHistID(value);
        }
    };

    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Autowired
    public void setYukonJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @PostConstruct
    public void init() throws Exception {
        template = 
            new SimpleTableAccessTemplate<LMControlHistory>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName(TABLE_NAME);
        template.setPrimaryKeyField("lmCtrlHistId");
        template.setFieldMapper(lmControlHistoryFieldMapper); 
    }
}
