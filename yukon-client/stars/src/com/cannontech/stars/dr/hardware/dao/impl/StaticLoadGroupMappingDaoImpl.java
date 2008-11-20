package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public class StaticLoadGroupMappingDaoImpl implements StaticLoadGroupMappingDao {

    private static final String selectLoadGroupSql;
    private static final ParameterizedRowMapper<StarsStaticLoadGroupMapping> loadGroupRowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    static {
        selectLoadGroupSql = "SELECT distinct LoadGroupID, ApplianceCategoryID, ZipCode, ConsumptionTypeID, " + "SwitchTypeID FROM StaticLoadGroupMapping " + " WHERE ApplianceCategoryID=? AND ZipCode LIKE '?%' AND ConsumptionTypeID=? " + " AND SwitchTypeID = ?";

        loadGroupRowMapper = StaticLoadGroupMappingDaoImpl.createRowMapper();
    }

    public StarsStaticLoadGroupMapping getStaticLoadGroupMapping(
            StarsStaticLoadGroupMapping criteria) {
        StarsStaticLoadGroupMapping loadGroup = null;

        // StaticLoadGroupMapping params
        Object[] loadGroupParams = new Object[] {
                criteria.getApplianceCategoryID(), criteria.getZipCode(),
                criteria.getConsumptionTypeID(), criteria.getSwitchTypeID() };

        List<StarsStaticLoadGroupMapping> loadGroups = simpleJdbcTemplate.query(selectLoadGroupSql,
                                                                                loadGroupRowMapper,
                                                                                loadGroupParams);
        if (loadGroups.size() > 0) {
            loadGroup = loadGroups.get(0);
        }

        return loadGroup;
    }

    private static final ParameterizedRowMapper<StarsStaticLoadGroupMapping> createRowMapper() {
        final ParameterizedRowMapper<StarsStaticLoadGroupMapping> rowMapper = new ParameterizedRowMapper<StarsStaticLoadGroupMapping>() {
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
        };
        return rowMapper;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
