package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.point.PointTypes;

/**
 * Implementation of RawPointHistoryDao
 */
public class RawPointHistoryDaoImpl implements RawPointHistoryDao {

    private SimpleJdbcTemplate jdbcTemplate = null;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {

        String sql = "SELECT DISTINCT                                           " + 
        "       rph.pointid,                                                    " + 
        "       rph.timestamp,                                                  " + 
        "       rph.value,                                                      " + 
        "       p.pointtype                                                     " + 
        "   FROM                                                                " + 
        "       rawpointhistory rph,                                            " + 
        "       point p                                                         " + 
        "   WHERE                                                               " + 
        "       rph.pointid IN (?)                                              " + 
        "       AND (rph.timestamp > ? AND rph.timestamp <= ? )                 " + 
        "       AND rph.pointid = p.pointid                                     " + 
        "   ORDER BY                                                            " + 
        "       rph.pointid,                                                    " + 
        "       rph.timestamp";

        return jdbcTemplate.query(sql, new LiteRPHRowMapper(), pointId, startDate, stopDate);

    }

    /**
     * Helper class which maps a result set row into a PointValueHolder
     */
    private class LiteRPHRowMapper implements ParameterizedRowMapper<PointValueHolder> {

        public PointValueHolder mapRow(ResultSet rs, int rowNum) throws SQLException {

            final int pointId = rs.getInt("pointid");
            final Timestamp timestamp = rs.getTimestamp("timestamp");
            final double value = rs.getDouble("value");

            final int type = PointTypes.getType(rs.getString("pointtype"));

            return new SimplePointValue(pointId, timestamp, type, value);
        }

    }

    public List<PointValueHolder> getIntervalPointData(int pointId, Date startDate, Date stopDate, ChartInterval resolution, Mode mode) {
        List<PointValueHolder> result = new ArrayList<PointValueHolder>();
        List<PointValueHolder> pointData = getPointData(pointId, startDate, stopDate);
        // we know this list is ordered by timestamp
        
        PointValueHolder lastGoodValue = null;
        Date lastGoodResolved = null;
        for (PointValueHolder currentValue : pointData) {
            Date currentResolved = resolution.roundDownToIntervalUnit(currentValue.getPointDataTimeStamp());
            if (lastGoodValue != null) {
                // check if current value is in the same interval as the previous
                if (currentResolved.equals(lastGoodResolved)) {
                    // okay, we need to decide which to keep
                    if (mode == Mode.HIGHEST) {
                        // we'll choose to pick the latest in a tie
                        if (currentValue.getValue() < lastGoodValue.getValue()) {
                            // ignore it
                            continue;
                        }
                    }
                } else {
                    // current value belongs in the next bin
                    // promote lastGoodValue to result
                    result.add(lastGoodValue);
                }
            }
            // set last to current
            lastGoodValue = currentValue;
            lastGoodResolved = currentResolved;
        }
        
        // now that we're done, add the last good value
        if (lastGoodValue != null) {
            result.add(lastGoodValue);
        }
        return result;
    }

}
