package com.cannontech.common.bulk.importdata.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.bulk.importdata.dao.BulkImportDataDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.db.importer.FailType;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;

public class BulkImportDataDaoImpl implements BulkImportDataDao {

    private SimpleJdbcOperations jdbcTemplate;
    
    
    // GETS
    public List<ImportFail> getAllDataFailures() {
              
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select Address, Name, RouteName, MeterNumber, CollectionGrp,");
        sql.append("AltGrp, TemplateName, ErrorMsg, DateTime, BillGrp, SubstationName, FailType");
        sql.append("FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType != ?");
        sql.append("ORDER BY DateTime DESC");
        
        ImportFailRowMapper mapper = new ImportFailRowMapper();
        List<ImportFail> failures = jdbcTemplate.query(sql.toString(), mapper, FailType.FAIL_COMMUNICATION.getErrorString());
        return failures;
    }
    
    
    public List<ImportPendingComm> getAllPending() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceId, Address, Name, RouteName, MeterNumber, CollectionGrp,");
        sql.append("AltGrp, TemplateName, BillGrp, SubstationName");
        sql.append("FROM " + ImportPendingComm.TABLE_NAME);
        
        ImportPendingCommRowMapper mapper = new ImportPendingCommRowMapper();
        List<ImportPendingComm> pending = jdbcTemplate.query(sql.toString(), mapper);
        return pending;
    }
    
    
    public List<ImportFail> getAllCommunicationFailures() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select Address, Name, RouteName, MeterNumber, CollectionGrp,");
        sql.append("AltGrp, TemplateName, ErrorMsg, DateTime, BillGrp, SubstationName, FailType");
        sql.append("FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType = ?");
        sql.append("ORDER BY DateTime DESC");
        
        ImportFailRowMapper mapper = new ImportFailRowMapper();
        List<ImportFail> failures = jdbcTemplate.query(sql.toString(), mapper, FailType.FAIL_COMMUNICATION.getErrorString());
        return failures;
    }
    
    
    // DELETES
    public boolean deleteAllDataFailures() {
        
        JdbcOperations jdbcOps = jdbcTemplate.getJdbcOperations();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType != '" + FailType.FAIL_COMMUNICATION.getErrorString() + "'");
       
        try {
            jdbcOps.execute(sql.toString());
        }
        catch (DataAccessException e) {
            throw e;
        }
        return true;
    }
    
    public boolean deleteAllPending() {
        
        JdbcOperations jdbcOps = jdbcTemplate.getJdbcOperations();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportPendingComm.TABLE_NAME);
       
        try {
            jdbcOps.execute(sql.toString());
        }
        catch (DataAccessException e) {
            throw e;
        }
        return true;
    }
    
    public boolean deleteAllCommunicationFailures() {
        
        JdbcOperations jdbcOps = jdbcTemplate.getJdbcOperations();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType = '" + FailType.FAIL_COMMUNICATION.getErrorString() + "'");
       
        try {
            jdbcOps.execute(sql.toString());
        }
        catch (DataAccessException e) {
            throw e;
        }
        return true;
    }
    
    public String getLastImportTime() throws Exception
    {
        String lastImportTime = new String("------------");
        
        com.cannontech.database.SqlStatement stmt =
            new com.cannontech.database.SqlStatement("SELECT LASTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'",
                                                    "yukon" );

        stmt.execute();
        if( stmt.getRowCount() > 0 )
        {
            lastImportTime = stmt.getRow(0)[0].toString();
        }
        return lastImportTime;
    }
    
    public String getNextImportTime() throws Exception
    {
        String nextImportTime = new String("------------");
        
        com.cannontech.database.SqlStatement stmt =
            new com.cannontech.database.SqlStatement("SELECT NEXTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'",
                                                    "yukon" );

        stmt.execute();
        if( stmt.getRowCount() > 0 )
        {
            nextImportTime = stmt.getRow(0)[0].toString();
        }
        return nextImportTime;
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
}
