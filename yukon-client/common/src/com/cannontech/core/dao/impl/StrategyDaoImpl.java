package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.StrategyDao;
import com.cannontech.database.db.capcontrol.CapControlStrategy;


public class StrategyDaoImpl implements StrategyDao{
    
    private final ParameterizedRowMapper<CapControlStrategy> rowMapper = new StrategyRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;

    public StrategyDaoImpl() {
        super();
    }
    
    @Override
    public List<CapControlStrategy> getAllStrategies() {
        String sql = "select " + 
        "StrategyID, StrategyName, ControlMethod, MaxDailyOperation,"+
        "MaxOperationDisableFlag," +
        "PeakStartTime, PeakStopTime," +
        "ControlInterval, MinResponseTime, MinConfirmPercent," +
        "FailurePercent, DaysOfWeek," +
        "ControlUnits, ControlDelayTime, ControlSendRetries," +
        "PeakLag, PeakLead, OffPkLag, OffPkLead, " + 
        "PeakVARLag, PeakVARLead , OffPkVARLag , OffPkVARLead," + 
        "PeakPFSetPoint, OffPkPFSetPoint, IntegrateFlag, IntegratePeriod," +
        "LikeDayFallBack, EndDaySettings" +
        " from CapControlStrategy  where StrategyId > 0 order by StrategyName";
        
        List<CapControlStrategy> strategies = simpleJdbcTemplate.query(sql.toString(), rowMapper);
        
        return strategies;
    }
    
    /**
     * Helper class to map a result set into LiteContacts
     * 
     * Note: this mapper MUST be used within a transaction
     */
    private class StrategyRowMapper implements ParameterizedRowMapper<CapControlStrategy> {
        
        @Override
        public CapControlStrategy mapRow(ResultSet rs, int rowNum) throws SQLException {
            CapControlStrategy strategy = new CapControlStrategy();
            strategy.setStrategyID( rs.getInt("StrategyID"));
            strategy.setStrategyName( rs.getString("StrategyName") );
            strategy.setControlMethod( rs.getString("ControlMethod") );
            strategy.setMaxDailyOperation( rs.getInt("MaxDailyOperation") );
            strategy.setMaxOperationDisableFlag( new Character(rs.getString(5).charAt(0)) );      
            strategy.setPeakStartTime( rs.getInt("PeakStartTime") );
            strategy.setPeakStopTime( rs.getInt("PeakStopTime") );        
            strategy.setControlInterval( rs.getInt("ControlInterval") );
            strategy.setMinResponseTime( rs.getInt("MinResponseTime") );
            strategy.setMinConfirmPercent( rs.getInt("MinConfirmPercent") );
            strategy.setFailurePercent( rs.getInt("FailurePercent") );
            strategy.setDaysOfWeek( rs.getString("DaysOfWeek") );
            strategy.setControlUnits( rs.getString("ControlUnits") );
            strategy.setControlDelayTime( rs.getInt("ControlDelayTime") );
            strategy.setControlSendRetries( rs.getInt("ControlSendRetries") );
            strategy.setPeakLag( rs.getDouble("PeakLag") );
            strategy.setPeakLead( rs.getDouble("PeakLead") );
            strategy.setOffPkLag( rs.getDouble("OffPkLag") );
            strategy.setOffPkLead( rs.getDouble("OffPkLead") );
            strategy.setPkVarLag(rs.getDouble("PeakVARLag") );
            strategy.setPkVarLead(rs.getDouble("PeakVARLead") );
            strategy.setOffpkVarLag(rs.getDouble("OffPkVARLag") );
            strategy.setOffpkVarLead(rs.getDouble("OffPkVARLead") );
            strategy.setPkPFPoint(rs.getDouble("PeakPFSetPoint"));
            strategy.setOffPkPFPoint(rs.getDouble("OffPkPFSetPoint"));
            strategy.setIntegrateFlag(rs.getString("IntegrateFlag"));
            strategy.setIntegratePeriod(rs.getInt("IntegratePeriod"));
            strategy.setLikeDayFallBack(rs.getString("LikeDayFallBack"));
            strategy.setEndDaySettings(rs.getString("EndDaySettings"));
            return strategy;
        }

    }
    
    @Override
    @Transactional(readOnly = false)
    public boolean deleteStrategyAssignmentsByStrategyId(int strategyId){
        String deleteStrategyAssignments = "DELETE FROM CCSeasonStrategyAssignment WHERE StrategyId = ?";
        int rowsAffected = simpleJdbcTemplate.update(deleteStrategyAssignments, strategyId);
        
        return rowsAffected == 1;
    }
    
    @Override
    @Transactional(readOnly = false)
    public boolean delete(int strategyId) {
        deleteStrategyAssignmentsByStrategyId(strategyId);
        String deleteStrategy = "DELETE FROM CapControlStrategy WHERE StrategyId = ?";
        int rowsAffected = simpleJdbcTemplate.update(deleteStrategy, strategyId);
        
        return rowsAffected == 1;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
