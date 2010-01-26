package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.database.model.Holiday;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.holiday.HolidaySchedule;

public class HolidayScheduleDaoImpl implements HolidayScheduleDao{

    private SimpleJdbcOperations jdbcTemplate;
    
    private static final ParameterizedRowMapper<Holiday> holidayRowMapper = new ParameterizedRowMapper<Holiday>() {
        public Holiday mapRow(ResultSet rs, int rowNum) throws SQLException {
            Holiday holiday = new Holiday();
            holiday.setHolidayName( rs.getString("HolidayName"));
            holiday.setScheduleId(rs.getInt("HolidayScheduleId"));
            return holiday;
        }
    };
    
    private static final ParameterizedRowMapper<HolidaySchedule> holidayScheduleRowMapper = new ParameterizedRowMapper<HolidaySchedule>() {
        public HolidaySchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
            HolidaySchedule holiday = new HolidaySchedule();
            holiday.setHolidayScheduleId(rs.getInt("HolidayScheduleId"));
            holiday.setHolidayScheduleName(rs.getString("HolidayScheduleName"));
            return holiday;
        }
    };
    
    private static final ParameterizedRowMapper<Integer> scheduleIDRowMapper = new ParameterizedRowMapper<Integer>() {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            int scheduleId = rs.getInt("HolidayScheduleId");
            return scheduleId;
        }
    };
    
    public HolidaySchedule getScheduleForPao(int paoId) {
        
        String sql = "Select HolidayScheduleId " + 
        "From CCHolidayStrategyAssignment " +
        "Where PaobjectId = " + paoId;
        
        // Use a row mapper and expect multiple rows since this table can have 
        // a row per holiday of this poa's assigned schedule.
        List<Integer> scheduleIds = jdbcTemplate.query(sql, scheduleIDRowMapper);
        
        HolidaySchedule schedule = new HolidaySchedule();
        // All we wanted was the scheduleId.
        if( scheduleIds.size() > 0 )
            schedule.setHolidayScheduleId(scheduleIds.get(0));
        else{
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
    
    public Integer getStrategyForPao(int paoId) {
        
        String sql = "Select StrategyId " + 
        "From CCHolidayStrategyAssignment " +
        "Where PaobjectId = " + paoId;
        try {
            Integer strategyId = jdbcTemplate.queryForInt(sql);
            return strategyId;
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }
    
    public List<Holiday> getHolidaysForSchedule(Integer scheduleId) {
        if(scheduleId <1) {
            return new ArrayList<Holiday>();
        }
        String sql = "Select HolidayName, HolidayScheduleId From DateOfHoliday Where HolidayScheduleId = ?";
        
        List<Holiday> holidays = jdbcTemplate.query(sql, holidayRowMapper, scheduleId);
        return holidays;
    }

    public void saveHolidayScheduleStrategyAssigment(int paoId, int scheduleId, int strategyId) {
        
        String sql = "Delete From CCHolidayStrategyAssignment Where PaobjectId = ?";
        jdbcTemplate.update(sql, paoId);
        
        if(scheduleId == -1) return;
        
        sql = "Insert Into CCHolidayStrategyAssignment Values ( ?,?,? )";
        jdbcTemplate.update( sql, paoId, scheduleId, strategyId);
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
    
    public List<HolidaySchedule> getAllHolidaySchedules() {
       String sql = "select HolidayScheduleID, HolidayScheduleName from HolidaySchedule where HolidayScheduleId <> 0 order by HolidayScheduleName";
       return jdbcTemplate.query(sql, holidayScheduleRowMapper);
    }

    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteStrategyAssigment(int paoId) {
        String sql = "Delete From CCHolidayStrategyAssignment Where PaobjectId = ?";
        jdbcTemplate.update(sql, paoId);
    }
    
}
