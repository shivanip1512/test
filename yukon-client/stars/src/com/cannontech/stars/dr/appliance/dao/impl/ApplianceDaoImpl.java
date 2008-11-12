package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;

public class ApplianceDaoImpl implements ApplianceDao {
    private final ParameterizedRowMapper<Appliance> rowMapper = createRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ApplianceCategoryDao applianceCategoryDao;
    private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    
    private String applianceSQLHeader = 
        "SELECT AB.applianceId, AB.applianceCategoryId, AB.accountId, " +
    	"       AB.programId, LMHC.inventoryId, LMHC.addressingGroupId, " +
    	"       LMHC.loadNumber, AB.kwCapacity " +
    	"FROM ApplianceBase AB, LMHardwareConfiguration LMHC ";
    
    private String applianceSQLFooter = 
        " AB.applianceId = LMHC.applianceId ";
    
    
    @Override
    public void updateApplianceKW(int applianceId, float applianceKW) {
        SqlStatementBuilder updateApplianceSQL = new SqlStatementBuilder();
        updateApplianceSQL.append("UPDATE ApplianceBase");
        updateApplianceSQL.append("SET kwCapacity = "+applianceKW+" ");
        updateApplianceSQL.append("WHERE applianceId = ?");

        simpleJdbcTemplate.update(updateApplianceSQL.toString(), 
                                  applianceId);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Appliance getById(final int applianceId) {
        SqlStatementBuilder applianceSQL = new SqlStatementBuilder();
        applianceSQL.append(applianceSQLHeader);
        applianceSQL.append("WHERE AB.applianceId = ? ");
        applianceSQL.append("AND "+applianceSQLFooter);

        Appliance appliance = simpleJdbcTemplate.queryForObject(applianceSQL.toString(), rowMapper, applianceId);
        return appliance;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Appliance> getByAccountId(final int accountId) {
        SqlStatementBuilder applianceSQL = new SqlStatementBuilder();
        applianceSQL.append(applianceSQLHeader);
        applianceSQL.append("WHERE AB.accountId = ? ");
        applianceSQL.append("AND "+applianceSQLFooter);

        List<Appliance> list = simpleJdbcTemplate.query(applianceSQL.toString(), rowMapper, accountId);
        return list;
    }
    
    public List<Appliance> getByAccountIdAndProgramIdAndInventoryId(int accountId, 
                                                                    int programId,
                                                                    int inventoryId) {

        SqlStatementBuilder applianceSQL = new SqlStatementBuilder();
        applianceSQL.append(applianceSQLHeader); 
        applianceSQL.append("Where AB.accountId = ? ");
        applianceSQL.append("AND AB.programId = ? ");
        applianceSQL.append("AND LMHC.inventoryId = ?");
        applianceSQL.append("AND "+applianceSQLFooter);
        
        try {
            return simpleJdbcTemplate.query(applianceSQL.toString(), rowMapper, 
                                            accountId, programId, inventoryId);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }
    
    @Override
    @Transactional
    public void deleteAppliancesForAccount(int accountId) {
        List<Appliance> appliances = getByAccountId(accountId);
        for(Appliance appliance : appliances) {
            deleteAppliance(appliance);    
        }
    }
    
    @Override
    @Transactional
    public void deleteAppliance(Appliance appliance) {
        deleteAppliance(appliance.getApplianceId());
    }
    
    @Override
    @Transactional
    public void deleteAppliance(int applianceId) {
        lmHardwareConfigurationDao.deleteForAppliance(applianceId);
        for(String table : ApplianceBase.DEPENDENT_TABLES) {
            String delete = "DELETE FROM " + table + " WHERE ApplianceId = ?";
            simpleJdbcTemplate.update(delete, applianceId);
        }
        String delete = "DELETE FROM ApplianceBase WHERE ApplianceId = ?";
        simpleJdbcTemplate.update(delete, applianceId);
    }
    
    public void deleteAppliancesByAccountId(int accountId) {
        String cond = "FROM ApplianceBase " +
        		      "WHERE accountId = " + accountId;
        SqlStatement stmt = new SqlStatement( "", CtiUtilities.getDatabaseAlias() );

        try {
            for (int i = 0; i < ApplianceBase.DEPENDENT_TABLES.length; i++) {
                String sql = "DELETE FROM " + ApplianceBase.DEPENDENT_TABLES[i] + " WHERE ApplianceID IN (SELECT ApplianceID " + cond + ")";
                stmt.setSQLString( sql );
                    stmt.execute();
            }
            
            String sql = "DELETE FROM LMHardwareConfiguration WHERE ApplianceID IN (SELECT ApplianceID " + cond + ")";
            stmt.setSQLString( sql );
            stmt.execute();
            
            stmt.setSQLString( "DELETE " + cond );
            stmt.execute();
        } catch (CommandExecutionException e) {
            CTILogger.error( e.getMessage(), e );
        }

    }
    
    @SuppressWarnings("unchecked")
    public void deleteAppliancesByAccountIdAndInventoryId(int accountId, int inventoryId) {
        
        String applianceIdsSQL = "Select AB.applianceId " + 
                                 "From ApplianceBase AB, LMHardwareConfiguration LMHC " + 
                                 "Where LMHC.inventoryId = " + inventoryId + " "+
                                 "AND AB.accountId = " + accountId + " "+ 
                                 "AND AB.applianceId = LMHC.applianceId";
        
        SqlStatement stmt = new SqlStatement( "", CtiUtilities.getDatabaseAlias() );
        
        try {
            List<Integer> applianceIds = simpleJdbcTemplate.getJdbcOperations().queryForList(applianceIdsSQL, Integer.class);
                
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
    
    @Autowired
    public void setLMHardwareConfigurationDao(LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }
}
