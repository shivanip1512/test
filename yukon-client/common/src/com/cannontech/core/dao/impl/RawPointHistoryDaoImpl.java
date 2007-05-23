package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;

/**
 * Implementation of RawPointHistoryDao
 */
public class RawPointHistoryDaoImpl implements RawPointHistoryDao {

    private SimpleJdbcTemplate jdbcTemplate = null;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {

        String sql = "SELECT DISTINCT                                                   " + "       pointid,                                                        " + "       timestamp,                                                      " + "       value                                                           " + "   FROM                                                                " + "       rawpointhistory                                                 " + "   WHERE                                                               " + "       pointid IN (?)                                                  " + "       AND (timestamp > ? AND timestamp <= ? )                         " + "   ORDER BY                                                            " + "       pointid,                                                        " + "       timestamp";

        return jdbcTemplate.query(sql, new LiteRPHRowMapper(), pointId, startDate, stopDate);

    }

    /**
     * Helper class which maps a result set row into a PointValueHolder
     */
    private class LiteRPHRowMapper implements ParameterizedRowMapper<PointValueHolder> {

        public PointValueHolder mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Timestamp timestamp = rs.getTimestamp("timestamp");
            final double value = rs.getDouble("value");

            return new PointValueHolder() {

                public double getValue() {
                    return value;
                }

                public int getType() {
                    return 0;
                }

                public Date getPointDataTimeStamp() {
                    return timestamp;
                }

                public int getId() {
                    return 0;
                }

            };
        }

    }

}
