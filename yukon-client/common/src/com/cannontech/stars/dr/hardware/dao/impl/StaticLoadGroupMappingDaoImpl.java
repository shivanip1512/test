package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public class StaticLoadGroupMappingDaoImpl implements StaticLoadGroupMappingDao {

    private static final String selectLoadGroupSql;
    private static final StarsStaticLoadGroupRowMapper loadGroupRowMapper = new StarsStaticLoadGroupRowMapper();
    private YukonJdbcTemplate yukonJdbcTemplate;

    static {
        selectLoadGroupSql = "SELECT distinct LoadGroupID, ApplianceCategoryID, ZipCode, ConsumptionTypeID, " 
            + " SwitchTypeID FROM StaticLoadGroupMapping " 
            + " WHERE ApplianceCategoryID=? AND ZipCode LIKE ? AND ConsumptionTypeID=? " 
            + " AND SwitchTypeID = ?";
    }

    @Override
    public StarsStaticLoadGroupMapping getStaticLoadGroupMapping(
            StarsStaticLoadGroupMapping criteria) {
        StarsStaticLoadGroupMapping loadGroup = null;

        // StaticLoadGroupMapping params
        Object[] loadGroupParams = new Object[] {
                criteria.getApplianceCategoryID(), criteria.getZipCode() + "%",
                criteria.getConsumptionTypeID(), criteria.getSwitchTypeID() };

        List<StarsStaticLoadGroupMapping> loadGroups = yukonJdbcTemplate.query(selectLoadGroupSql,
                                                                                loadGroupRowMapper,
                                                                                loadGroupParams);
        if (loadGroups.size() > 0) {
            loadGroup = loadGroups.get(0);
        }

        return loadGroup;
    }

    @Override
    public List<Integer> getLoadGroupIdsForApplianceCategory(
            int applianceCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT loadGroupId FROM staticLoadGroupMapping");
        sql.append("WHERE applianceCategoryId").eq(applianceCategoryId);
        return yukonJdbcTemplate.query(sql, new IntegerRowMapper());
    }

    public static class StarsStaticLoadGroupRowMapper implements
            ParameterizedRowMapper<StarsStaticLoadGroupMapping> {

        public StarsStaticLoadGroupMapping mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            StarsStaticLoadGroupMapping staticLoadGroup = new StarsStaticLoadGroupMapping();
            staticLoadGroup.setApplianceCategoryID(rs.getInt("ApplianceCategoryID"));
            staticLoadGroup.setConsumptionTypeID(rs.getInt("ConsumptionTypeID"));
            staticLoadGroup.setLoadGroupID(rs.getInt("LoadGroupID"));
            staticLoadGroup.setSwitchTypeID(rs.getInt("SwitchTypeID"));
            staticLoadGroup.setZipCode(rs.getString("ZipCode"));
            return staticLoadGroup;
        }
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
