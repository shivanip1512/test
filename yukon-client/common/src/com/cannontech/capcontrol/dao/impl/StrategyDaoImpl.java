package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.StrategyLimitsHolder;
import com.cannontech.capcontrol.model.ViewableStrategy;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.PeaksTargetType;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSetting;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSettingName;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSettingType;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.database.db.capcontrol.VoltageViolationSetting;
import com.cannontech.database.db.capcontrol.VoltageViolationSettingNameType;
import com.cannontech.database.db.capcontrol.VoltageViolationSettingType;
import com.cannontech.database.db.capcontrol.VoltageViolationSettingsHelper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class StrategyDaoImpl implements StrategyDao, InitializingBean {
    
    private final ParameterizedRowMapper<CapControlStrategy> rowMapper = new StrategyRowMapper();
    private SimpleTableAccessTemplate<CapControlStrategy> strategyTemplate;
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DurationFormattingService durationFormattingService;
    
    private FieldMapper<CapControlStrategy> strategyFieldMapper = new FieldMapper<CapControlStrategy>() {

        @Override
        public void extractValues(MapSqlParameterSource p, CapControlStrategy o) {
            p.addValue("StrategyName", o.getStrategyName());
            p.addValue("ControlMethod", o.getControlMethod());
            p.addValue("MaxDailyOperation", o.getMaxDailyOperation());
            p.addValue("MaxOperationDisableFlag", o.getMaxOperationDisableFlag(), Types.VARCHAR);
            p.addValue("PeakStartTime", o.getPeakStartTime());
            p.addValue("PeakStopTime", o.getPeakStopTime());
            p.addValue("ControlInterval", o.getControlInterval());
            p.addValue("MinResponseTime", o.getMinResponseTime());
            p.addValue("MinConfirmPercent", o.getMinConfirmPercent());
            p.addValue("FailurePercent", o.getFailurePercent());
            p.addValue("DaysOfWeek", o.getDaysOfWeek());
            p.addValue("ControlUnits", o.getControlUnits());
            p.addValue("ControlDelayTime", o.getControlDelayTime());
            p.addValue("ControlSendRetries", o.getControlSendRetries());
            p.addValue("IntegrateFlag", o.getIntegrateFlag());
            p.addValue("IntegratePeriod", o.getIntegratePeriod());
            p.addValue("LikeDayFallBack", o.getLikeDayFallBack());
            p.addValue("EndDaySettings", o.getEndDaySettings());
        }

        @Override
        public Number getPrimaryKey(CapControlStrategy strategy) {
            return strategy.getStrategyID();
        }

        @Override
        public void setPrimaryKey(CapControlStrategy object, int strategyId) {
            object.setStrategyID(strategyId);
        }
    };
    
    @Override
    @Transactional
    public int add(String name) {
        
        CapControlStrategy strategy = new CapControlStrategy();
        strategy.setStrategyName(name);
        
        try {
            strategyTemplate.insert(strategy);
            savePeakSettings(strategy);
            saveVoltageViolationSettings(strategy);
            savePowerFactorCorrectionSetting(strategy);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Strategy name already in use.");
        }
        
        return strategy.getStrategyID();
    }
    
    @Override
    @Transactional
    public void update(CapControlStrategy strategy) {
        try {
            strategyTemplate.update(strategy);
            savePeakSettings(strategy);
            saveVoltageViolationSettings(strategy);
            savePowerFactorCorrectionSetting(strategy);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Strategy name already in use.");
        }
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
    public List<ViewableStrategy> getAllViewableStrategies(final YukonUserContext userContext) {
        Function<CapControlStrategy, ViewableStrategy> viewableConverter = new Function<CapControlStrategy, ViewableStrategy>() {
            @Override
            public ViewableStrategy apply(CapControlStrategy from) {
                ViewableStrategy strategy = new ViewableStrategy();
                
                strategy.setStrategyId(from.getStrategyID());
                strategy.setStrategyName(from.getStrategyName());
                strategy.setControlMethod(from.getControlMethod().getDisplayName());
                strategy.setControlUnits(from.getControlUnits().getDisplayName());
                
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
        SqlStatementBuilder sql = new SqlStatementBuilder();
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
        
        return yukonJdbcTemplate.query(sql, rowMapperAdapter);
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
        strategy.setVoltageViolationSettings(getVoltageViolationSettings(strategy));
        strategy.setPowerFactorCorrectionSetting(getPowerFactorCorrectionSetting(strategy));
        
        return strategy;
    }
    
    @Override
    public List<CapControlStrategy> getAllStrategies() {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StrategyID, StrategyName, ControlMethod, MaxDailyOperation,");
        sql.append("  MaxOperationDisableFlag, PeakStartTime, PeakStopTime,");
        sql.append("  ControlInterval, MinResponseTime, MinConfirmPercent,");
        sql.append("  FailurePercent, DaysOfWeek, ControlUnits, ControlDelayTime,");
        sql.append("  ControlSendRetries, IntegrateFlag, IntegratePeriod,");
        sql.append("  LikeDayFallBack, EndDaySettings");
        sql.append("FROM CapControlStrategy");
        sql.append("ORDER BY StrategyName");
        
        List<CapControlStrategy> strategies = yukonJdbcTemplate.query(sql, rowMapper);
        
        for(CapControlStrategy strategy : strategies) {
            strategy.setTargetSettings(getPeakSettings(strategy));
            strategy.setVoltageViolationSettings(getVoltageViolationSettings(strategy));
            strategy.setPowerFactorCorrectionSetting(getPowerFactorCorrectionSetting(strategy));
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
            strategy.setControlMethod( ControlMethod.valueOf(rs.getString("ControlMethod")) );
            strategy.setMaxDailyOperation( rs.getInt("MaxDailyOperation") );
            strategy.setMaxOperationDisableFlag( new Character(rs.getString(5).charAt(0)) );      
            strategy.setPeakStartTime( rs.getInt("PeakStartTime") );
            strategy.setPeakStopTime( rs.getInt("PeakStopTime") );        
            strategy.setControlInterval( rs.getInt("ControlInterval") );
            strategy.setMinResponseTime( rs.getInt("MinResponseTime") );
            strategy.setMinConfirmPercent( rs.getInt("MinConfirmPercent") );
            strategy.setFailurePercent( rs.getInt("FailurePercent") );
            strategy.setDaysOfWeek( rs.getString("DaysOfWeek") );
            strategy.setControlUnits( ControlAlgorithm.valueOf(rs.getString("ControlUnits")) );
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
    @Transactional
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
        sql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId").eq(strategyId);
        sql.append("AND SettingType").in(Lists.newArrayList(PeaksTargetType.values()));
        yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        
        for(PeakTargetSetting setting : targetSettings) {
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, setting.getType(), setting.getPeakValue(), peak);
            yukonJdbcTemplate.update(sql);
            
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, setting.getType(), setting.getOffPeakValue(), offpeak);
            yukonJdbcTemplate.update(sql);
        }
    }

    @Override
    @Transactional
    public void saveVoltageViolationSettings(CapControlStrategy strategy) {
        int strategyId = strategy.getStrategyID();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId").eq(strategyId);
        sql.append("AND SettingType").in(Lists.newArrayList(VoltageViolationSettingType.values()));
        yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        
        if (!strategy.isIvvc()) return; // Don't save these IVVC-only settings if we aren't going to use them

        List<VoltageViolationSetting> targetSettings = strategy.getVoltageViolationSettings();
        /* Perform Validation */
        validateVoltageViolationSettings(targetSettings);
        
        for(VoltageViolationSetting setting : targetSettings) {
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, setting.getName(), setting.getBandwidth(), VoltageViolationSettingType.BANDWIDTH);
            yukonJdbcTemplate.update(sql);

            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, setting.getName(), setting.getCost(), VoltageViolationSettingType.COST);
            yukonJdbcTemplate.update(sql);
            
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, setting.getName(), setting.getEmergencyCost(), VoltageViolationSettingType.EMERGENCY_COST);
            yukonJdbcTemplate.update(sql);
        }
    }
    
    @Override
    @Transactional
    public void savePowerFactorCorrectionSetting(CapControlStrategy strategy) {
        int strategyId = strategy.getStrategyID();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId").eq(strategyId);
        sql.append("AND SettingName").eq_k(PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION.getDisplayName());
        yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        
        if (!strategy.isIvvc()) return; // Don't save these IVVC-only settings if we aren't going to use them
        
        PowerFactorCorrectionSetting setting = strategy.getPowerFactorCorrectionSetting();
        /* Perform Validation */
        validatePowerFactorCorrectionSetting(setting);

        /* Bandwidth */
        sql = new SqlStatementBuilder();
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION,
                   setting.getBandwidth(),
                   PowerFactorCorrectionSettingType.BANDWIDTH);
        yukonJdbcTemplate.update(sql);

        /* cost */
        sql = new SqlStatementBuilder();
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION,
                   setting.getCost(),
                   PowerFactorCorrectionSettingType.COST);
        yukonJdbcTemplate.update(sql);
        
        /* max cost */
        sql = new SqlStatementBuilder();
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION,
                   setting.getMaxCost(),
                   PowerFactorCorrectionSettingType.MAX_COST);
        yukonJdbcTemplate.update(sql);
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
        sql.append("  AND peaks.SettingType").eq(peak);
        sql.append("  AND offpeaks.SettingType").eq(offpeak);
        sql.append("  AND peaks.strategyid").eq(strategy.getStrategyID());
        
        List<PeakTargetSetting> settings = yukonJdbcTemplate.query(sql, peakTargetSettingMapper);
        if(settings.isEmpty()) {
            settings = StrategyPeakSettingsHelper.getSettingDefaults(strategy.getControlUnits());
        }
        
        return settings;
    }
    
    @Override
    public PowerFactorCorrectionSetting getPowerFactorCorrectionSetting(CapControlStrategy strategy) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT bw.SettingValue bwValue, cost.SettingValue costValue, maxCost.SettingValue maxCostValue");
        sql.append("FROM CCStrategyTargetSettings bw, CCStrategyTargetSettings cost, CCStrategyTargetSettings maxCost");
        sql.append("WHERE bw.SettingName = cost.SettingName");
        sql.append("  AND bw.SettingName = maxCost.SettingName");
        sql.append("  AND bw.SettingName").eq_k(PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION.getDisplayName());
        sql.append("  AND bw.strategyid = cost.strategyid");
        sql.append("  AND bw.strategyid = maxCost.strategyid");
        sql.append("  AND bw.strategyid").eq(strategy.getStrategyID());
        sql.append("  AND bw.SettingType").eq_k(PowerFactorCorrectionSettingType.BANDWIDTH);
        sql.append("  AND cost.SettingType").eq_k(PowerFactorCorrectionSettingType.COST);
        sql.append("  AND maxCost.SettingType").eq_k(PowerFactorCorrectionSettingType.MAX_COST);
        
        try {
            return yukonJdbcTemplate.queryForObject(sql, powerFactorCorrectionSettingMapper);
        } catch (EmptyResultDataAccessException e) {
            return new PowerFactorCorrectionSetting();
        }
    }

    @Override
    public List<VoltageViolationSetting> getVoltageViolationSettings(CapControlStrategy strategy) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT bw.SettingName name, bw.SettingValue bwValue, cost.SettingValue costValue, emergencyCost.SettingValue emergencyCostValue");
        sql.append("FROM CCStrategyTargetSettings bw, CCStrategyTargetSettings cost, CCStrategyTargetSettings emergencyCost");
        sql.append("WHERE bw.SettingName = cost.SettingName");
        sql.append("  AND bw.SettingName = emergencyCost.SettingName");
        sql.append("  AND bw.strategyid = cost.strategyid");
        sql.append("  AND bw.strategyid = emergencyCost.strategyid");
        sql.append("  AND bw.SettingType").eq_k(VoltageViolationSettingType.BANDWIDTH);
        sql.append("  AND cost.SettingType").eq_k(VoltageViolationSettingType.COST);
        sql.append("  AND emergencyCost.SettingType").eq_k(VoltageViolationSettingType.EMERGENCY_COST);
        sql.append("  AND bw.strategyid").eq(strategy.getStrategyID());
        sql.append("ORDER BY bw.SettingName DESC");
        
        List<VoltageViolationSetting> settings = yukonJdbcTemplate.query(sql, voltageViolationSettingMapper);
        if(settings.isEmpty()) {
            settings = VoltageViolationSettingsHelper.getVoltageViolationDefaults();
        }
        
        return settings;
    }

    @Override
    public StrategyLimitsHolder getStrategyLimitsHolder(int strategyId) {
        CapControlStrategy strategy = getForId(strategyId);
        List<PeakTargetSetting> settings = strategy.getTargetSettings();
        double upperVoltLimit = 0.0;
        double lowerVoltLimit = 0.0;
        for(PeakTargetSetting setting : settings) {
            if(setting.getType() == TargetSettingType.UPPER_VOLT_LIMIT) {
                upperVoltLimit = Double.parseDouble(setting.getPeakValue());
            } else if(setting.getType() == TargetSettingType.LOWER_VOLT_LIMIT) {
                lowerVoltLimit = Double.parseDouble(setting.getPeakValue());
            }
            if (upperVoltLimit != 0.0 && lowerVoltLimit != 0.0) break;
        }
        return new StrategyLimitsHolder(strategy, upperVoltLimit, lowerVoltLimit);
    }

    private static YukonRowMapper<PeakTargetSetting> peakTargetSettingMapper = 
		new YukonRowMapper<PeakTargetSetting>() {
		@Override
        public PeakTargetSetting mapRow(YukonResultSet rs) throws SQLException {
			TargetSettingType targetSettingType = rs.getEnum("name", TargetSettingType.class);
            String peakValue = rs.getString("peakValue");
            String offPeakValue = rs.getString("offPeakValue");
            
            PeakTargetSetting setting = new PeakTargetSetting(targetSettingType, peakValue, offPeakValue);
            return setting;
        }
    };
    
    private static YukonRowMapper<VoltageViolationSetting> voltageViolationSettingMapper = 
            new YukonRowMapper<VoltageViolationSetting>() {
        @Override
        public VoltageViolationSetting mapRow(YukonResultSet rs) throws SQLException {
            VoltageViolationSettingNameType type = rs.getEnum("name", VoltageViolationSettingNameType.class);
            double bwValue = rs.getDouble("bwValue");
            double costValue = rs.getDouble("costValue");
            double emergencyCostValue = rs.getDouble("emergencyCostValue");
            
            VoltageViolationSetting setting = new VoltageViolationSetting(type, bwValue, costValue, emergencyCostValue);
            return setting;
        }
    };
    
    private static YukonRowMapper<PowerFactorCorrectionSetting> powerFactorCorrectionSettingMapper = 
            new YukonRowMapper<PowerFactorCorrectionSetting>() {
        @Override
        public PowerFactorCorrectionSetting mapRow(YukonResultSet rs) throws SQLException {
            return new PowerFactorCorrectionSetting(rs.getDouble("bwValue"),
                                                    rs.getDouble("costValue"),
                                                    rs.getDouble("maxCostValue"));
        }
    };
    
    private static void validateVoltageViolationSettings(List<VoltageViolationSetting> settings) {
        List<String> errors = Lists.newArrayList();
        for (VoltageViolationSetting setting : settings) {
            if (setting.getName() == VoltageViolationSettingNameType.LOW_VOLTAGE_VIOLATION) {
                if (setting.getCost() > 0) {
                    errors.add(VoltageViolationSettingNameType.LOW_VOLTAGE_VIOLATION.getDisplayName() + " Cost must be less than or equal to zero");
                }
                if (setting.getEmergencyCost() > setting.getCost()) {
                    errors.add(VoltageViolationSettingNameType.LOW_VOLTAGE_VIOLATION.getDisplayName() + " Emergency Cost must be less than or equal to cost");
                }
                if (setting.getBandwidth() <= 0) {
                    errors.add(VoltageViolationSettingNameType.LOW_VOLTAGE_VIOLATION.getDisplayName() + " Bandwidth must be greater than zero");
                }
            } else if (setting.getName() == VoltageViolationSettingNameType.HIGH_VOLTAGE_VIOLATION) {
                if (setting.getCost() < 0) {
                    errors.add(VoltageViolationSettingNameType.HIGH_VOLTAGE_VIOLATION.getDisplayName() + " Cost must be greater than or equal to zero");
                }
                if (setting.getEmergencyCost() < setting.getCost()) {
                    errors.add(VoltageViolationSettingNameType.HIGH_VOLTAGE_VIOLATION.getDisplayName() + " Emergency Cost must be greater than or equal to cost");
                }
                if (setting.getBandwidth() <= 0) {
                    errors.add(VoltageViolationSettingNameType.HIGH_VOLTAGE_VIOLATION.getDisplayName() + " Bandwidth must be greater than zero");
                }
            }
        }
        if (!errors.isEmpty()) throw new IllegalArgumentException(StringUtils.join(errors, ", "));
    }
    
    private static void validatePowerFactorCorrectionSetting(PowerFactorCorrectionSetting setting) {
        List<String> errors = Lists.newArrayList();
        if (setting.getBandwidth() < 0 || setting.getBandwidth() > 2) {
            errors.add("Bandwidth must be between zero and two");
        }
        if (setting.getCost() <= 0) {
            errors.add("Cost must be greater than zero");
        }
        if (setting.getMaxCost() < 0) {
            errors.add("Max Cost must be greater than or equal to zero");
        }
        if (!errors.isEmpty()) throw new IllegalArgumentException(StringUtils.join(errors, ", "));
    }
    
    public void afterPropertiesSet() throws Exception {
        strategyTemplate = new SimpleTableAccessTemplate<CapControlStrategy>(yukonJdbcTemplate, nextValueHelper);
        strategyTemplate.setTableName("CapControlStrategy");
        strategyTemplate.setPrimaryKeyField("StrategyId");
        strategyTemplate.setFieldMapper(strategyFieldMapper); 
    }
}