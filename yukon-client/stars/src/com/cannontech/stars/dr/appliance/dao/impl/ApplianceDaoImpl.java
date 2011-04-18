package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;

public class ApplianceDaoImpl implements ApplianceDao {
    private final ParameterizedRowMapper<Appliance> rowMapper = createRowMapper();
    private YukonJdbcTemplate yukonJdbcTemplate;
    private ApplianceCategoryDao applianceCategoryDao;
    private LMHardwareConfigurationDao lmHardwareConfigurationDao;

    
    private String applianceSQLHeader = 
        "SELECT AB.applianceId, AB.applianceCategoryId, AB.accountId, "+
        "       AB.programId, LMHC.inventoryId, LMHC.addressingGroupId, "+
        "       LMHC.loadNumber, AB.kwCapacity, AB.ManufacturerId, "+
        "       AB.YearManufactured, AB.LocationId, AB.EfficiencyRating, "+
        "       AB.Notes, AB.ModelNumber " +
        "FROM ApplianceBase AB "+
        "LEFT JOIN LMHardwareConfiguration LMHC ON AB.applianceId = LMHC.applianceId ";
    
    @Override
    public void updateApplianceKW(int applianceId, float applianceKW) {
        SqlStatementBuilder updateApplianceSQL = new SqlStatementBuilder();
        updateApplianceSQL.append("UPDATE ApplianceBase");
        updateApplianceSQL.append("SET kwCapacity = ? ");
        updateApplianceSQL.append("WHERE applianceId = ?");

        yukonJdbcTemplate.update(updateApplianceSQL.toString(),
                                  Float.toString(applianceKW),
                                  applianceId);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Appliance getById(final int applianceId) {
        SqlStatementBuilder applianceSQL = new SqlStatementBuilder();
        applianceSQL.append(applianceSQLHeader);
        applianceSQL.append("WHERE AB.applianceId").eq(applianceId);

        try {
            return yukonJdbcTemplate.queryForObject(applianceSQL, rowMapper);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("The appliance id supplied does not exist.");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Appliance> getAssignedAppliancesByAccountId(final int accountId) {
        SqlStatementBuilder applianceSQL = new SqlStatementBuilder();
        applianceSQL.append("SELECT AB.applianceId, AB.applianceCategoryId, AB.accountId, ");
        applianceSQL.append("       AB.programId, LMHC.inventoryId, LMHC.addressingGroupId, ");
        applianceSQL.append("       LMHC.loadNumber, AB.kwCapacity, AB.ManufacturerId, ");
        applianceSQL.append("       AB.YearManufactured, AB.LocationId, AB.EfficiencyRating, ");
        applianceSQL.append("       AB.Notes, AB.ModelNumber ");
        applianceSQL.append("FROM ApplianceBase AB ");
        applianceSQL.append("JOIN LMHardwareConfiguration LMHC ON AB.applianceId = LMHC.applianceId ");
        applianceSQL.append("WHERE AB.accountId ").eq(accountId);

        List<Appliance> list = yukonJdbcTemplate.query(applianceSQL, rowMapper);
        return list;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Appliance> getByAccountId(final int accountId) {
        SqlStatementBuilder applianceSQL = new SqlStatementBuilder();
        applianceSQL.append(applianceSQLHeader);
        applianceSQL.append("WHERE AB.accountId").eq(accountId);

        List<Appliance> list = yukonJdbcTemplate.query(applianceSQL, rowMapper);
        return list;
    }

    public Appliance getByAccountIdAndProgramIdAndInventoryId(int accountId, int assignedProgramId, int inventoryId) {

        SqlStatementBuilder applianceSQL = new SqlStatementBuilder();
        applianceSQL.append(applianceSQLHeader); 
        applianceSQL.append("Where AB.accountId").eq(accountId);
        applianceSQL.append("AND AB.programId").eq(assignedProgramId);
        applianceSQL.append("AND LMHC.inventoryId").eq(inventoryId);
        
        Appliance appliance = yukonJdbcTemplate.queryForObject(applianceSQL, rowMapper);
        return appliance;
    }
    
    @Override
    public List<Integer> getApplianceIdsForAccountId(int accountId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ApplianceId");
        sql.append("FROM ApplianceBase");
        sql.append("WHERE AccountId").eq(accountId);
        List<Integer> applianceIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return applianceIds;
    }
    
    @Override
    @Transactional
    public void deleteAppliancesByAccountId(int accountId) {
        List<Integer> applianceIds = getApplianceIdsForAccountId(accountId);
        if(!applianceIds.isEmpty()) {
            for(String table : ApplianceBase.DEPENDENT_TABLES) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("DELETE FROM " + table + " WHERE ApplianceId IN (", applianceIds, ")");
                yukonJdbcTemplate.update(sql.toString());
            }
            lmHardwareConfigurationDao.deleteForAppliances(applianceIds);
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM ApplianceBase WHERE ApplianceId IN (", applianceIds, ")");
            yukonJdbcTemplate.update(sql.toString());
        }
    }
    
    public void deleteAppliancesByAccountIdAndInventoryId(int accountId, int inventoryId) {
        
        String applianceIdsSQL = "Select AB.applianceId " + 
                                 "From ApplianceBase AB, LMHardwareConfiguration LMHC " + 
                                 "Where LMHC.inventoryId = " + inventoryId + " "+
                                 "AND AB.accountId = " + accountId + " "+ 
                                 "AND AB.applianceId = LMHC.applianceId";
        
        SqlStatement stmt = new SqlStatement( "", CtiUtilities.getDatabaseAlias() );
        
        try {
            List<Integer> applianceIds = yukonJdbcTemplate.getJdbcOperations().queryForList(applianceIdsSQL, Integer.class);
                
            for (int i = 0; i < ApplianceBase.DEPENDENT_TABLES.length; i++) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("DELETE FROM " + ApplianceBase.DEPENDENT_TABLES[i] + " WHERE applianceId IN (",applianceIds,")");
                stmt.setSQLString( sql.toString() );
                stmt.execute();
            }
            SqlStatementBuilder lmHardwareConfigurationSQL = new SqlStatementBuilder();
            lmHardwareConfigurationSQL.append("DELETE FROM LMHardwareConfiguration WHERE applianceId IN (",applianceIds,")");
            stmt.setSQLString( lmHardwareConfigurationSQL.toString() );
            stmt.execute();
            
            SqlStatementBuilder applianceBaseSQL = new SqlStatementBuilder();
            applianceBaseSQL.append("DELETE FROM ApplianceBase " +
            		                "WHERE applianceId IN (",applianceIds,")" );
            stmt.setSQLString(applianceBaseSQL.toString());
            stmt.execute();
        }
        catch (CommandExecutionException e) {
            CTILogger.error( e.getMessage(), e );
        }
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
                
                appliance.setManufacturerId(rs.getInt("ManufacturerId"));
                appliance.setYearManufactured(rs.getInt("YearManufactured"));
                appliance.setLocationId(rs.getInt("LocationId"));
                appliance.setEfficiencyRating(rs.getFloat("EfficiencyRating"));
                appliance.setNotes(rs.getString("Notes"));
                appliance.setModelNumber(rs.getString("ModelNumber"));
                
                return appliance;
            }
        };
        return mapper;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setApplianceCategoryDao(
            ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
    
    @Autowired
    public void setLMHardwareConfigurationDao(LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }

}
