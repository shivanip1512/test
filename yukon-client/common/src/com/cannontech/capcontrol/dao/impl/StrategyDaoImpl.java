package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.ViewableStrategy;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.PeaksTargetType;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.database.db.point.calculation.ControlAlgorithm;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class StrategyDaoImpl implements StrategyDao{
    
    private final ParameterizedRowMapper<CapControlStrategy> rowMapper = new StrategyRowMapper();
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private DurationFormattingService durationFormattingService;
    
    @Override
    public int add(String name) {
        int strategyId =  nextValueHelper.getNextValue("CapControlStrategy"); // StrategyId
        Object[] values = new Object[] {
            name,  // Strategy Name
            ControlMethod.INDIVIDUAL_FEEDER.getDatabaseRepresentation(), // Control Method
            0, // Max Daily Operations
            "N", // Max Operation Disable Flag
            0, // Peak Start Time
            86400,  // Peak Stop Time (24:00)
            900, // Control Interval
            900, // Min Response Time
            75, // Min Confirm Percent
            25, // Failure Percent
            "NYYYYYNN", // Days of the Week
            ControlAlgorithm.KVAR.getDisplayName(), // Control Units
            0, // Control Delay Time
            0, // Control Send Retries
            "N", // Integrate Flag
            0, // Integrate Period
            "N", // Like Day Fall Back
            CtiUtilities.STRING_NONE // End Day Settings
        };
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO CapControlStrategy");
        sql.values(strategyId, values);
        
        try {
            yukonJdbcTemplate.update(sql);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Strategy name already in use.");
        }
        
        return strategyId;
    }
    
    @Override
    public List<ViewableStrategy> getAllViewableStrategies(final YukonUserContext userContext) {
        Function<CapControlStrategy, ViewableStrategy> viewableConverter = new Function<CapControlStrategy, ViewableStrategy>() {
            @Override
            public ViewableStrategy apply(CapControlStrategy from) {
                ViewableStrategy strategy = new ViewableStrategy();
                
                strategy.setStrategyId(from.getStrategyID());
                strategy.setStrategyName(from.getStrategyName());
                strategy.setControlMethod(ControlMethod.getForDbString(from.getControlMethod()).getDisplayName());
                strategy.setControlUnits(from.getControlUnits());
                
                strategy.setPeakStartTime(durationFormattingService.formatDuration(from.getPeakStartTime(), TimeUnit.SECONDS, DurationFormat.HM_SHORT, userContext));
                strategy.setPeakStopTime(durationFormattingService.formatDuration(from.getPeakStopTime(), TimeUnit.SECONDS, DurationFormat.HM_SHORT, userContext));
                strategy.setControlInterval(durationFormattingService.formatDuration(from.getControlInterval(), TimeUnit.SECONDS, DurationFormat.MS_ABBR, userContext));
                strategy.setMinResponseTime(durationFormattingService.formatDuration(from.getMinResponseTime(), TimeUnit.SECONDS, DurationFormat.MS_ABBR, userContext));
                
                strategy.setPassFailPercent(from.getMinConfirmPercent() + "% / " + from.getFailurePercent() + "%");
                
                strategy.setPeakSettings(StrategyPeakSettingsHelper.getPeakSettingsString(from));
                strategy.setOffPeakSettings(StrategyPeakSettingsHelper.getOffPeakSettingsString(from));
                
                return strategy;
            }
        };
        
        List<CapControlStrategy> strategies = getAllStrategies();
        
        return Lists.transform(strategies, viewableConverter);
    }
    
    @Override
    public List<LiteCapControlStrategy> getAllLiteStrategies() {
        SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
        sql.append("SELECT StrategyId, StrategyName");
        sql.append("FROM CapControlStrategy");
        sql.append("ORDER BY StrategyName");
        
        YukonRowMapperAdapter< LiteCapControlStrategy> rowMapperAdapter = new YukonRowMapperAdapter<LiteCapControlStrategy>( 
                new YukonRowMapper<LiteCapControlStrategy>() {

            @Override
            public LiteCapControlStrategy mapRow(YukonResultSet rs) throws SQLException {
                LiteCapControlStrategy liteStrategy = new LiteCapControlStrategy();
                liteStrategy.setStrategyId(rs.getInt("strategyId"));
                liteStrategy.setStrategyName(rs.getString("strategyName"));
                return liteStrategy;
            }
            
        } );
        
        return yukonJdbcTemplate.query(sql.toString(), rowMapperAdapter);
    }
    
    @Override
    public CapControlStrategy getForId(int strategyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StrategyId, StrategyName, ControlMethod, MaxDailyOperation,");
        sql.append("  MaxOperationDisableFlag, PeakStartTime, PeakStopTime,");
        sql.append("  ControlInterval, MinResponseTime, MinConfirmPercent,");
        sql.append("  FailurePercent, DaysOfWeek, ControlUnits, ControlDelayTime,");
        sql.append("  ControlSendRetries, IntegrateFlag, IntegratePeriod,");
        sql.append("  LikeDayFallBack, EndDaySettings");
        sql.append("FROM CapControlStrategy");
        sql.append("WHERE StrategyId").eq(strategyId);
        
        CapControlStrategy strategy =  yukonJdbcTemplate.queryForObject(sql, rowMapper);
        strategy.setTargetSettings(getPeakSettings(strategy));
        
        return strategy;
    }
    
    @Override
    public List<CapControlStrategy> getAllStrategies() {
        SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
        sql.append("SELECT StrategyID, StrategyName, ControlMethod, MaxDailyOperation,");
        sql.append("  MaxOperationDisableFlag, PeakStartTime, PeakStopTime,");
        sql.append("  ControlInterval, MinResponseTime, MinConfirmPercent,");
        sql.append("  FailurePercent, DaysOfWeek, ControlUnits, ControlDelayTime,");
        sql.append("  ControlSendRetries, IntegrateFlag, IntegratePeriod,");
        sql.append("  LikeDayFallBack, EndDaySettings");
        sql.append("FROM CapControlStrategy");
        sql.append("ORDER BY StrategyName");
        
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
       SqlStatementBuilder sql = new SqlStatementBuilder();
       sql.append("SELECT ypo.PaoName paoName");
       sql.append("FROM YukonPAObject ypo");
       sql.append("  JOIN CCSeasonStrategyAssignment ssa on ssa.paobjectid = ypo.PAObjectID");
       sql.append("WHERE ssa.strategyid = ").appendArgument(strategyId);
       sql.append("  AND ssa.paobjectid <> ").appendArgument(excludedPaoId);

       return yukonJdbcTemplate.query(sql, new StringRowMapper());
    }
    
    @Override
    public List<String> getAllOtherPaoNamesUsingHolidayStrategy(int strategyId, int excludedPaoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PaoName paoName");
        sql.append("FROM YukonPAObject ypo");
        sql.append("  JOIN CCHolidayStrategyAssignment hsa on hsa.paobjectid = ypo.PAObjectID");
        sql.append("WHERE hsa.strategyid = ").appendArgument(strategyId);
        sql.append("  AND hsa.paobjectid <> ").appendArgument(excludedPaoId);

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
        sql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId = ").appendArgument(strategyId);
        yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        
        for(PeakTargetSetting setting : targetSettings) {
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, setting.getName(), setting.getPeakValue(), peak);
            yukonJdbcTemplate.update(sql);
            
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, setting.getName(), setting.getOffPeakValue(), offpeak);
            yukonJdbcTemplate.update(sql);
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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT peaks.SettingName name, peaks.SettingValue peakValue, offpeaks.SettingValue offPeakValue");
        sql.append("FROM CCStrategyTargetSettings peaks, CCStrategyTargetSettings offpeaks");
        sql.append("WHERE peaks.SettingName = offpeaks.SettingName");
        sql.append("  AND peaks.strategyid = offpeaks.strategyid");
        sql.append("  AND peaks.SettingType = ").appendArgument(peak);
        sql.append("  AND offpeaks.SettingType = ").appendArgument(offpeak);
        sql.append("  AND peaks.strategyid = ").appendArgument(strategy.getStrategyID());
        
        ParameterizedRowMapper<PeakTargetSetting> mapper = new ParameterizedRowMapper<PeakTargetSetting>() {
            
            public PeakTargetSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
                String name = rs.getString("name");
                String peakValue = rs.getString("peakValue");
                String offPeakValue = rs.getString("offPeakValue");
                
                TargetSettingType targetSetting = TargetSettingType.getByName(name);
                PeakTargetSetting setting = new PeakTargetSetting(name, peakValue, offPeakValue, targetSetting.getUnits());
                
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
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setDurationFormattingService(DurationFormattingService durationFormattingService) {
        this.durationFormattingService = durationFormattingService;
    }
}