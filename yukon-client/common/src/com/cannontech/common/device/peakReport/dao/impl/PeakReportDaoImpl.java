package com.cannontech.common.device.peakReport.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.device.peakReport.dao.PeakReportDao;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class PeakReportDaoImpl implements PeakReportDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    
    @Override
    public PeakReportResult getResult(int deviceId, PeakReportRunType runType) {
        
        String sql = "SELECT * FROM PeakReport WHERE deviceId = ? AND runType = ?";

        try {
            
            // get result and do general mapping
            PeakReportResult peakResult = jdbcTemplate.queryForObject(sql,
                                                                   new PeakReportResultMapper(),
                                                                   deviceId,
                                                                   runType.toString());
            
            return peakResult;
            
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }

    }

    @Override
    public void saveResult(PeakReportResult peakResult) {

        // Delete any existing rows
        this.deleteReport(peakResult.getDeviceId(), peakResult.getRunType());

        // Insert new rows
        String sql = "INSERT INTO PeakReport (resultId, deviceId, channel, peakType, runType, runDate, resultString) VALUES (?,?,?,?,?,?,?)";
            
        int nextValue = nextValueHelper.getNextValue("PeakReport");
        
        jdbcTemplate.update(sql,
                            nextValue,
                            peakResult.getDeviceId(),
                            peakResult.getChannel(),
                            peakResult.getPeakType().toString().toLowerCase(),
                            peakResult.getRunType().toString().toUpperCase(),
                            peakResult.getRunDate(),
                            peakResult.getResultString());
    }

    @Override
    public void deleteReport(int deviceId, PeakReportRunType runType) {
        
        // Delete any existing rows
        String sql = "DELETE FROM PeakReport WHERE deviceId = ? AND runType = ?";
        jdbcTemplate.update(sql, deviceId, runType.toString().toUpperCase());
    }
    
    /**
     * Helper class which maps a result set row into a PeakReportResult
     */
    private class PeakReportResultMapper implements RowMapper<PeakReportResult> {

        @Override
        public PeakReportResult mapRow(ResultSet rs, int rowNum) throws SQLException {

            PeakReportResult peakResult = new PeakReportResult();
            
            peakResult.setDeviceId(rs.getInt("deviceId"));
            peakResult.setChannel(rs.getInt("channel"));
            peakResult.setPeakType(rs.getString("peakType"));
            peakResult.setRunType(rs.getString("runType"));
            peakResult.setRunDate(rs.getTimestamp("runDate"));
            peakResult.setResultString(rs.getString("resultString"));
            
            peakResult.setNoData(false);
            peakResult.setDeviceError("");
            
            return peakResult;
        }
    }
}
