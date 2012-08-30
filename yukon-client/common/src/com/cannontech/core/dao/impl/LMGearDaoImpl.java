package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;

public class LMGearDaoImpl implements LMGearDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private final static String tableName = "LMBeatThePeakGear";
    
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

        SqlParameterSink params = sql.insertInto(tableName);
        params.addValue( "GearId", tgc.getGearId() );
        params.addValue( "AlertLevel" , tgc.getAlertLevel() );

        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateContainer(BeatThePeakGearContainer btpGearContainer){
        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.update(tableName);
        params.addValue("AlertLevel", btpGearContainer.getAlertLevel());
        sql.append("WHERE GearId").eq(btpGearContainer.getGearId());

        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public BeatThePeakGearContainer getContainer(int gearId) {    
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearId, AlertLevel");
        sql.append("FROM " + tableName);
        sql.append("WHERE GearId").eq(gearId);
        
        BeatThePeakGearContainer btpgearContainer = yukonJdbcTemplate.queryForObject(sql, beatThePeakGearRowMapper);
        
        return btpgearContainer;
    }
    
    @Override
    public void delete(int gearId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + tableName);
        sql.append("WHERE GearId").eq(gearId);
        yukonJdbcTemplate.update(sql);
    }
}