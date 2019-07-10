package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.ThermostatRampRateValues;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.google.common.collect.Maps;

public class LMGearDaoImpl implements LMGearDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ChunkingSqlTemplate chunkingSqlTemplate;
    private final static String beatThePeakTableName = "LMBeatThePeakGear";
    private final static String tableName = "LMProgramDirectGear";
    
    public static YukonRowMapper<BeatThePeakGearContainer> beatThePeakGearRowMapper = new YukonRowMapper<BeatThePeakGearContainer>() {
        @Override
        public BeatThePeakGearContainer mapRow(YukonResultSet rs) throws SQLException {
            BeatThePeakGearContainer btpContainer = new BeatThePeakGearContainer();
            
            int gearId = rs.getInt("GearId");
            String alertLevel = rs.getString("AlertLevel");
            btpContainer.setGearId(gearId);
            btpContainer.setAlertLevel(alertLevel);
            
            return btpContainer;
        }
    };
    
    @PostConstruct
    public void init() {
        chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
    }
    
    @Override
    public void insertContainer(BeatThePeakGearContainer tgc){
        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.insertInto(beatThePeakTableName);
        params.addValue( "GearId", tgc.getGearId() );
        params.addValue( "AlertLevel" , tgc.getAlertLevel() );

        jdbcTemplate.update(sql);
    }
    
    @Override
    public void updateContainer(BeatThePeakGearContainer btpGearContainer){
        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.update(beatThePeakTableName);
        params.addValue("AlertLevel", btpGearContainer.getAlertLevel());
        sql.append("WHERE GearId").eq(btpGearContainer.getGearId());

        jdbcTemplate.update(sql);
    }
    
    @Override
    public BeatThePeakGearContainer getContainer(int gearId) {    
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearId, AlertLevel");
        sql.append("FROM").append(beatThePeakTableName);
        sql.append("WHERE GearId").eq(gearId);
        
        BeatThePeakGearContainer btpgearContainer = jdbcTemplate.queryForObject(sql, beatThePeakGearRowMapper);
        
        return btpgearContainer;
    }
    
    @Override
    public void deleteBeatThePeakGear(int gearId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(beatThePeakTableName);
        sql.append("WHERE GearId").eq(gearId);
        jdbcTemplate.update(sql);
    }

    @Override
    public Map<Integer, LMProgramDirectGear> getAllGears() {
        LMProgramDirectGearRowMapper gearMapper = new LMProgramDirectGearRowMapper();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(gearMapper.getBaseQuery());

        List<LMProgramDirectGear> gears = jdbcTemplate.query(sql, gearMapper);

        Map<Integer, LMProgramDirectGear> gearMap = Maps.newHashMap();
        for (LMProgramDirectGear gear : gears) {
            gearMap.put(gear.getYukonID(), gear);
        }

        return gearMap;
    }

    @Override
    public Map<Integer, LMProgramDirectGear> getByGearIds(Iterable<Integer> gearIds) {
        LMProgramDirectGearRowMapper gearMapper = new LMProgramDirectGearRowMapper();

        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(gearMapper.getBaseQuery());
                sql.append("WHERE GearId").in(subList);
                return sql;
            }
            
        };
        
        List<LMProgramDirectGear> gears = chunkingSqlTemplate.query(sqlFragmentGenerator, gearIds, gearMapper);

        Map<Integer, LMProgramDirectGear> gearMap = Maps.newHashMap();
        for (LMProgramDirectGear gear : gears) {
            gearMap.put(gear.getYukonID(), gear);
        }

        return gearMap;
    }

    @Override
    public LMProgramDirectGear getByGearId(Integer gearId) {
        return getByGearIds(Collections.singleton(gearId)).get(gearId);
    }

    private static class LMProgramDirectGearRowMapper extends AbstractRowMapperWithBaseQuery<LMProgramDirectGear> {
        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT * FROM").append(tableName);
            return retVal;
        }

        @Override
        public LMProgramDirectGear mapRow(YukonResultSet rs) throws SQLException {
            LMProgramDirectGear gear = new LMProgramDirectGear();

            gear.setChangeCondition(rs.getString("ChangeCondition"));
            gear.setChangeDuration(rs.getInt("ChangeDuration"));
            gear.setChangePriority(rs.getInt("ChangePriority"));
            gear.setChangeTriggerNumber(rs.getInt("ChangeTriggerNumber"));
            gear.setChangeTriggerOffset(rs.getDouble("ChangeTriggerOffset"));
            gear.setControlMethod(rs.getEnum("ControlMethod", GearControlMethod.class));
            gear.setCycleRefreshRate(rs.getInt("CycleRefreshRate"));
            gear.setGearName(rs.getString("GearName"));
            gear.setGearNumber(rs.getInt("GearNumber"));
            gear.setGroupSelectionMethod(rs.getString("GroupSelectionMethod"));
            gear.setKwReduction(rs.getDouble("kwReduction"));
            gear.setMethodOptionMax(rs.getInt("MethodOptionMax"));
            gear.setMethodOptionType(rs.getString("MethodOptionType"));
            gear.setMethodPeriod(rs.getInt("MethodPeriod"));
            gear.setMethodRate(rs.getInt("MethodRate"));
            gear.setMethodRateCount(rs.getInt("MethodRateCount"));
            gear.setMethodStopType(rs.getString("MethodStopType"));
            gear.setRampInInterval(rs.getInt("RampInInterval"));
            gear.setPercentReduction(rs.getInt("PercentReduction"));
            gear.setRampInPercent(rs.getInt("RampInPercent"));
            gear.setRampOutInterval(rs.getInt("RampOutInterval"));
            gear.setRampOutPercent(rs.getInt("RampOutPercent"));
            gear.setYukonID(rs.getInt("GearId"));
            gear.setDeviceId(rs.getInt("DeviceID"));

            return gear;
        }
    }
    
    public ThermostatRampRateValues getThermostatGearRampRateValues(int gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ValueD, ValueTd");
        sql.append("FROM LMThermoStatGear");
        sql.append("WHERE GearId").eq(gearId);
        
        return jdbcTemplate.queryForLimitedResults(sql, new YukonRowMapper<ThermostatRampRateValues>() {
            @Override
            public ThermostatRampRateValues mapRow(YukonResultSet rs) throws SQLException {
                return new ThermostatRampRateValues(rs.getInt("ValueD"), rs.getInt("ValueTd"));
            }
        }, 1).get(0);
    }
    
    public Double getSimpleThermostatGearRampRate(int gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RampRate");
        sql.append("FROM LMThermoStatGear");
        sql.append("WHERE GearId").eq(gearId);
        
        return jdbcTemplate.queryForLimitedResults(sql, new YukonRowMapper<Double>() {
            @Override
            public Double mapRow(YukonResultSet rs) throws SQLException {
                return rs.getDouble("RampRate");
            }
        }, 1).get(0);
    }
    
    public String getGearName(int gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearName");
        sql.append("FROM LMProgramDirectGear");
        sql.append("WHERE GearId").eq(gearId);
        
        return jdbcTemplate.queryForString(sql);
    }
    
    @Override
    public List<LiteGear> getAllLiteGears() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select GearId, GearName, ControlMethod, DeviceId, GearNumber");
        sql.append("from LmProgramDirectGear");
        sql.append("where GearId >= 0");
        sql.append("order by DeviceId");
        
        return jdbcTemplate.query(sql, new YukonRowMapper<LiteGear>() {
            @Override
            public LiteGear mapRow(YukonResultSet rs) throws SQLException {
                LiteGear gear = new LiteGear(rs.getInt("GearId"));
                gear.setGearName(rs.getString("GearName"));
                gear.setGearType(rs.getString("ControlMethod"));
                gear.setOwnerID(rs.getInt("DeviceId"));
                gear.setGearNumber(rs.getInt("GearNumber"));
                return gear;
            }
        });
    }
    
    @Override
    public List<LiteGear> getAllLiteGears(Integer programId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearId, GearName, ControlMethod, program.DeviceId, GearNumber");
        sql.append("FROM LmProgramDirectGear gear JOIN LMProgramDirect program ON gear.DeviceID = program.DeviceId");
        sql.append("WHERE program.DeviceId").eq(programId);

        return jdbcTemplate.query(sql, new YukonRowMapper<LiteGear>() {
            @Override
            public LiteGear mapRow(YukonResultSet rs) throws SQLException {
                LiteGear gear = new LiteGear(rs.getInt("GearId"));
                gear.setGearName(rs.getString("GearName"));
                gear.setGearType(rs.getString("ControlMethod"));
                gear.setOwnerID(rs.getInt("DeviceId"));
                gear.setGearNumber(rs.getInt("GearNumber"));
                return gear;
            }
        });
    }
}