package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.google.common.collect.Maps;

public class LMGearDaoImpl implements LMGearDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
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
    
    @Override
    public void insertContainer(BeatThePeakGearContainer tgc){
        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.insertInto(beatThePeakTableName);
        params.addValue( "GearId", tgc.getGearId() );
        params.addValue( "AlertLevel" , tgc.getAlertLevel() );

        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateContainer(BeatThePeakGearContainer btpGearContainer){
        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.update(beatThePeakTableName);
        params.addValue("AlertLevel", btpGearContainer.getAlertLevel());
        sql.append("WHERE GearId").eq(btpGearContainer.getGearId());

        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public BeatThePeakGearContainer getContainer(int gearId) {    
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearId, AlertLevel");
        sql.append("FROM").append(beatThePeakTableName);
        sql.append("WHERE GearId").eq(gearId);
        
        BeatThePeakGearContainer btpgearContainer = yukonJdbcTemplate.queryForObject(sql, beatThePeakGearRowMapper);
        
        return btpgearContainer;
    }
    
    @Override
    public void deleteBeatThePeakGear(int gearId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(beatThePeakTableName);
        sql.append("WHERE GearId").eq(gearId);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public Map<Integer, LMProgramDirectGear> getByGearIds(Iterable<Integer> gearIds) {
        LMProgramDirectGearRowMapper gearMapper = new LMProgramDirectGearRowMapper();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(gearMapper.getBaseQuery());
        sql.append("WHERE GearId").in(gearIds);

        List<LMProgramDirectGear> gears = yukonJdbcTemplate.query(sql, gearMapper);

        Map<Integer, LMProgramDirectGear> gearMap = Maps.newHashMap();
        for (LMProgramDirectGear gear : gears) {
            gearMap.put(gear.getYukonID(), gear);
        }

        return gearMap;
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

            return gear;
        }
    }
    
}