package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceType;

public class ApplianceDaoImpl implements ApplianceDao {
    private static final String selectBaseSql;
    private final ParameterizedRowMapper<Appliance> rowMapper = createRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        
        selectBaseSql = "SELECT ab.ApplianceID,AccountID,ProgramID,InventoryID " +
        		        "FROM ApplianceBase ab,LMHardwareConfiguration lmhc " +
        		        "WHERE ab.ApplianceID = lmhc.ApplianceID";
        
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ApplianceType getApplianceType(final int applianceId) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT EntryText FROM YukonListEntry WHERE YukonListEntry.ListID = (");
        sb.append("SELECT ListID FROM YukonSelectionList WHERE ListName = ?) ");
        sb.append("AND EntryID = (SELECT CategoryID FROM ApplianceCategory WHERE ApplianceCategoryID = (");
        sb.append("SELECT ApplianceCategoryID FROM ApplianceBase WHERE ApplianceID = ?))");
        
        String sql = sb.toString();
        
        String textValue = simpleJdbcTemplate.queryForObject(sql,
                                                             String.class, 
                                                             YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
                                                             applianceId);
        
        ApplianceType type = getApplianceTypeEnumFromTextValue(textValue);
        return type;
    }
    
    private ApplianceType getApplianceTypeEnumFromTextValue(final String textValue) {
        String result = textValue;
        result = result.replaceAll("[(|)]", "");
        result = result.replaceAll("\\s+", "_");
        result = result.toUpperCase();
        return ApplianceType.valueOf(result);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Appliance getById(final int applianceId) {
        String sql = selectBaseSql + " AND ab.ApplianceID = ?";
        Appliance appliance = simpleJdbcTemplate.queryForObject(sql, rowMapper, applianceId);
        return appliance;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Appliance> getByAccountId(final int accountId) {
        String sql = selectBaseSql + " AND ab.AccountID = ?";
        List<Appliance> list = simpleJdbcTemplate.query(sql, rowMapper, accountId);
        return list;
    }

    private ParameterizedRowMapper<Appliance> createRowMapper() {
        final ParameterizedRowMapper<Appliance> mapper = new ParameterizedRowMapper<Appliance>() {
            @Override
            public Appliance mapRow(ResultSet rs, int rowNum) throws SQLException {
                int applianceId = rs.getInt("ApplianceID");

                final Appliance appliance = new Appliance();
                appliance.setAccountId(rs.getInt("AccountID"));
                appliance.setApplianceId(applianceId);
                appliance.setApplianceType(getApplianceType(applianceId));
                appliance.setInventoryId(rs.getInt("InventoryID"));
                appliance.setProgramId(rs.getInt("ProgramID"));
                return appliance;
            }
        };
        return mapper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
