package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.lm.HeatCool;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.dr.ThermostatRampRateValues;
import com.cannontech.dr.itron.model.ItronCycleType;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.cannontech.loadcontrol.gear.model.EcobeeSetpointValues;
import com.cannontech.loadcontrol.gear.model.LMThermostatGear;
import com.google.common.collect.Maps;

public class LMGearDaoImpl implements LMGearDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ChunkingSqlTemplate chunkingSqlTemplate;
    private final static String beatThePeakTableName = "LMBeatThePeakGear";

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
            gearMap.put(gear.getGearId(), gear);
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
            gearMap.put(gear.getGearId(), gear);
        }

        return gearMap;
    }

    @Override
    public LMProgramDirectGear getByGearId(Integer gearId) {
        return getByGearIds(Collections.singleton(gearId)).get(gearId);
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

    @Override
    public LoadShapingOptions getLoadShapingOptions(Integer gearId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreparationOption, PeakOption, PostPeakOption");
        sql.append("FROM LMNestLoadShapingGear");
        sql.append("WHERE GearId").eq(gearId);

        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<LoadShapingOptions>() {
            @Override
            public LoadShapingOptions mapRow(YukonResultSet rs) throws SQLException {

                PrepLoadShape prepLoadShape = rs.getEnum("PreparationOption", PrepLoadShape.class);
                PeakLoadShape peakLoadShape = rs.getEnum("PeakOption", PeakLoadShape.class);
                PostLoadShape postLoadShape = rs.getEnum("PostPeakOption", PostLoadShape.class);

                LoadShapingOptions loadShapingOptions = new LoadShapingOptions(prepLoadShape, peakLoadShape, postLoadShape);

                return loadShapingOptions;
            }
        });
    }

    @Override
    public ItronCycleType getItronCycleType(Integer gearId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CycleOption");
        sql.append("FROM LMItronCycleGear");
        sql.append("WHERE GearId").eq(gearId);

        String itronCycle = jdbcTemplate.queryForString(sql);

        return ItronCycleType.valueOf(itronCycle);
    }

    @Override
    public EcobeeSetpointValues getEcobeeSetpointValues(Integer gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Settings, MaxValue");
        sql.append("FROM LMThermostatGear");
        sql.append("WHERE GearId").eq(gearId);

        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<EcobeeSetpointValues>() {
            @Override
            public EcobeeSetpointValues mapRow(YukonResultSet rs) throws SQLException {

                String heatCool = rs.getString("Settings");
                Integer setpointOffset = rs.getInt("MaxValue");

                EcobeeSetpointValues ecobeeSetpointValues = new EcobeeSetpointValues(setpointOffset, HeatCool.of(heatCool));

                return ecobeeSetpointValues;
            }
        });
    }

    @Override
    public LMThermostatGear getLMThermostatGear(Integer gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM LMThermostatGear");
        sql.append("WHERE GearId").eq(gearId);

        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<LMThermostatGear>() {
            @Override
            public LMThermostatGear mapRow(YukonResultSet rs) throws SQLException {
                LMThermostatGear thermostatGear = new LMThermostatGear();
                thermostatGear.setSettings(rs.getString("Settings"));
                thermostatGear.setMinValue(rs.getInt("MinValue"));
                thermostatGear.setMaxValue(rs.getInt("MaxValue"));
                thermostatGear.setValueB(rs.getInt("ValueB"));
                thermostatGear.setValueD(rs.getInt("ValueD"));
                thermostatGear.setValueF(rs.getInt("ValueF"));
                thermostatGear.setValueTa(rs.getInt("ValueTa"));
                thermostatGear.setValueTb(rs.getInt("ValueTb"));
                thermostatGear.setValueTc(rs.getInt("ValueTc"));
                thermostatGear.setValueTd(rs.getInt("ValueTd"));
                thermostatGear.setValueTe(rs.getInt("ValueTe"));
                thermostatGear.setValueTf(rs.getInt("ValueTf"));
                thermostatGear.setRampRate(rs.getFloat("RampRate"));
                thermostatGear.setRandom(rs.getInt("Random"));

                return thermostatGear;
            }
        });
    }
}