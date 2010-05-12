package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.collect.Ordering;

/**
 * Implementation of RawPointHistoryDao
 */
public class RawPointHistoryDaoImpl implements RawPointHistoryDao {

    private YukonJdbcTemplate yukonTemplate = null;
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    
    private SqlFragmentSource buildSql(boolean startInclusive, int pointId, Date startDate, Date stopDate, boolean reverseOrder) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT rph.changeid, rph.pointid, rph.timestamp, rph.value, rph.quality, p.pointtype");
        appendFromAndWhereClause(sql, pointId, startDate, stopDate, startInclusive);
        appendOrderByClause(sql, reverseOrder);

        return sql;
    }

    private SqlFragmentSource buildLimitedSql(boolean startInclusive, int pointId, Date startDate, Date stopDate, int maxRows, boolean reverseOrder) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder sqla = builder.buildFor(DatabaseVendor.MS2000);
        sqla.append("SELECT TOP " + maxRows);
        sqla.append("rph.changeid, rph.pointid, rph.timestamp, rph.value, rph.quality, p.pointtype");
        appendFromAndWhereClause(sqla, pointId, startDate, stopDate, startInclusive);
        appendOrderByClause(sqla, reverseOrder);

        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("select * from (");
        sqlb.append(  "SELECT DISTINCT rph.changeid, rph.pointid, rph.timestamp,");
        sqlb.append(    "rph.value, rph.quality, p.pointtype,");
        sqlb.append(    "ROW_NUMBER() over (");
        appendOrderByClause(sqlb, reverseOrder);
        sqlb.append(    ") rn");
        appendFromAndWhereClause(sqlb, pointId, startDate, stopDate, startInclusive);
        sqlb.append(") numberedRows");
        sqlb.append("where numberedRows.rn").lte(maxRows);
        sqlb.append("ORDER BY numberedRows.rn");
        
        return builder;
    }

    private void appendFromAndWhereClause(SqlBuilder sql, int pointId, Date startDate,
            Date stopDate, boolean startInclusive) {
        
        sql.append("FROM rawpointhistory rph,");
        sql.append(  "point p");
        sql.append("WHERE rph.pointid").eq(pointId); //used to be an IN clause?
        sql.append(  "AND rph.pointid = p.pointid");
        
        if (startInclusive) {
            if (startDate != null) {
                sql.append("AND rph.timestamp").gte(startDate);
            }
            if (stopDate != null) {
                sql.append("AND rph.timestamp").lt(stopDate);
            }
        } else {
            if (startDate != null) {
                sql.append("AND rph.timestamp").gt(startDate);
            }
            if (stopDate != null) {
                sql.append("AND rph.timestamp").lte(stopDate);
            }
        }
    }
    
    private void appendOrderByClause(SqlBuilder sql, boolean reverseOrder) {
        sql.append("ORDER BY rph.timestamp");
        if (reverseOrder) {
            sql.append("DESC");
        }
        sql.append(", rph.changeid");
        if (reverseOrder) {
            sql.append("DESC");
        }
    }
    
    private List<PointValueHolder> executeQuery(SqlFragmentSource sql) {
        List<PointValueHolder> result = yukonTemplate.query(sql, new LiteRphRowMapper());
        result = Collections.unmodifiableList(result);
        return result;
    }
    
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {
        return getPointData(pointId, startDate, stopDate, false, false);
    }

    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, boolean startInclusive, boolean reverseOrder) {
        SqlFragmentSource sql = buildSql(startInclusive, pointId, startDate, stopDate, reverseOrder);
        return executeQuery(sql);
    }

    public List<PointValueHolder> getLimitedPointData(int pointId, Date startDate, Date stopDate, boolean startInclusive, boolean reverseOrder, int maxRows) {
        SqlFragmentSource sql = buildLimitedSql(startInclusive, pointId, startDate, stopDate, maxRows, reverseOrder);
        return executeQuery(sql);
    }
    
    /**
     * Helper class which maps a result set row into a PointValueHolder
     */
    private class LiteRphRowMapper implements ParameterizedRowMapper<PointValueHolder> {

        public PointValueQualityHolder mapRow(ResultSet rs, int rowNum) throws SQLException {

            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
        }
    }
    
    private class LiteRPHQualityRowMapper implements ParameterizedRowMapper<PointValueQualityHolder> {

        public PointValueQualityHolder mapRow(ResultSet rs, int rowNum) throws SQLException {
            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
        }
    }

    public List<PointValueHolder> getIntervalPointData(int pointId, Date startDate, Date stopDate, ChartInterval resolution, Mode mode) {
        // unlike the other code, this expects to process things in increasing order

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
    
    @Override
    public void changeQuality(int changeId, PointQuality questionable) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update RawPointHistory");
        sql.append("set Quality").eq(questionable);
        sql.append("where ChangeId").eq(changeId);
        
        yukonTemplate.update(sql);
        
    }
    
    @Override
    public PointValueQualityHolder getPointValueQualityForChangeId(int changeId) {
    
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT");
    	sql.append("rph.pointId,");
    	sql.append("rph.timestamp,");
    	sql.append("rph.value,");
    	sql.append("rph.quality,");
    	sql.append("p.pointtype");
    	sql.append("FROM RawPointHistory rph");
    	sql.append("JOIN Point p ON (rph.pointId = p.pointId)");
    	sql.append("WHERE rph.changeId").eq(changeId);
    	
    	return yukonTemplate.queryForObject(sql, new LiteRPHQualityRowMapper());
    }

    @Override
    public List<PointValueQualityHolder> getAdjacentPointValues(final int changeId, int ... offsets) throws SQLException {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT rph.*, p.PointType");
    	sql.append("FROM RawPointHistory rph");
    	sql.append("JOIN Point p ON (rph.PointId = p.PointId)");
    	sql.append("WHERE rph.PointId = (");
    	sql.append("	SELECT rph_inner.PointId");
    	sql.append("	FROM RawPointHistory rph_inner");
    	sql.append("	WHERE rph_inner.ChangeId ").eq(changeId);
    	sql.append(")");
    	appendOrderByClause(sql, true);
    	
    	AdjacentPointValuesRse rse = new AdjacentPointValuesRse(changeId, Arrays.asList(ArrayUtils.toObject(offsets)));
        yukonTemplate.query(sql, rse);
        
        int indexOfChange = rse.getIndexOfChange();
        List<PointValueQualityHolder> resultsTail = rse.getResultsTail();
        
        List<PointValueQualityHolder> adjacentPointValues = new ArrayList<PointValueQualityHolder>(offsets.length);
        for (int offset : offsets) {
        	int index = indexOfChange - offset;
        	if (index >= 0 && index < resultsTail.size()) {
        		adjacentPointValues.add(resultsTail.get(index));
        	} else {
        		adjacentPointValues.add(null);
        	}
        }
        
        return adjacentPointValues;
    }
    
    private final class AdjacentPointValuesRse implements ResultSetExtractor {
    
    	private Integer indexOfChange = null;
    	private List<PointValueQualityHolder> resultsTail = new ArrayList<PointValueQualityHolder>();
    	int minimumIndex;
    	int changeId;
    	
    	public AdjacentPointValuesRse(int changeId, List<Integer> offsetsCollection) {
    		this.changeId = changeId;
    		this.minimumIndex = Ordering.natural().min(offsetsCollection);
    	}
		
		public int getIndexOfChange() {
			return this.indexOfChange;
		}
		public List<PointValueQualityHolder> getResultsTail() {
			return resultsTail;
		}
		
		@Override
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			
			int currentChangeId = 0;
			PointValueQualityHolder prevHolder = null;
			
			while (rs.next()) {
				
				currentChangeId = rs.getInt("ChangeId");
				
				PointValueBuilder builder = PointValueBuilder.create();
				builder.withResultSet(rs);
				builder.withType(rs.getString("PointType"));
				PointValueQualityHolder holder = builder.build();
				
				if (prevHolder == null || !holder.equals(prevHolder)) {
					resultsTail.add(holder);
					prevHolder = holder;
				}
				
            	if (currentChangeId == changeId) {
            		indexOfChange = resultsTail.size() - 1;
            	}
            	
            	if (indexOfChange != null && (resultsTail.size() - 1) >= indexOfChange - minimumIndex) {
            		return indexOfChange;
            	}
			}
			if (indexOfChange == null) {
				throw new IllegalStateException();
			}
			return null;
		}
	}
    
    @Override
    public void deleteValue(int changeId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("DELETE");
    	sql.append("FROM RawPointHistory");
    	sql.append("WHERE ChangeId").eq(changeId);
    	
    	yukonTemplate.update(sql);
    }
    
    @Autowired
    public void setJdbcTemplate(YukonJdbcTemplate yukonTemplate) {
        this.yukonTemplate = yukonTemplate;
    }
    
    @Autowired
    public void setVendorSpecificSqlBuilderFactory(
            VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }


}
