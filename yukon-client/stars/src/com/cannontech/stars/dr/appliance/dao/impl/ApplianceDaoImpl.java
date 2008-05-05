package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;

public class ApplianceDaoImpl implements ApplianceDao {
    private static final String selectBaseSql;
    private final ParameterizedRowMapper<Appliance> rowMapper = createRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ApplianceCategoryDao applianceCategoryDao;
    
    static {
        
        selectBaseSql = "SELECT ab.ApplianceID,ab.ApplianceCategoryID,AccountID,ProgramID,InventoryID,AddressingGroupID,LoadNumber " +
                        "FROM ApplianceBase ab,LMHardwareConfiguration lmhc " +
                        "WHERE ab.ApplianceID = lmhc.ApplianceID";
        
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
                final Appliance appliance = new Appliance();
                appliance.setAccountId(rs.getInt("AccountID"));
                appliance.setApplianceId(rs.getInt("ApplianceID"));
                
                int applianceCategoryId = rs.getInt("ApplianceCategoryID");
                ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
                appliance.setApplianceCategory(applianceCategory);
                
                appliance.setInventoryId(rs.getInt("InventoryID"));
                appliance.setProgramId(rs.getInt("ProgramID"));
                appliance.setGroupdId(rs.getInt("AddressingGroupID"));
                appliance.setRelay(rs.getInt("LoadNumber"));
                return appliance;
            }
        };
        return mapper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setApplianceCategoryDao(
            ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
}
