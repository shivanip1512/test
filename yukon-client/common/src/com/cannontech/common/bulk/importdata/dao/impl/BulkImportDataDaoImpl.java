package com.cannontech.common.bulk.importdata.dao.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.importdata.dao.BulkImportDataDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.importer.FailType;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.user.YukonUserContext;

public class BulkImportDataDaoImpl implements BulkImportDataDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired  private DateFormattingService dateFormattingService;
    
    @Override
    public int getImportDataCount() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM " + ImportData.TABLE_NAME);

        int count = jdbcTemplate.queryForInt(sql);
        return count;
    }
    
    // GETS
    @Override
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
    
    
    @Override
    public List<ImportPendingComm> getAllPending() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceId, Address, Name, RouteName, MeterNumber, CollectionGrp,");
        sql.append("AltGrp, TemplateName, BillGrp, SubstationName");
        sql.append("FROM " + ImportPendingComm.TABLE_NAME);
        
        ImportPendingCommRowMapper mapper = new ImportPendingCommRowMapper();
        List<ImportPendingComm> pending = jdbcTemplate.query(sql.toString(), mapper);
        return pending;
    }
    
    
    @Override
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
    @Override
    public boolean deleteAllDataFailures() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType != ?");
       
        jdbcTemplate.update(sql.toString(), FailType.FAIL_COMMUNICATION.getErrorString());
        
        return true;
    }
    
    @Override
    public boolean deleteAllPending() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportPendingComm.TABLE_NAME);
        jdbcTemplate.update(sql.toString());
        
        return true;
    }
    
    @Override
    public boolean deleteAllCommunicationFailures()  {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + ImportFail.TABLE_NAME);
        sql.append("WHERE FailType = ?");
        jdbcTemplate.update(sql.toString(), FailType.FAIL_COMMUNICATION.getErrorString());

        return true;
    }
    
    @Override
    public String getLastImportTime() {
        
        String lastImportTime = jdbcTemplate.queryForObject("SELECT LASTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'", 
                                                            String.class);
        return lastImportTime;
    }
    
    @Override
    public String getNextImportTime() {
        String nextImportTime = jdbcTemplate.queryForObject("SELECT NEXTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'", 
                                                               String.class);
        return nextImportTime;
    }

    @Override
    public String getFormattedLastImportTime(YukonUserContext userContext, DateFormatEnum dateFormatEnum) {
        String lastImportTime = getLastImportTime();
        return getFormattedImportTime(lastImportTime, userContext, dateFormatEnum);
    }

    @Override
    public String getFormattedNextImportTime(YukonUserContext userContext, DateFormatEnum dateFormatEnum) {
        String nextImportTime = getNextImportTime();
        return getFormattedImportTime(nextImportTime, userContext, dateFormatEnum);
    }

    private String getFormattedImportTime(String importTimeStr, YukonUserContext userContext, DateFormatEnum dateFormatEnum) {
        try {
            Date importTime = CtiUtilities.parseJavaDateString(importTimeStr);
            return dateFormattingService.format(importTime,
                                                    dateFormatEnum,
                                                    userContext);
        } catch (ParseException okayToIgnore) {
            //We can ignore this exception because we are actually storing the importTimes
            // as a string in the database.  Other possibilities are "-----", "Currently Running...", for example.
        }
        return importTimeStr;
    }
}
