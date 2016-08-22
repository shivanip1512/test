package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

public class HolidayScheduleDaoImpl implements HolidayScheduleDao{

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private IDatabaseCache cache;
    
    private static final RowMapper<HolidaySchedule> holidayScheduleRowMapper = new RowMapper<HolidaySchedule>() {
        @Override
        public HolidaySchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
            HolidaySchedule holiday = new HolidaySchedule();
            holiday.setHolidayScheduleId(rs.getInt("HolidayScheduleId"));
            holiday.setHolidayScheduleName(rs.getString("HolidayScheduleName"));
            return holiday;
        }
    };
    
    private static final RowMapper<Integer> scheduleIDRowMapper = new RowMapper<Integer>() {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            int scheduleId = rs.getInt("HolidayScheduleId");
            return scheduleId;
        }
    };
    
    @Override
    public HolidaySchedule getScheduleForPao(int paoId) {
        
        String sql = "Select HolidayScheduleId " + 
        "From CCHolidayStrategyAssignment " +
        "Where PaobjectId = " + paoId;
        
        // Use a row mapper and expect multiple rows since this table can have 
        // a row per holiday of this poa's assigned schedule.
        List<Integer> scheduleIds = jdbcTemplate.query(sql, scheduleIDRowMapper);
        
        HolidaySchedule schedule = new HolidaySchedule();
        // All we wanted was the scheduleId.
        if( scheduleIds.size() > 0 ) {
            schedule.setHolidayScheduleId(scheduleIds.get(0));
        } else {
            CTILogger.error("Error: No schedule in database for paoid: " + paoId );
            schedule.setHolidayScheduleId(-1);
        }
        Connection connection = null;
        try {
            if (schedule.getDbConnection() == null) {
                connection = getConnection();
                schedule.setDbConnection(connection);
            }
            schedule.retrieve();
        } catch (SQLException e) {
            CTILogger.error(e);
        }
        finally {
            closeConnection(connection);
        }
        
        return schedule;
    }
    
    @Override
    public Integer getStrategyForPao(int paoId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StrategyId");
        sql.append("FROM CCHolidayStrategyAssignment");
        sql.append("WHERE PaobjectId").eq(paoId);
       
        try {
            Integer strategyId = jdbcTemplate.queryForInt(sql);
            return strategyId;
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public void saveHolidayScheduleStrategyAssigment(int paoId, int scheduleId, int strategyId) {
        
        String sql = "Delete From CCHolidayStrategyAssignment Where PaobjectId = ?";
        jdbcTemplate.update(sql, paoId);
        
        if(scheduleId == -1) {
            return;
        }
        
        sql = "Insert Into CCHolidayStrategyAssignment Values ( ?,?,? )";
        jdbcTemplate.update( sql, paoId, scheduleId, strategyId);
        
        LiteYukonPAObject paoObj = cache.getAllPaosMap().get(paoId);
        dbChangeManager.processPaoDbChange(paoObj, DbChangeType.UPDATE);
    }
    
    public static Connection getConnection() {
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {}
        }
    }
    
    @Override
    public List<HolidaySchedule> getAllHolidaySchedules() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT HolidayScheduleID, HolidayScheduleName");
        sql.append("FROM HolidaySchedule");
        sql.append("WHERE HolidayScheduleId <> 0");
        sql.append("ORDER BY HolidayScheduleName");
        
        return jdbcTemplate.query(sql, holidayScheduleRowMapper);
    }

    @Override
    public void deleteStrategyAssigment(int paoId) {
        String sql = "Delete From CCHolidayStrategyAssignment Where PaobjectId = ?";
        jdbcTemplate.update(sql, paoId);
    }
    
}
