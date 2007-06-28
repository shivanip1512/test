package com.cannontech.common.device.profilePeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.device.profilePeak.dao.ProfilePeakDao;
import com.cannontech.common.device.profilePeak.model.ProfilePeakResult;
import com.cannontech.common.device.profilePeak.model.ProfilePeakResultType;
import com.cannontech.database.incrementer.NextValueHelper;

public class ProfilePeakDaoImpl implements ProfilePeakDao {

    private SimpleJdbcTemplate jdbcTemplate = null;
    private NextValueHelper nextValueHelper = null;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public ProfilePeakResult getResult(int deviceId, ProfilePeakResultType type) {
        String sql = "SELECT * FROM profilepeakresult WHERE deviceId = ? AND resulttype = ?";

        try {
            ProfilePeakResult result = jdbcTemplate.queryForObject(sql,
                                                                   new ProfilePeakResultMapper(),
                                                                   deviceId,
                                                                   type.toString());

            return result;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }

    }

    public void saveResults(int deviceId, List<ProfilePeakResult> resultList) {

        // Delete any existing rows
        String sql = "DELETE FROM profilepeakresult WHERE deviceId = ?";
        jdbcTemplate.update(sql, deviceId);

        // Insert new rows
        sql = "INSERT INTO profilepeakresult (resultId, deviceId, resultFrom, resultTo, runDate, peakDay, usage, demand, averageDailyUsage, totalUsage, resultType, days) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        for (ProfilePeakResult result : resultList) {
            int nextValue = nextValueHelper.getNextValue("ProfilePeakResult");
            jdbcTemplate.update(sql,
                                nextValue,
                                result.getDeviceId(),
                                result.getStartDate(),
                                result.getStopDate(),
                                result.getRunDate(),
                                result.getPeakDate(),
                                result.getUsage(),
                                result.getDemand(),
                                result.getAverageDailyUsage(),
                                result.getTotalUsage(),
                                result.getResultType().toString(),
                                result.getDays());
        }
    }

    /**
     * Helper class which maps a result set row into a ProfilePeakResult
     */
    private class ProfilePeakResultMapper implements ParameterizedRowMapper<ProfilePeakResult> {

        public ProfilePeakResult mapRow(ResultSet rs, int rowNum) throws SQLException {

            ProfilePeakResult result = new ProfilePeakResult();
            result.setDeviceId(rs.getInt("deviceid"));
            result.setStartDate(rs.getString("resultFrom"));
            result.setStopDate(rs.getString("resultTo"));
            result.setRunDate(rs.getString("runDate"));
            result.setPeakDate(rs.getString("peakDay"));
            result.setUsage(rs.getString("usage"));
            result.setDemand(rs.getString("demand"));
            result.setAverageDailyUsage(rs.getString("averageDailyUsage"));
            result.setTotalUsage(rs.getString("totalUsage"));
            result.setResultType(ProfilePeakResultType.valueOf(rs.getString("resultType")));
            result.setDays(rs.getLong("days"));
            return result;
        }

    }

}
