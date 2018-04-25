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
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.ecobee.dao.EcobeeReconciliationReportDao;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeExtraneousDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeExtraneousSetDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedSetDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMissingDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMissingSetDiscrepancy;

public class EcobeeReconciliationReportDaoImpl implements EcobeeReconciliationReportDao {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeReconciliationReportDaoImpl.class);
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final YukonRowMapper<EcobeeDiscrepancy> ecobeeDiscrepancyRowMapper = new YukonRowMapper<EcobeeDiscrepancy>() {
        @Override
        public EcobeeDiscrepancy mapRow(YukonResultSet rs) throws SQLException {
            EcobeeDiscrepancyType errorType = rs.getEnum("ErrorType", EcobeeDiscrepancyType.class);
            int errorId = rs.getInt("EcobeeReconReportErrorId");
            String serialNumber = rs.getString("SerialNumber");
            String currentLocation = rs.getString("CurrentLocation");
            String correctLocation = rs.getString("CorrectLocation");
            
            switch(errorType) {
                case MISSING_MANAGEMENT_SET:
                    return new EcobeeMissingSetDiscrepancy(errorId, correctLocation);
                case EXTRANEOUS_MANAGEMENT_SET:
                    return new EcobeeExtraneousSetDiscrepancy(errorId, currentLocation);
                case MISLOCATED_MANAGEMENT_SET:
                    return new EcobeeMislocatedSetDiscrepancy(errorId, currentLocation, correctLocation);
                case MISSING_DEVICE:
                    return new EcobeeMissingDeviceDiscrepancy(errorId, serialNumber, correctLocation);
                case EXTRANEOUS_DEVICE:
                    return new EcobeeExtraneousDeviceDiscrepancy(errorId, serialNumber, currentLocation);
                case MISLOCATED_DEVICE:
                    return new EcobeeMislocatedDeviceDiscrepancy(errorId, serialNumber, currentLocation, correctLocation);
                default:
                    throw new IllegalArgumentException("Unsupported EcobeeDiscrepancyType " + errorType);
            }
        }
    };
    
    @Override
    @Transactional
    public int insertReport(EcobeeReconciliationReport report) {
        log.debug("Inserting new reconciliation report.");
        
        int reportId = nextValueHelper.getNextValue("EcobeeReconciliationReport");
        
        //clear out the previous report, we're inserting a new one
        deleteReport();
        
        //insert the new report
        insertReportRow(reportId);
        
        for (EcobeeDiscrepancy error : report.getErrors()) {
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
    public EcobeeReconciliationReport findReport() {
        Integer reportId = findCurrentReportId();
        if(reportId == null) {
            return null;
        }
        
        Instant reportDate = getReportDate(reportId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select EcobeeReconReportErrorId, ErrorType, SerialNumber, CurrentLocation, CorrectLocation");
        sql.append("from EcobeeReconReportError");
        sql.append("where EcobeeReconReportId").eq_k(reportId);
        
        List<EcobeeDiscrepancy> errorList = jdbcTemplate.query(sql, ecobeeDiscrepancyRowMapper);
        
        return new EcobeeReconciliationReport(reportId, reportDate, errorList);
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
