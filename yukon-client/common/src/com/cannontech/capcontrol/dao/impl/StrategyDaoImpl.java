package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.joda.time.LocalTime;
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
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CapControlStrategy.DayOfWeek;
import com.cannontech.database.db.capcontrol.CapControlStrategy.EndDaySetting;
import com.cannontech.database.db.capcontrol.CommReportingPercentageSetting;
import com.cannontech.database.db.capcontrol.CommReportingPercentageSettingName;
import com.cannontech.database.db.capcontrol.CommReportingPercentageSettingType;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.PeaksTargetType;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSetting;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSettingName;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSettingType;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.database.db.capcontrol.VoltViolationType;
import com.cannontech.database.db.capcontrol.VoltageViolationSetting;
import com.cannontech.database.db.capcontrol.VoltageViolationSettingType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class StrategyDaoImpl implements StrategyDao {
    
    private final ParameterizedRowMapper<CapControlStrategy> rowMapper = new StrategyRowMapper();
    private SimpleTableAccessTemplate<CapControlStrategy> strategyTemplate;
    private static final YukonRowMapper<LiteCapControlStrategy> liteMapper = new YukonRowMapper<LiteCapControlStrategy>() {
        @Override
        public LiteCapControlStrategy mapRow(YukonResultSet rs) throws SQLException {
            
            LiteCapControlStrategy strategy = new LiteCapControlStrategy();
            strategy.setId(rs.getInt("StrategyId"));
            strategy.setName(rs.getString("StrategyName"));
            
            return strategy;
        }
    };
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private FieldMapper<CapControlStrategy> strategyFieldMapper = new FieldMapper<CapControlStrategy>() {

        @Override
        public void extractValues(MapSqlParameterSource p, CapControlStrategy o) {
            p.addValue("StrategyName", o.getName());
            p.addValue("ControlMethod", o.getControlMethod());
            p.addValue("MaxDailyOperation", o.getMaxDailyOperation());
            p.addValue("MaxOperationDisableFlag", !o.isMaxOperationEnabled());
            p.addValue("PeakStartTime", o.getPeakStartTime().getMillisOfDay() / 1000);
            p.addValue("PeakStopTime", o.getPeakStopTime().getMillisOfDay() / 1000);
            p.addValue("ControlInterval", o.getControlInterval());
            p.addValue("MinResponseTime", o.getMinResponseTime());
            p.addValue("MinConfirmPercent", o.getMinConfirmPercent());
            p.addValue("FailurePercent", o.getFailurePercent());
            
            /*
             * Build an 8 character string for Sun-Sat and Holiday
             * 'Y' in a position if that day is a peak day,
             * 'N' otherwise
             */
            StringBuilder dayOfWeek = new StringBuilder();
            for (DayOfWeek day : DayOfWeek.values()) {
                if (o.getPeakDays().get(day)) {
                    dayOfWeek.append("Y");
                } else {
                    dayOfWeek.append("N");
                }
            }
            
            p.addValue("DaysOfWeek", dayOfWeek.toString());
            p.addValue("ControlUnits", o.getAlgorithm());
            p.addValue("ControlDelayTime", o.getControlDelayTime());
            p.addValue("ControlSendRetries", o.getControlSendRetries());
            p.addValue("IntegrateFlag", o.isIntegrateFlag());
            p.addValue("IntegratePeriod", o.getIntegratePeriod());
            p.addValue("LikeDayFallBack", o.isLikeDayFallBack());
            p.addValue("EndDaySettings", o.getEndDaySettings());
        }

        @Override
        public Number getPrimaryKey(CapControlStrategy strategy) {
            return strategy.getId();
        }

        @Override
        public void setPrimaryKey(CapControlStrategy object, int strategyId) {
            object.setId(strategyId);
        }
    };
    
    @Override
    @Transactional
    public int add(String name) {
        
        CapControlStrategy strategy = new CapControlStrategy();
        strategy.setName(name);
        
        try {
            strategyTemplate.insert(strategy);
            savePeakSettings(strategy);
            saveVoltageViolationSettings(strategy);
            savePowerFactorCorrectionSetting(strategy);
            saveMinCommunicationPercentageSetting(strategy);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Strategy name already in use.");
        }
        
        return strategy.getId();
    }

    @Override
    @Transactional
    public boolean delete(int strategyId) {
        deleteStrategyAssignmentsByStrategyId(strategyId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CapControlStrategy WHERE StrategyId").eq(strategyId);
        int rowsAffected = jdbcTemplate.update(sql);

        return rowsAffected == 1;
    }

    private boolean deleteStrategyAssignmentsByStrategyId(int strategyId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CCSeasonStrategyAssignment WHERE StrategyId").eq(strategyId);
        int rowsAffected = jdbcTemplate.update(sql);

        return rowsAffected == 1;
    }
    
    @Override
    public LiteCapControlStrategy getLiteStrategy(int id) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StrategyId, StrategyName");
        sql.append("FROM CapControlStrategy");
        sql.append("WHERE StrategyId").eq(id);
        
        LiteCapControlStrategy strategy = jdbcTemplate.queryForObject(sql, liteMapper);
        
        return strategy;
    }
    
    @Override
    public List<LiteCapControlStrategy> getAllLiteStrategies() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StrategyId, StrategyName");
        sql.append("FROM CapControlStrategy");
        sql.append("ORDER BY StrategyName");
        
        return jdbcTemplate.query(sql, liteMapper);
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
        
        CapControlStrategy strategy =  jdbcTemplate.queryForObject(sql, rowMapper);
        strategy.setTargetSettings(getPeakSettings(strategy));
        strategy.setVoltageViolationSettings(getVoltageViolationSettings(strategy));
        strategy.setPowerFactorCorrectionSetting(getPowerFactorCorrectionSetting(strategy));
        strategy.setMinCommunicationPercentageSetting(getMinCommunicationPercentageSetting(strategy));
        
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
        
        List<CapControlStrategy> strategies = jdbcTemplate.query(sql, rowMapper);
        
        for(CapControlStrategy strategy : strategies) {
            strategy.setTargetSettings(getPeakSettings(strategy));
            strategy.setVoltageViolationSettings(getVoltageViolationSettings(strategy));
            strategy.setPowerFactorCorrectionSetting(getPowerFactorCorrectionSetting(strategy));
            strategy.setMinCommunicationPercentageSetting(getMinCommunicationPercentageSetting(strategy));
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
            strategy.setId( rs.getInt("StrategyID"));
            strategy.setName( rs.getString("StrategyName") );
            strategy.setControlMethod( ControlMethod.valueOf(rs.getString("ControlMethod")) );
            strategy.setMaxDailyOperation( rs.getInt("MaxDailyOperation") );
            strategy.setMaxOperationEnabled(!rs.getBoolean("MaxOperationDisableFlag"));
            
            int peakStartSeconds = rs.getInt("PeakStartTime");
            LocalTime peakStart = LocalTime.fromMillisOfDay(peakStartSeconds * 1000);
            strategy.setPeakStartTime(peakStart);
            
            int peakStopSeconds = rs.getInt("PeakStopTime");
            if (peakStopSeconds > CapControlStrategy.MAX_TIME.getMillisOfDay() / 1000) {
                peakStopSeconds = CapControlStrategy.MAX_TIME.getMillisOfDay() / 1000;
            }
            LocalTime peakStop = LocalTime.fromMillisOfDay(peakStopSeconds * 1000);
            strategy.setPeakStopTime(peakStop);
            
            strategy.setControlInterval( rs.getInt("ControlInterval") );
            strategy.setMinResponseTime( rs.getInt("MinResponseTime") );
            strategy.setMinConfirmPercent( rs.getInt("MinConfirmPercent") );
            strategy.setFailurePercent( rs.getInt("FailurePercent") );

            /*
             * Expecting 8 character sequence of 'Y' and 'N', for Sun-Sat and Holiday
             * We convert this to a Map for each day of the week 
             */
            String daysOfWeek = rs.getString("DaysOfWeek");
            Map<DayOfWeek, Boolean> peakDays = new HashMap<>();
            
            int index = 0;
            for (DayOfWeek day : DayOfWeek.values()) {
                if (daysOfWeek.charAt(index) == 'Y') {
                    peakDays.put(day, true);
                } else {
                    peakDays.put(day, false);
                }
                index++;
            }
            
            strategy.setPeakDays(peakDays);
            strategy.setAlgorithm( ControlAlgorithm.valueOf(rs.getString("ControlUnits")) );
            strategy.setControlDelayTime( rs.getInt("ControlDelayTime") );
            strategy.setControlSendRetries( rs.getInt("ControlSendRetries") );
            strategy.setIntegrateFlag(rs.getBoolean("IntegrateFlag"));
            strategy.setIntegratePeriod(rs.getInt("IntegratePeriod"));
            strategy.setLikeDayFallBack(rs.getBoolean("LikeDayFallBack"));
            strategy.setEndDaySettings(EndDaySetting.from(rs.getString("EndDaySettings")));
            return strategy;
        }

    }

    @Override
    public List<String> getAllPaoNamesUsingStrategyAssignment(int strategyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaoName");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE PAObjectID  IN (");
        sql.append("  SELECT PAObjectId FROM CCHolidayStrategyAssignment");
        sql.append("  WHERE StrategyId").eq(strategyId);
        sql.append("  UNION");
        sql.append("  SELECT paobjectid FROM CCSeasonStrategyAssignment");
        sql.append("  WHERE strategyid").eq(strategyId);
        sql.append(")");
        
        return jdbcTemplate.query(sql, RowMapper.STRING);
    }
    
    @Override
    @Transactional
    public void save(CapControlStrategy strategy) {
        
        strategyTemplate.update(strategy);
        savePeakSettings(strategy);
        saveVoltageViolationSettings(strategy);
        savePowerFactorCorrectionSetting(strategy);
        saveMinCommunicationPercentageSetting(strategy);
    }
    
    @Transactional
    private void savePeakSettings(CapControlStrategy strategy) {
        
        Map<TargetSettingType, PeakTargetSetting> targetSettings = strategy.getTargetSettings();
        int strategyId = strategy.getId();
        
        PeaksTargetType peak;
        PeaksTargetType offpeak;
        if(strategy.isTimeOfDay()){
            peak = PeaksTargetType.WEEKDAY;
            offpeak = PeaksTargetType.WEEKEND;
        } else {
            peak = PeaksTargetType.PEAK;
            offpeak = PeaksTargetType.OFFPEAK;
        }
        
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId").eq(strategyId);
        deleteSql.append("AND SettingType").in(Lists.newArrayList(PeaksTargetType.values()));
        jdbcTemplate.update(deleteSql.getSql(), deleteSql.getArguments());
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        for(Entry<TargetSettingType, PeakTargetSetting> entry : targetSettings.entrySet()) {
            
            TargetSettingType type = entry.getKey();
            PeakTargetSetting setting = entry.getValue();
            
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, type, setting.getPeakValue(), peak);
            sql.append(";");
            
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, type, setting.getOffPeakValue(), offpeak);
            sql.append(";");
        }
        jdbcTemplate.update(sql);
    }

    @Transactional
    private void saveVoltageViolationSettings(CapControlStrategy strategy) {
        int strategyId = strategy.getId();
        
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId").eq(strategyId);
        deleteSql.append("AND SettingType").in(Lists.newArrayList(VoltageViolationSettingType.values()));
        jdbcTemplate.update(deleteSql);
        
        if (!strategy.isIvvc()) {
            return; // Don't save these IVVC-only settings if we aren't going to use them
        }

        Map<VoltViolationType, VoltageViolationSetting> targetSettings = strategy.getVoltageViolationSettings();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        for (Entry<VoltViolationType, VoltageViolationSetting> entry : targetSettings.entrySet()) {
            
            VoltViolationType type = entry.getKey();
            VoltageViolationSetting setting = entry.getValue();
            
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, type, setting.getBandwidth(), VoltageViolationSettingType.BANDWIDTH);
            sql.append(";");

            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, type, setting.getCost(), VoltageViolationSettingType.COST);
            sql.append(";");
            
            sql.append("INSERT INTO CCStrategyTargetSettings");
            sql.values(strategyId, type, setting.getEmergencyCost(), VoltageViolationSettingType.EMERGENCY_COST);
            sql.append(";");
        }
        jdbcTemplate.update(sql);
    }
    
    @Transactional
    private void savePowerFactorCorrectionSetting(CapControlStrategy strategy) {
        int strategyId = strategy.getId();
        
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId").eq(strategyId);
        deleteSql.append("AND SettingName").eq_k(PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION);
        jdbcTemplate.update(deleteSql);
        
        if (!strategy.isIvvc())
         {
            return; // Don't save these IVVC-only settings if we aren't going to use them
        }
        
        PowerFactorCorrectionSetting setting = strategy.getPowerFactorCorrectionSetting();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        /* Bandwidth */
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION,
                   setting.getBandwidth(),
                   PowerFactorCorrectionSettingType.BANDWIDTH);
        sql.append(";");

        /* cost */
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION,
                   setting.getCost(),
                   PowerFactorCorrectionSettingType.COST);
        sql.append(";");
        
        /* max cost */
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION,
                   setting.getMaxCost(),
                   PowerFactorCorrectionSettingType.MAX_COST);
        sql.append(";");

        jdbcTemplate.update(sql);
    }
    
    @Transactional
    private void saveMinCommunicationPercentageSetting(CapControlStrategy strategy) {
        int strategyId = strategy.getId();
        
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM CCStrategyTargetSettings WHERE strategyId").eq(strategyId);
        deleteSql.append("AND SettingName").eq_k(CommReportingPercentageSettingName.COMM_REPORTING_PERCENTAGE);
        jdbcTemplate.update(deleteSql);
        
        if (!strategy.isIvvc())
         {
            return; // Don't save these IVVC-only settings if we aren't going to use them
        }
        
        CommReportingPercentageSetting setting = strategy.getMinCommunicationPercentageSetting();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        /* Banks */
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   CommReportingPercentageSettingName.COMM_REPORTING_PERCENTAGE,
                   setting.getBanksReportingRatio(),
                   CommReportingPercentageSettingType.CAPBANK);
        sql.append(";");
        
        /* Regulators */
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   CommReportingPercentageSettingName.COMM_REPORTING_PERCENTAGE,
                   setting.getRegulatorReportingRatio(),
                   CommReportingPercentageSettingType.REGULATOR);
        sql.append(";");
        
        /* Additional Points */
        sql.append("INSERT INTO CCStrategyTargetSettings");
        sql.values(strategyId,
                   CommReportingPercentageSettingName.COMM_REPORTING_PERCENTAGE,
                   setting.getVoltageMonitorReportingRatio(),
                   CommReportingPercentageSettingType.VOLTAGE_MONITOR);
        sql.append(";");

        jdbcTemplate.update(sql);
    }

    private Map<TargetSettingType, PeakTargetSetting> getPeakSettings(CapControlStrategy strategy) {
        
        PeaksTargetType peak = PeaksTargetType.PEAK;
        PeaksTargetType offpeak = PeaksTargetType.OFFPEAK;
        if(strategy.isTimeOfDay()){
            peak = PeaksTargetType.WEEKDAY;
            offpeak = PeaksTargetType.WEEKEND;
        } 
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT peaks.SettingName name, peaks.SettingValue peakValue, offpeaks.SettingValue offPeakValue");
        sql.append("FROM CCStrategyTargetSettings peaks, CCStrategyTargetSettings offpeaks");
        sql.append("WHERE peaks.SettingName = offpeaks.SettingName");
        sql.append("  AND peaks.strategyid = offpeaks.strategyid");
        sql.append("  AND peaks.SettingType").eq(peak);
        sql.append("  AND offpeaks.SettingType").eq(offpeak);
        sql.append("  AND peaks.strategyid").eq(strategy.getId());
        
        PeakTargetMapper mapper = new PeakTargetMapper();
        
        jdbcTemplate.query(sql, mapper);
        
        Map<TargetSettingType, PeakTargetSetting> settings = mapper.getSettings();
        
        if(settings.isEmpty()) {
            settings = StrategyPeakSettingsHelper.getSettingDefaults(strategy.getAlgorithm());
        }
        
        return settings;
    }
    
    private PowerFactorCorrectionSetting getPowerFactorCorrectionSetting(CapControlStrategy strategy) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT bw.SettingValue bwValue, cost.SettingValue costValue, maxCost.SettingValue maxCostValue");
        sql.append("FROM CCStrategyTargetSettings bw, CCStrategyTargetSettings cost, CCStrategyTargetSettings maxCost");
        sql.append("WHERE bw.SettingName = cost.SettingName");
        sql.append("  AND bw.SettingName = maxCost.SettingName");
        sql.append("  AND bw.SettingName").eq_k(PowerFactorCorrectionSettingName.POWER_FACTOR_CORRECTION);
        sql.append("  AND bw.strategyid = cost.strategyid");
        sql.append("  AND bw.strategyid = maxCost.strategyid");
        sql.append("  AND bw.strategyid").eq(strategy.getId());
        sql.append("  AND bw.SettingType").eq_k(PowerFactorCorrectionSettingType.BANDWIDTH);
        sql.append("  AND cost.SettingType").eq_k(PowerFactorCorrectionSettingType.COST);
        sql.append("  AND maxCost.SettingType").eq_k(PowerFactorCorrectionSettingType.MAX_COST);
        
        try {
            return jdbcTemplate.queryForObject(sql, powerFactorCorrectionSettingMapper);
        } catch (EmptyResultDataAccessException e) {
            return new PowerFactorCorrectionSetting();
        }
    }
    
    private CommReportingPercentageSetting getMinCommunicationPercentageSetting(CapControlStrategy strategy) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT banks.SettingValue banksValue, regulator.SettingValue regulatorValue, voltageMonitor.SettingValue voltageMonitorValue");
        sql.append("FROM CCStrategyTargetSettings banks, CCStrategyTargetSettings regulator, CCStrategyTargetSettings voltageMonitor");
        sql.append("WHERE banks.SettingName = regulator.SettingName");
        sql.append("  AND banks.SettingName = voltageMonitor.SettingName");
        sql.append("  AND banks.SettingName").eq_k(CommReportingPercentageSettingName.COMM_REPORTING_PERCENTAGE);
        sql.append("  AND banks.strategyid = regulator.strategyid");
        sql.append("  AND banks.strategyid = voltageMonitor.strategyid");
        sql.append("  AND banks.strategyid").eq(strategy.getId());
        sql.append("  AND banks.SettingType").eq_k(CommReportingPercentageSettingType.CAPBANK);
        sql.append("  AND regulator.SettingType").eq_k(CommReportingPercentageSettingType.REGULATOR);
        sql.append("  AND voltageMonitor.SettingType").eq_k(CommReportingPercentageSettingType.VOLTAGE_MONITOR);
        
        try {
            return jdbcTemplate.queryForObject(sql, minCommunicationPercentageSettingMapper);
        } catch (EmptyResultDataAccessException e) {
            return new CommReportingPercentageSetting();
        }
    }
    
    private Map<VoltViolationType, VoltageViolationSetting> getVoltageViolationSettings(CapControlStrategy strategy) {
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
        sql.append("  AND bw.strategyid").eq(strategy.getId());
        sql.append("ORDER BY bw.SettingName DESC");
        
        VoltageViolationsMapper mapper = new VoltageViolationsMapper();
        
        jdbcTemplate.query(sql, mapper);
        
        Map<VoltViolationType, VoltageViolationSetting> settings = mapper.getSettings();
        
        return settings;
    }

    @Override
    public StrategyLimitsHolder getStrategyLimitsHolder(int strategyId) {
        CapControlStrategy strategy = getForId(strategyId);
        Map<TargetSettingType, PeakTargetSetting> settings = strategy.getTargetSettings();
        double upperVoltLimit = 0.0;
        double lowerVoltLimit = 0.0;
        
        PeakTargetSetting upperLimitSetting = settings.get(TargetSettingType.UPPER_VOLT_LIMIT);
        if (upperLimitSetting != null) {
            upperVoltLimit = upperLimitSetting.getPeakValue();
        }
        
        PeakTargetSetting lowerLimitSetting = settings.get(TargetSettingType.LOWER_VOLT_LIMIT);
        if (lowerLimitSetting != null) {
            lowerVoltLimit = lowerLimitSetting.getPeakValue();
        }
 
        return new StrategyLimitsHolder(strategy, upperVoltLimit, lowerVoltLimit);
    }

    private static class PeakTargetMapper implements YukonRowCallbackHandler {
        
        Map<TargetSettingType, PeakTargetSetting> settings = new HashMap<>();

        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            
            TargetSettingType type = rs.getEnum("name", TargetSettingType.class);
            double peakValue = rs.getDouble("peakValue");
            double offPeakValue = rs.getDouble("offPeakValue");
            
            PeakTargetSetting setting = new PeakTargetSetting(peakValue, offPeakValue);
            
            settings.put(type, setting);
        }
        
        public Map<TargetSettingType, PeakTargetSetting> getSettings() {
            return settings;
        }
        
    }
    
    private static class VoltageViolationsMapper implements YukonRowCallbackHandler {
        
        Map<VoltViolationType, VoltageViolationSetting> settings = new HashMap<>();

        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            
            VoltViolationType type = rs.getEnum("name", VoltViolationType.class);
            double bwValue = rs.getDouble("bwValue");
            double costValue = rs.getDouble("costValue");
            double emergencyCostValue = rs.getDouble("emergencyCostValue");
            
            VoltageViolationSetting setting = new VoltageViolationSetting(bwValue, costValue, emergencyCostValue);
            
            settings.put(type, setting);
        }
        
        public Map<VoltViolationType, VoltageViolationSetting> getSettings() {
            return settings;
        }
        
    }
    
    private static YukonRowMapper<PowerFactorCorrectionSetting> powerFactorCorrectionSettingMapper = 
            new YukonRowMapper<PowerFactorCorrectionSetting>() {
        @Override
        public PowerFactorCorrectionSetting mapRow(YukonResultSet rs) throws SQLException {
            return new PowerFactorCorrectionSetting(rs.getDouble("bwValue"),
                                                    rs.getDouble("costValue"),
                                                    rs.getDouble("maxCostValue"));
        }
    };
    
    private static YukonRowMapper<CommReportingPercentageSetting> minCommunicationPercentageSettingMapper = 
            new YukonRowMapper<CommReportingPercentageSetting>() {
        @Override
        public CommReportingPercentageSetting mapRow(YukonResultSet rs) throws SQLException {
            return new CommReportingPercentageSetting(rs.getDouble("banksValue"),
                                                         rs.getDouble("regulatorValue"),
                                                         rs.getDouble("voltageMonitorValue"));
        }
    };
    
    @PostConstruct
    public void init() throws Exception {
        strategyTemplate = new SimpleTableAccessTemplate<CapControlStrategy>(jdbcTemplate, nextValueHelper);
        strategyTemplate.setTableName("CapControlStrategy");
        strategyTemplate.setPrimaryKeyField("StrategyId");
        strategyTemplate.setFieldMapper(strategyFieldMapper); 
    }

    @Override
    public boolean isUniqueName(String name) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM CapControlStrategy");
        sql.append("WHERE StrategyName").eq(name);
        
        int duplicateNames =  jdbcTemplate.queryForInt(sql);
        return duplicateNames != 0;
    }

}