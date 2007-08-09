package com.cannontech.cbc.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.cbc.dao.SeasonScheduleDao;
import com.cannontech.cbc.model.Season;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.season.SeasonSchedule;

public class SeasonScheduleDaoImpl implements SeasonScheduleDao{

    private SimpleJdbcOperations jdbcTemplate;
    
    private static final ParameterizedRowMapper<Season> seasonRowMapper = new ParameterizedRowMapper<Season>() {
        public Season mapRow(ResultSet rs, int rowNum) throws SQLException {
            Season season = new Season();
            season.setSeasonName( rs.getString("SeasonName"));
            season.setScheduleId(rs.getInt("SeasonScheduleId"));
            return season;
        }
    };
    
    private static final ParameterizedRowMapper<Integer> scheduleIDRowMapper = new ParameterizedRowMapper<Integer>() {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            int scheduleId = rs.getInt("SeasonScheduleId");
            return scheduleId;
        }
    };
    
    public SeasonSchedule getScheduleForPao(int paoId) {
        
        String sql = "Select SeasonScheduleID " + 
        "From CCSeasonStrategyAssignment " +
        "Where PaobjectId = " + paoId;
        
        // Use a row mapper and expect multiple rows since this table will have 
        // a row per season of this poa's assigned schedule.
        List<Integer> scheduleIds = jdbcTemplate.query(sql, scheduleIDRowMapper);
        
        SeasonSchedule schedule = new SeasonSchedule();
        // All we wanted was the scheduleId.
        schedule.setScheduleID(scheduleIds.get(0));
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
    
    public Map<Season, Integer> getSeasonStrategyAssignments(int paoId) {
        HashMap<Season, Integer> map = new HashMap<Season, Integer>();
        SeasonSchedule schedule = getScheduleForPao(paoId);
        List<Season> seasons = getSeasonsForSchedule(schedule.getScheduleId());
        for(Season season : seasons) {
            String sql = "Select StrategyId From CCSeasonStrategyAssignment Where PaobjectId = ? And SeasonScheduleId = ? " 
                + " And SeasonName = ?" ;
            int strategyId;
            try {
                strategyId = jdbcTemplate.queryForInt(sql, paoId, season.getScheduleId(), season.getSeasonName());
            } catch (EmptyResultDataAccessException e) {
                strategyId = 0;
            }
            map.put(season, strategyId);
        }
        return map;
    }

    public List<Season> getSeasonsForSchedule(Integer scheduleId) {
        
        String sql = "Select SeasonName, SeasonScheduleID From DateOfSeason Where SeasonScheduleID = ?";
        
        List<Season> seasons = jdbcTemplate.query(sql, seasonRowMapper, scheduleId);
        return seasons;
    }

    public void saveSeasonStrategyAssigment(int paoId, Map<Season, Integer> map, int scheduleId) {
        
        String sql = "Delete From CCSeasonStrategyAssignment Where PaobjectId = ?";
        jdbcTemplate.update(sql, paoId);
        List<Season> actualSeasons = getSeasonsForSchedule(scheduleId);
        for(Season actualSeason : actualSeasons) {
            if(map.containsKey(actualSeason)) {
                sql = "Insert Into CCSeasonStrategyAssignment Values ( ?,?,?,? )";
                jdbcTemplate.update( sql, paoId, actualSeason.getScheduleId(), actualSeason.getSeasonName() , map.get(actualSeason) );
            }
        }
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

    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
}
