package com.cannontech.dr.ecobee.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.ecobee.dao.EcobeeZeusReconciliationReportDao;
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusExtraneousDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusExtraneousGroupDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusMislocatedDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusMissingDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusMissingGroupDiscrepancy;

public class EcobeeZeusReconciliationReportDaoImpl implements EcobeeZeusReconciliationReportDao {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusReconciliationReportDaoImpl.class);
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final YukonRowMapper<EcobeeZeusDiscrepancy> ecobeeDiscrepancyRowMapper = new YukonRowMapper<EcobeeZeusDiscrepancy>() {
        @Override
        public EcobeeZeusDiscrepancy mapRow(YukonResultSet rs) throws SQLException {
            EcobeeZeusDiscrepancyType errorType = rs.getEnum("ErrorType", EcobeeZeusDiscrepancyType.class);
            int errorId = rs.getInt("EcobeeReconReportErrorId");
            String serialNumber = rs.getString("SerialNumber");
            String currentLocation = rs.getString("CurrentLocation");
            String correctLocation = rs.getString("CorrectLocation");
            
            switch(errorType) {
                case MISSING_GROUP:
                    return new EcobeeZeusMissingGroupDiscrepancy(errorId, correctLocation);
                case EXTRANEOUS_GROUP:
                    return new EcobeeZeusExtraneousGroupDiscrepancy(errorId, currentLocation);
                case MISSING_DEVICE:
                    return new EcobeeZeusMissingDeviceDiscrepancy(errorId, serialNumber, correctLocation);
                case EXTRANEOUS_DEVICE:
                    return new EcobeeZeusExtraneousDeviceDiscrepancy(errorId, serialNumber, currentLocation);
                case MISLOCATED_DEVICE:
                    return new EcobeeZeusMislocatedDeviceDiscrepancy(errorId, serialNumber, currentLocation, correctLocation);
                default:
                    throw new IllegalArgumentException("Unsupported EcobeeDiscrepancyType " + errorType);
            }
        }
    };
    
    @Override
    @Transactional
    public int insertReport(EcobeeZeusReconciliationReport report) {
        log.debug("Inserting new reconciliation report.");
        
        int reportId = nextValueHelper.getNextValue("EcobeeReconciliationReport");
        
        //clear out the previous report, we're inserting a new one
        deleteReport();
        
        //insert the new report
        insertReportRow(reportId);
        
        for (EcobeeZeusDiscrepancy error : report.getErrors()) {
            int errorId = nextValueHelper.getNextValue("EcobeeReconReportError");
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink parameters = sql.insertInto("EcobeeReconReportError");
            parameters.addValue("EcobeeReconReportErrorId", errorId);
            parameters.addValue("EcobeeReconReportId", reportId);
            parameters.addValue("ErrorType", error.getErrorType());
            parameters.addValue("SerialNumber", error.getSerialNumber());
            parameters.addValue("CurrentLocation", error.getCurrentPath());
            parameters.addValue("CorrectLocation", error.getCorrectPath());
            
            jdbcTemplate.update(sql);
        }
        
        return reportId;
    }
    
    @Override
    @Transactional
    public EcobeeZeusReconciliationReport findReport() {
        Integer reportId = findCurrentReportId();
        if(reportId == null) {
            return null;
        }
        
        Instant reportDate = getReportDate(reportId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select EcobeeReconReportErrorId, ErrorType, SerialNumber, CurrentLocation, CorrectLocation");
        sql.append("from EcobeeReconReportError");
        sql.append("where EcobeeReconReportId").eq_k(reportId);
        
        List<EcobeeZeusDiscrepancy> errorList = jdbcTemplate.query(sql, ecobeeDiscrepancyRowMapper);
        
        return new EcobeeZeusReconciliationReport(reportId, reportDate, errorList);
    }
    
    @Override
    public boolean removeError(int reportId, int errorId) {
        log.debug("Removing discrepancy from ecobee reconciliation report. ReportId: " + reportId + " ErrorId: " + errorId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from EcobeeReconReportError");
        sql.append("where EcobeeReconReportErrorId").eq(errorId);
        sql.append("and EcobeeReconReportId").eq(reportId);
        
        int rowsAffected = jdbcTemplate.update(sql);
        return rowsAffected == 1;
    }
    
    @Override
    public void cleanUpReconciliationTables() {
        deleteReport();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from EcobeeReconReportError");

        jdbcTemplate.update(sql);
    }
    
    private Integer findCurrentReportId() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select EcobeeReconReportId");
        sql.append("from EcobeeReconciliationReport");
        
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    private Instant getReportDate(int reportId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ReportDate");
        sql.append("from EcobeeReconciliationReport");
        sql.append("where EcobeeReconReportId").eq_k(reportId);
        
        return jdbcTemplate.queryForObject(sql, TypeRowMapper.INSTANT);
    }
    
    /**
     * Deletes everything from EcobeeReconciliationReport (and EcobeeReconReportError via FK).
     */
    private int deleteReport() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from EcobeeReconciliationReport");
        
        return jdbcTemplate.update(sql);
    }
    

    /**
     * Adds a row with the current date and the specified id to EcobeeReconciliationReport.
     */
    private void insertReportRow(int reportId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink parameters = sql.insertInto("EcobeeReconciliationReport");
        parameters.addValue("EcobeeReconReportId", reportId);
        parameters.addValue("ReportDate", Instant.now());
        
        jdbcTemplate.update(sql);
    }
}
