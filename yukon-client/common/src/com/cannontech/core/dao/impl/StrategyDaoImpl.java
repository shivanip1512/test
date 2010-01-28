package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.StrategyDao;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.PeaksTargetType;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.database.db.point.calculation.ControlAlgorithm;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class StrategyDaoImpl implements StrategyDao{
    
    private final ParameterizedRowMapper<CapControlStrategy> rowMapper = new StrategyRowMapper();
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
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
        String integrateFlag = "N";
        Integer integratePeriod = new Integer (0);
        String likeDayFallBack = "N";
        String endDaySettings = CtiUtilities.STRING_NONE;
        String sql = "INSERT INTO CapControlStrategy VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        yukonJdbcTemplate.update(sql, strategyId, name, controlMethod, maxDailyOperation, maxOperationDisableFlag, peakStartTime, peakStopTime,
            controlInterval, minResponseTime, minConfirmPercent, failurePercent, daysOfWeek, controlUnits, controlDelayTime, 
            controlSendRetries, integrateFlag, integratePeriod, likeDayFallBack, endDaySettings);
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
        "IntegrateFlag, IntegratePeriod," +
        "LikeDayFallBack, EndDaySettings" +
        " from CapControlStrategy order by StrategyName";
        
        List<CapControlStrategy> strategies = yukonJdbcTemplate.query(sql.toString(), rowMapper);
        
        for(CapControlStrategy strategy : strategies) {
            strategy.setTargetSettings(getPeakSettings(strategy));
        }
        
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
        int rowsAffected = yukonJdbcTemplate.update(deleteStrategyAssignments, strategyId);
        
        return rowsAffected == 1;
    }
    
    @Override
    public List<String> getAllOtherPaoNamesUsingSeasonStrategy( int strategyId, int excludedPaoId ) {
       SqlStatementBuilder sql = new SqlStatementBuilder("select ypo.PaoName paoName from YukonPAObject ypo");
       sql.append("join CCSeasonStrategyAssignment ssa on ssa.paobjectid = ypo.PAObjectID");
       sql.append("where ssa.strategyid = ").appendArgument(strategyId).append("and ssa.paobjectid <> ").appendArgument(excludedPaoId);

       return yukonJdbcTemplate.query(sql, new StringRowMapper());
    }
    
    @Override
    public List<String> getAllOtherPaoNamesUsingHolidayStrategy(int strategyId, int excludedPaoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder("select ypo.PaoName paoName from YukonPAObject ypo");
        sql.append("join CCHolidayStrategyAssignment hsa on hsa.paobjectid = ypo.PAObjectID");
        sql.append("where hsa.strategyid = ").appendArgument(strategyId).append("and hsa.paobjectid <> ").appendArgument(excludedPaoId);

        return yukonJdbcTemplate.query(sql, new StringRowMapper());
    }
    
    @Override
    public List<String> getAllOtherPaoNamesUsingStrategyAssignment(int strategyId, int excludedPaoId) {
        List<String> allPaosList = Lists.newArrayList();
        allPaosList.addAll(getAllOtherPaoNamesUsingSeasonStrategy(strategyId, excludedPaoId));
        allPaosList.addAll(getAllOtherPaoNamesUsingHolidayStrategy(strategyId, excludedPaoId));
        return allPaosList;
    }
    
    @Override
    @Transactional(readOnly = false)
    public boolean delete(int strategyId) {
        deleteStrategyAssignmentsByStrategyId(strategyId);
        String deleteStrategy = "DELETE FROM CapControlStrategy WHERE StrategyId = ?";
        int rowsAffected = yukonJdbcTemplate.update(deleteStrategy, strategyId);
        
        return rowsAffected == 1;
    }
    
    @Override
    public void savePeakSettings(CapControlStrategy strategy) {
        List<PeakTargetSetting> targetSettings = strategy.getTargetSettings();
        int strategyId = strategy.getStrategyID();
        
        PeaksTargetType peak;
        PeaksTargetType offpeak;
        if(strategy.isTimeOfDay()){
            peak = PeaksTargetType.WEEKDAY;
            offpeak = PeaksTargetType.WEEKEND;
        } else {
            peak = PeaksTargetType.PEAK;
            offpeak = PeaksTargetType.OFFPEAK;
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCStrategyTargetSettings where strategyId = ").appendArgument(strategyId);
        yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        
        for(PeakTargetSetting setting : targetSettings) {
            sql = new SqlStatementBuilder("insert into CCStrategyTargetSettings values (");
            sql.appendArgument(strategyId);
            sql.append(", ").appendArgument(setting.getName());
            sql.append(", ").appendArgument(setting.getPeakValue());
            sql.append(", ").appendArgument(peak).append(" ) ");
            yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
            
            sql = new SqlStatementBuilder("insert into CCStrategyTargetSettings values ( ");
            sql.appendArgument(strategyId);
            sql.append(", ").appendArgument(setting.getName());
            sql.append(", ").appendArgument(setting.getOffPeakValue());
            sql.append(", ").appendArgument(offpeak).append(" ) ");
            yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        }
    }

    @Override
    public List<PeakTargetSetting> getPeakSettings(CapControlStrategy strategy) {
        PeaksTargetType peak;
        PeaksTargetType offpeak;
        if(strategy.isTimeOfDay()){
            peak = PeaksTargetType.WEEKDAY;
            offpeak = PeaksTargetType.WEEKEND;
        } else {
            peak = PeaksTargetType.PEAK;
            offpeak = PeaksTargetType.OFFPEAK;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder("select peaks.SettingName name, peaks.SettingValue peakValue, offpeaks.SettingValue offPeakValue");
        sql.append("from CCStrategyTargetSettings peaks, CCStrategyTargetSettings offpeaks");
        sql.append("where peaks.SettingName = offpeaks.SettingName");
        sql.append("and peaks.strategyid = offpeaks.strategyid");
        sql.append("and peaks.SettingType = ").appendArgument(peak);
        sql.append("and offpeaks.SettingType = ").appendArgument(offpeak);
        sql.append("and peaks.strategyid = ").appendArgument(strategy.getStrategyID());
        
        ParameterizedRowMapper<PeakTargetSetting> mapper = new ParameterizedRowMapper<PeakTargetSetting>() {
            
            public PeakTargetSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
                String name = rs.getString("name");
                String peakValue = rs.getString("peakValue");
                String offPeakValue = rs.getString("offPeakValue");
                
                PeakTargetSetting setting = new PeakTargetSetting(name, peakValue, offPeakValue, null);
                
                return setting;
            }
        };

        List<PeakTargetSetting> settings = yukonJdbcTemplate.query(sql.getSql(), mapper, sql.getArguments());
        if(settings.isEmpty()) {
            ControlAlgorithm algorithm = ControlAlgorithm.getControlAlgorithm(strategy.getControlUnits());
            settings = StrategyPeakSettingsHelper.getSettingDefaults(algorithm);
        }
        
        return settings;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}