package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.MaxListResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;

/**
 * Implementation of RawPointHistoryDao
 */
public class RawPointHistoryDaoImpl implements RawPointHistoryDao {

    private YukonJdbcTemplate yukonTemplate = null;

    public void setJdbcTemplate(YukonJdbcTemplate yukonTemplate) {
        this.yukonTemplate = yukonTemplate;
    }

    private static final String sqlBasePoint = 
        "SELECT DISTINCT                                           " + 
        "       rph.pointid,                                                    " + 
        "       rph.timestamp,                                                  " + 
        "       rph.value,                                                      " + 
        "       rph.quality,                                                    " + 
        "       p.pointtype                                                     " + 
        "   FROM                                                                " + 
        "       rawpointhistory rph,                                            " + 
        "       point p                                                         " + 
        "   WHERE                                                               " + 
        "       rph.pointid IN (?)                                              " + 
        "       AND rph.timestamp = ?                                           " + 
        "       AND rph.pointid = p.pointid                                     ";
    
    private static final String sqlBase = 
        "SELECT DISTINCT                                           " + 
        "       rph.pointid,                                                    " + 
        "       rph.timestamp,                                                  " + 
        "       rph.value,                                                      " + 
        "       rph.quality,                                                    " + 
        "       p.pointtype                                                     " + 
        "   FROM                                                                " + 
        "       rawpointhistory rph,                                            " + 
        "       point p                                                         " + 
        "   WHERE                                                               " + 
        "       rph.pointid IN (?)                                              " + 
        "       AND rph.pointid = p.pointid                                     ";
    
    private String buildSql(boolean startInclusive, boolean orderForward) {
        
        String sql = sqlBase;
        
        if (startInclusive) {
            sql += " AND (rph.timestamp >= ? AND rph.timestamp < ? ) ";
        }
        else {
            sql += " AND (rph.timestamp > ? AND rph.timestamp <= ? ) ";
        }
        
        sql += " ORDER BY rph.timestamp ";
        
        if (!orderForward) {
            sql += "DESC";
        }
        
        return sql;
    }
    
    public PointValueHolder getPointData(int pointId, Date date) {
        PointValueHolder result;
        List<PointValueHolder> results = yukonTemplate.query(sqlBasePoint, new LiteRPHRowMapper(), pointId, date);
        if(results.isEmpty()){
            return null;
        }else{
            result = results.get(0);
        }

        return result;
    }
    
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {
        
        if (startDate.before(stopDate)) {
            return doGetPointData(buildSql(false, true), pointId, startDate, stopDate);
        }
        else {
            return doGetPointData(buildSql(false, false), pointId, stopDate, startDate);
        }
    }
    
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, boolean startInclusive) {
        
        if (startDate.before(stopDate)) {
            return doGetPointData(buildSql(startInclusive, true), pointId, startDate, stopDate);
        }
        else {
            return doGetPointData(buildSql(startInclusive, false), pointId, stopDate, startDate);
        }
    }
    
    private List<PointValueHolder> doGetPointData(String sql, int pointId, Date startDate, Date stopDate) {
        
        List<PointValueHolder> result = yukonTemplate.query(sql, new LiteRPHRowMapper(), pointId, startDate, stopDate);
        return result;
    }
    
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, int maxRows) {
        MaxListResultSetExtractor<PointValueHolder> rse = new MaxListResultSetExtractor<PointValueHolder>(new LiteRPHRowMapper(), maxRows);
        if (startDate.before(stopDate)) {
            // do forward query
            Object[] arguments = new Object[] {pointId, startDate, stopDate};
            JdbcOperations oldTemplate = yukonTemplate.getJdbcOperations();
            oldTemplate.query(buildSql(false, true), arguments, rse);
        } else {
            // do backward query
            Object[] arguments = new Object[] {pointId, stopDate, startDate};
            JdbcOperations oldTemplate = yukonTemplate.getJdbcOperations();
            oldTemplate.query(buildSql(false, false), arguments, rse);
        }
        List<PointValueHolder> result = rse.getResult();
        return result;
    }
    
    /**
     * Helper class which maps a result set row into a PointValueHolder
     */
    private class LiteRPHRowMapper implements ParameterizedRowMapper<PointValueHolder> {

        public PointValueQualityHolder mapRow(ResultSet rs, int rowNum) throws SQLException {

            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
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
    
    @Override
    public void changeQuality(int changeId, PointQuality questionable) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update RawPointHistory");
        sql.append("set Quality").eq(questionable);
        sql.append("where ChangeId").eq(changeId);
        
        yukonTemplate.update(sql);
        
    }

}
