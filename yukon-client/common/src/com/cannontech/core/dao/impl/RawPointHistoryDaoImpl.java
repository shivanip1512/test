package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.MaxListResultSetExtractor;
import com.cannontech.database.data.point.PointTypes;

/**
 * Implementation of RawPointHistoryDao
 */
public class RawPointHistoryDaoImpl implements RawPointHistoryDao {

    private SimpleJdbcTemplate jdbcTemplate = null;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String sqlBase = 
        "SELECT DISTINCT                                           " + 
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
        "       AND rph.pointid = p.pointid                                     ";
    
    private static final String sqlForward = 
        sqlBase + 
        "   ORDER BY " + 
        "       rph.timestamp";

    private static final String sqlBackward = 
        sqlBase + 
        "   ORDER BY " + 
        "       rph.timestamp DESC";
    
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {
        List<PointValueHolder> result;
        if (startDate.before(stopDate)) {
            result = jdbcTemplate.query(sqlForward, new LiteRPHRowMapper(), pointId, startDate, stopDate);
        } else {
            result = jdbcTemplate.query(sqlBackward, new LiteRPHRowMapper(), pointId, stopDate, startDate);
        }
        return result;
    }

    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, int maxRows) {
        MaxListResultSetExtractor<PointValueHolder> rse = new MaxListResultSetExtractor<PointValueHolder>(new LiteRPHRowMapper(), maxRows);
        if (startDate.before(stopDate)) {
            // do forward query
            Object[] arguments = new Object[] {pointId, startDate, stopDate};
            JdbcOperations oldTemplate = jdbcTemplate.getJdbcOperations();
            oldTemplate.query(sqlForward, arguments, rse);
        } else {
            // do backward query
            Object[] arguments = new Object[] {pointId, stopDate, startDate};
            JdbcOperations oldTemplate = jdbcTemplate.getJdbcOperations();
            oldTemplate.query(sqlBackward, arguments, rse);
        }
        List<PointValueHolder> result = rse.getResult();
        return result;
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
        // unlike the other code, this expects to process things in increasing order
        boolean reverseResult = false;
        if (startDate.after(stopDate)) {
            // okay, we'll just flip the result before we return it
            reverseResult = true;
            Date temp = startDate;
            startDate = stopDate;
            stopDate = temp;
        }
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
        
        if (reverseResult) {
            result = new ReverseList<PointValueHolder>(result);
        }
        return result;
    }

}
