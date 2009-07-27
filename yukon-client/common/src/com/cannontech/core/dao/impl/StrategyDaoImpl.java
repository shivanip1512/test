package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.StrategyDao;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.database.incrementer.NextValueHelper;


public class StrategyDaoImpl implements StrategyDao{
    
    private final ParameterizedRowMapper<CapControlStrategy> rowMapper = new StrategyRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;

    public StrategyDaoImpl() {
        super();
    }
    
    @Override
    public int add(String name) {
        int strategyId = nextValueHelper.getNextValue("CapControlStrategy");
        String controlMethod = "IndividualFeeder";
        Integer maxDailyOperation = new Integer(0);
        String maxOperationDisableFlag = "N";
        Integer peakStartTime = new Integer(0);
        Integer peakStopTime = new Integer(86400);  //24:00
        Integer controlInterval = new Integer(900);
        Integer minResponseTime = new Integer(900);
        Integer minConfirmPercent = new Integer(75);
        Integer failurePercent = new Integer(25);
        String daysOfWeek = new String("NYYYYYNN");
        String controlUnits = CalcComponentTypes.LABEL_KVAR;
        Integer controlDelayTime = new Integer(0);
        Integer controlSendRetries = new Integer(0);
        Double peakLag = new Double(0.0);
        Double peakLead = new Double(0.0);
        Double offPkLag = new Double(0.0);
        Double offPkLead = new Double(0.0);
        Double pkVarLag = new Double (0.0);
        Double pkVarLead = new Double(0.0);
        Double offpkVarLead = new Double(0.0);
        Double offpkVarLag = new Double(0.0);
        Double pkPFPoint = new Double (0.1);
        Double offPkPFPoint = new Double (0.1);
        String integrateFlag = "N";
        Integer integratePeriod = new Integer (0);
        String likeDayFallBack = "N";
        String endDaySettings = CtiUtilities.STRING_NONE;
        String sql = "INSERT INTO CapControlStrategy VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        simpleJdbcTemplate.update(sql, strategyId, name, controlMethod, maxDailyOperation, maxOperationDisableFlag, peakStartTime, peakStopTime,
            controlInterval, minResponseTime, minConfirmPercent, failurePercent, daysOfWeek, controlUnits, controlDelayTime, controlSendRetries, peakLag, peakLead,
            offPkLag, offPkLead, pkVarLag, pkVarLead, offpkVarLag, offpkVarLead, pkPFPoint, offPkPFPoint, integrateFlag, integratePeriod, likeDayFallBack, endDaySettings);
        return strategyId;
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
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}