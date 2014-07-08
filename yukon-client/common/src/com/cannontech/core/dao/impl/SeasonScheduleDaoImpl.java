package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.model.Season;

public class SeasonScheduleDaoImpl implements SeasonScheduleDao{

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final ParameterizedRowMapper<Season> seasonRowMapper = new ParameterizedRowMapper<Season>() {
        @Override
        public Season mapRow(ResultSet rs, int rowNum) throws SQLException {
            Season season = new Season();
            season.setSeasonName( rs.getString("SeasonName"));
            season.setScheduleId(rs.getInt("SeasonScheduleId"));
            return season;
        }
    };
    
    private static final ParameterizedRowMapper<Integer> scheduleIDRowMapper = new ParameterizedRowMapper<Integer>() {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            int scheduleId = rs.getInt("SeasonScheduleId");
            return scheduleId;
        }
    };
    
    @Override
    public SeasonSchedule getScheduleForPao(int paoId) {
        
        String sql = "Select SeasonScheduleID " + 
        "From CCSeasonStrategyAssignment " +
        "Where PaobjectId = " + paoId;
        
        // Use a row mapper and expect multiple rows since this table will have 
        // a row per season of this poa's assigned schedule.
        List<Integer> scheduleIds = jdbcTemplate.query(sql, scheduleIDRowMapper);
        
        SeasonSchedule schedule = new SeasonSchedule();
        // All we wanted was the scheduleId.
        if( scheduleIds.size() > 0 )
            schedule.setScheduleID(scheduleIds.get(0));
        else{
            CTILogger.warn("Error: No schedule in database for paoid: " + paoId );
            schedule.setScheduleID(-1);
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
                strategyId = -1;
            }
            map.put(season, strategyId);
        }
        return map;
    }
    
    @Override
    public Map<Season, Integer> getUserFriendlySeasonStrategyAssignments(int paoId) {
        HashMap<Season, Integer> map = new HashMap<Season, Integer>();
        SeasonSchedule schedule = getScheduleForPao(paoId);
        List<Season> seasons = getSeasonsForSchedule(schedule.getScheduleId());
        Iterator<Season> iter = seasons.iterator();
        while(iter.hasNext()) {
            Season season = iter.next();
            String sql = "Select StrategyId From CCSeasonStrategyAssignment Where PaobjectId = ? And SeasonScheduleId = ? " 
                + " And SeasonName = ?" ;
            int strategyId;
            try {
                strategyId = jdbcTemplate.queryForInt(sql, paoId, season.getScheduleId(), season.getSeasonName());
            } catch (EmptyResultDataAccessException e) {
                strategyId = -1;
            }
            if(season.getSeasonName().startsWith("&1&")) {
                season.setSeasonName(season.getSeasonName().replaceFirst("&1&", ""));
                map.put(season, strategyId);
            }else if(season.getSeasonName().startsWith("&2&")) {
                continue;  // skip the sister season for the users sake.
            }else {
                map.put(season, strategyId);
            }
        }
        return map;
    }

    @Override
    public List<Season> getSeasonsForSchedule(Integer scheduleId) {
        
        String sql = "SELECT SeasonName, SeasonScheduleID FROM DateOfSeason WHERE SeasonScheduleID = ?" 
            + " ORDER BY SEASONSCHEDULEID, SEASONNAME, SEASONSTARTMONTH," 
            + " SEASONSTARTDAY, SEASONENDMONTH, SEASONENDDAY";
        
        List<Season> seasons = jdbcTemplate.query(sql, seasonRowMapper, scheduleId);
        return seasons;
    }
    
    @Override
    public List<Season> getUserFriendlySeasonsForSchedule(Integer scheduleId) {
        
        String sql = "Select SeasonName, SeasonScheduleID From DateOfSeason Where SeasonScheduleID = ?";
        
        List<Season> seasons = jdbcTemplate.query(sql, seasonRowMapper, scheduleId);
        Iterator<Season> iter = seasons.iterator();
        while(iter.hasNext()) {
            Season season = iter.next();
            if(season.getSeasonName().startsWith("&1&")) {
                season.setSeasonName(season.getSeasonName().replaceFirst("&1&", ""));
            }else if(season.getSeasonName().startsWith("&2&")) {
                iter.remove();
            }
        }
        return seasons;
    }

    @Override
    public void saveSeasonStrategyAssigment(int paoId, Map<Season, Integer> map, int scheduleId) {
        List<Season> actualSeasons = getSeasonsForSchedule(scheduleId);
        Map<Season, Integer> fixedMap = fixSeasonMapForEndOfYearJump(map, actualSeasons);
        
        String sql = "Delete From CCSeasonStrategyAssignment Where PaobjectId = ?";
        jdbcTemplate.update(sql, paoId);
        
        for(Season actualSeason : actualSeasons) {
            if(fixedMap.containsKey(actualSeason)) {
                /* Skip updates where we are not assigning a strategy to a season (strategyId = -1) */
                if(fixedMap.get(actualSeason) == -1) continue;
                sql = "Insert Into CCSeasonStrategyAssignment Values ( ?,?,?,? )";
                jdbcTemplate.update( sql, paoId, actualSeason.getScheduleId(), actualSeason.getSeasonName() , fixedMap.get(actualSeason) );
            }
        }
    }
    
    /**
     * This is a hack to take the user friendly list of seasons
     * from the user and persist them and thier assigned strategies to
     * the database in their true, messed up, form by spliting a season that 
     * jumps the end of the year into two seasons.  This should be 
     * fixed on the server side to handle such seasons but alas that day
     * has not yet dawned.
     * @param map
     * @param actualSeasons
     * @return
     */
    @Override
    public Map<Season, Integer> fixSeasonMapForEndOfYearJump(Map<Season, Integer> map, List<Season> actualSeasons){
        HashMap<Season, Integer> newMap = new HashMap<Season, Integer>();
        
        for(Season season : actualSeasons) {
            if(season.getSeasonName().startsWith("&1&")) {
                String fakeName = season.getSeasonName().replaceFirst("&1&", "");
                Integer scheduleId = season.getScheduleId();
                Season newSeason = new Season(fakeName, scheduleId);
                Integer strategyId = map.get(newSeason);
                newMap.put(season, strategyId);
            }else if(season.getSeasonName().startsWith("&2&")) {
                String fakeName = season.getSeasonName().replaceFirst("&2&", "");
                Integer scheduleId = season.getScheduleId();
                Season newSeason = new Season(fakeName, scheduleId);
                Integer strategyId = map.get(newSeason);
                newMap.put(season, strategyId);
            }else {
                Integer strategyId = map.get(season);
                newMap.put(season, strategyId);
            }
        }
        return newMap;
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
    public void deleteStrategyAssigment(int paoId) {
        String sql = "Delete From CCSeasonStrategyAssignment Where PaobjectId = ?";
        jdbcTemplate.update(sql, paoId);
    }
    
}
