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
        "LikeDayFallBack" +
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
            strategy.setStrategyID( new Integer(rs.getInt(1)) );
            strategy.setStrategyName( rs.getString(2) );
            strategy.setControlMethod( rs.getString(3) );
            strategy.setMaxDailyOperation( new Integer(rs.getInt(4)) );
            strategy.setMaxOperationDisableFlag( new Character(rs.getString(5).charAt(0)) );      
            strategy.setPeakStartTime( new Integer(rs.getInt(6)) );
            strategy.setPeakStopTime( new Integer(rs.getInt(7)) );        
            strategy.setControlInterval( new Integer(rs.getInt(8)) );
            strategy.setMinResponseTime( new Integer(rs.getInt(9)) );
            strategy.setMinConfirmPercent( new Integer(rs.getInt(10)) );
            strategy.setFailurePercent( new Integer(rs.getInt(11)) );
            strategy.setDaysOfWeek( rs.getString(12) );
            strategy.setControlUnits( rs.getString(13) );
            strategy.setControlDelayTime( new Integer(rs.getInt(14)) );
            strategy.setControlSendRetries( new Integer(rs.getInt(15)) );
            strategy.setPeakLag( new Double(rs.getDouble(16)) );
            strategy.setPeakLead( new Double(rs.getDouble(17)) );
            strategy.setOffPkLag( new Double(rs.getDouble(18)) );
            strategy.setOffPkLead( new Double(rs.getDouble(19)) );
            strategy.setPkVarLag(new Double (rs.getDouble(20)) );
            strategy.setPkVarLead(new Double (rs.getDouble(21)) );
            strategy.setOffpkVarLag(new Double (rs.getDouble(22)) );
            strategy.setOffpkVarLead(new Double (rs.getDouble(23)) );
            strategy.setPkPFPoint(new Double (rs.getDouble(24)));
            strategy.setOffPkPFPoint(new Double (rs.getDouble(25)));
            strategy.setIntegrateFlag(new String (rs.getString(26)));
            strategy.setIntegratePeriod(new Integer (rs.getInt(27)));
            strategy.setLikeDayFallBack(new String (rs.getString(28)));
            
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
