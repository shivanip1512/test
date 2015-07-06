package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public class StaticLoadGroupMappingDaoImpl implements StaticLoadGroupMappingDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final StarsStaticLoadGroupRowMapper mapper = new StarsStaticLoadGroupRowMapper();
    
    @Override
    public StarsStaticLoadGroupMapping getStaticLoadGroupMapping(StarsStaticLoadGroupMapping criteria) {
        
        StarsStaticLoadGroupMapping loadGroup = null;
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT LoadGroupId, ApplianceCategoryId, ZipCode, ConsumptionTypeId, SwitchTypeId");
        sql.append("FROM StaticLoadGroupMapping");
        sql.append("WHERE ApplianceCategoryId").eq(criteria.getApplianceCategoryID());
        sql.append("AND ZipCode").startsWith(criteria.getZipCode());
        sql.append("AND ConsumptionTypeId").eq(criteria.getConsumptionTypeID());
        sql.append("AND SwitchTypeId").eq(criteria.getSwitchTypeID());
        
        List<StarsStaticLoadGroupMapping> loadGroups = jdbcTemplate.query(sql, mapper);
        
        if (loadGroups.size() > 0) {
            loadGroup = loadGroups.get(0);
        }
        
        return loadGroup;
    }
    
    @Override
    public List<Integer> getAllIds() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT LoadGroupId");
        sql.append("FROM StaticLoadGroupMapping");
        
        List<Integer> ids = jdbcTemplate.query(sql, RowMapper.INTEGER);
        
        return ids;
    }
    
    @Override
    public List<StarsStaticLoadGroupMapping> getAll() {
        
        List<StarsStaticLoadGroupMapping> groups = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT slg.LoadGroupId, slg.ApplianceCategoryId,");
        sql.append("slg.ZipCode, slg.ConsumptionTypeId, slg.SwitchTypeId, ypo.PAOName");
        sql.append("FROM StaticLoadGroupMapping slg");
        sql.append("JOIN YukonPAObject ypo on ypo.PAObjectId = slg.LoadGroupId");
        
        jdbcTemplate.query(sql, mapper);
        
        return groups;
    }
    
    @Override
    public List<Integer> getLoadGroupIdsForApplianceCategory( int applianceCategoryId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT loadGroupId FROM StaticLoadGroupMapping");
        sql.append("WHERE applianceCategoryId").eq(applianceCategoryId);
        
        return jdbcTemplate.query(sql, RowMapper.INTEGER);
    }
    
    public static class StarsStaticLoadGroupRowMapper implements YukonRowMapper<StarsStaticLoadGroupMapping> {
        
        @Override
        public StarsStaticLoadGroupMapping mapRow(YukonResultSet rs) throws SQLException {
            
            StarsStaticLoadGroupMapping staticLoadGroup = new StarsStaticLoadGroupMapping();
            staticLoadGroup.setLoadGroupName(rs.getString("PAOName"));
            staticLoadGroup.setApplianceCategoryID(rs.getInt("ApplianceCategoryID"));
            staticLoadGroup.setConsumptionTypeID(rs.getInt("ConsumptionTypeID"));
            staticLoadGroup.setLoadGroupID(rs.getInt("LoadGroupID"));
            staticLoadGroup.setSwitchTypeID(rs.getInt("SwitchTypeID"));
            staticLoadGroup.setZipCode(rs.getString("ZipCode"));
            
            return staticLoadGroup;
        }
    }
    
}