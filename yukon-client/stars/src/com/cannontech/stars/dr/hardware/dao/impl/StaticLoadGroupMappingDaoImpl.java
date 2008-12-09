package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public class StaticLoadGroupMappingDaoImpl implements StaticLoadGroupMappingDao {

    private static final String selectLoadGroupSql;
    private static final StarsStaticLoadGroupRowMapper loadGroupRowMapper = new StarsStaticLoadGroupRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;

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

        List<StarsStaticLoadGroupMapping> loadGroups = simpleJdbcTemplate.query(selectLoadGroupSql,
                                                                                loadGroupRowMapper,
                                                                                loadGroupParams);
        if (loadGroups.size() > 0) {
            loadGroup = loadGroups.get(0);
        }

        return loadGroup;
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
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
