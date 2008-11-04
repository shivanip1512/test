package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.cannontech.spring.YukonSpringHook;
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
        SimpleJdbcTemplate simpleJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", SimpleJdbcTemplate.class);
        
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
}
