package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.loadcontrol.gear.model.TierGearContainer;

public class LMGearDaoImpl implements LMGearDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private final static String tableName = "LMTierGear";
    
    public static YukonRowMapper<TierGearContainer> tierGearRowMapper = new YukonRowMapper<TierGearContainer>() {
        @Override
        public TierGearContainer mapRow(YukonResultSet rs) throws SQLException {
            TierGearContainer tgc = new TierGearContainer();
            
            int gearId = rs.getInt("GearId");
            int tier= rs.getInt("Tier");
            tgc.setGearId(gearId);
            tgc.setTier(tier);
            
            return tgc;
        }
    };
        
    public void insertContainer(TierGearContainer tgc){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO " + tableName + " (GearId, Tier)");
        sql.append(" VALUES (");
        sql.append(tgc.getGearId() + ",");
        sql.append(tgc.getTier() + ")");
        yukonJdbcTemplate.update(sql);
    }
    
    public void updateContainer(TierGearContainer tgc){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE " + tableName);
        sql.append(" SET Tier =");
        sql.append(tgc.getTier());
        sql.append(" WHERE GearId =");
        sql.append(tgc.getGearId());
        yukonJdbcTemplate.update(sql);
    }
    
    public TierGearContainer getContainer(int gearId) {    
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearId, Tier");
        sql.append(" FROM " + tableName);
        sql.append(" WHERE GearId =");
        sql.append(gearId);
        
        TierGearContainer tgc = yukonJdbcTemplate.queryForObject(sql, tierGearRowMapper);
        
        return tgc;
    }
    
    public void delete(int gearId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + tableName);
        sql.append(" WHERE GearId =");
        sql.append(gearId);
        yukonJdbcTemplate.update(sql);
    }
    
       
    public Integer getTierByGearId(int gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Tier");
        sql.append(" FROM " + tableName);
        sql.append(" WHERE GearId =");
        sql.append(gearId);
        Integer tier =  yukonJdbcTemplate.queryForInt(sql);
        return tier;
    }
}