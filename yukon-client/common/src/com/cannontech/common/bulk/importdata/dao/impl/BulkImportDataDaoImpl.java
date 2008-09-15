package com.cannontech.common.bulk.importdata.dao.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.bulk.importdata.dao.BulkImportDataDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.db.importer.FailType;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.user.YukonUserContext;

public class BulkImportDataDaoImpl implements BulkImportDataDao {

    private SimpleJdbcOperations jdbcTemplate;
    private DateFormattingService dateFormattingService;
    
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
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType != ?");
       
        jdbcTemplate.update(sql.toString(), FailType.FAIL_COMMUNICATION.getErrorString());
        
        return true;
    }
    
    public boolean deleteAllPending() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportPendingComm.TABLE_NAME);
        jdbcTemplate.update(sql.toString());
        
        return true;
    }
    
    public boolean deleteAllCommunicationFailures()  {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType = ?");
        jdbcTemplate.update(sql.toString(), FailType.FAIL_COMMUNICATION.getErrorString());

        return true;
    }
    
    public Date getLastImportTime(YukonUserContext userContext) throws ParseException{
        
        String lastImportTime = jdbcTemplate.queryForObject("SELECT LASTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'", 
                                                            String.class);
        return dateFormattingService.flexibleDateParser(lastImportTime, userContext);
    }
    
    public Date getNextImportTime(YukonUserContext userContext) throws ParseException
    {
        String nextImportTime = jdbcTemplate.queryForObject("SELECT NEXTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'", 
                                                               String.class);
        return dateFormattingService.flexibleDateParser(nextImportTime, userContext);
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}
