package com.cannontech.web.amr.usageThresholdReport.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.SortBy;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;
import com.google.common.collect.Lists;

public class ThresholdReportDaoImpl implements ThresholdReportDao {
    
    private static final Logger log = YukonLogManager.getLogger(ThresholdReportDaoImpl.class);
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static class CriteriaRowMapper implements YukonRowMapper<ThresholdReportCriteria> {
        @Override
        public ThresholdReportCriteria mapRow(YukonResultSet rs) throws SQLException {
            ThresholdReportCriteria criteria = new ThresholdReportCriteria();
            criteria.setAttribute(rs.getEnum("Attribute", BuiltInAttribute.class));
            criteria.setDescription(rs.getString("DevicesDescription"));
            criteria.setStartDate(rs.getInstant("StartDate"));
            criteria.setEndDate(rs.getInstant("EndDate"));
            criteria.setReportId(rs.getInt("UsageThresholdReportId"));
            criteria.setRunTime(rs.getInstant("RunTime"));
            return criteria;
        }
    }
    
    @Override
    public int getDeviceCount(int reportId, ThresholdReportFilter filter, PagingParameters paging, SortBy sortBy,
            Direction direction) {
        return 0;
    }

    @Override
    public SearchResults<ThresholdReportDetail> getReportDetail(int reportId, Range<Instant> criteriaRange,
            ThresholdReportFilter filter, PagingParameters paging, SortBy sortBy, Direction direction) {
        return null;
    }

    @Override
    public int createReport(ThresholdReportCriteria criteria) {
        int reportId = nextValueHelper.getNextValue("UsageThresholdReport");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("UsageThresholdReport");
        sink.addValue("UsageThresholdReportId", reportId);
        sink.addValue("Attribute", criteria.getAttribute());
        sink.addValue("StartDate", criteria.getRange().getMin());
        sink.addValue("EndDate", criteria.getRange().getMax());
        sink.addValue("RunTime", new Instant());
        sink.addValue("DevicesDescription", criteria.getDescription());
        jdbcTemplate.update(sql);
        
        return reportId;
    }
    
    @Override
    public ThresholdReportCriteria getReport(int reportId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UsageThresholdReportId, Attribute, StartDate, EndDate, RunTime, DevicesDescription");
        sql.append("FROM UsageThresholdReport");
        sql.append("WHERE UsageThresholdReportId").eq(reportId);
        return jdbcTemplate.queryForObject(sql, new CriteriaRowMapper());
    }
    
    @Override
    public void deleteReport(int reportId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UsageThresholdReport");
        sql.append("AND UsageThresholdReportId").eq(reportId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void createReportDetail(int reportId, List<ThresholdReportDetail> details) {
        log.debug("Inserting values into UsageThresholdReportRow:" + details.size());
        List<List<ThresholdReportDetail>> part = Lists.partition(details, ChunkingSqlTemplate.DEFAULT_SIZE);
        part.forEach(batch -> {
            //insert 1000 rows
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO UsageThresholdReportRow");
            insertSql.append("(UsageThresholdReportId, PaoId, PointId, FirstTimestamp, FirstValue, LastTimestamp, LastValue, Delta)");
            insertSql.append("values");
            insertSql.append("(?, ?, ?, ?, ?, ?, ?, ?)");

            jdbcTemplate.batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ThresholdReportDetail detail = batch.get(i);
                    ps.setInt(1, reportId);
                    ps.setInt(2, detail.getPaoIdentifier().getPaoId());
                    if (detail.getPointId() == null) {
                        ps.setNull(3, Types.NULL);
                    } else {
                        ps.setInt(3, detail.getPointId());
                    }
                    if (detail.getEarliestReading() == null) {
                        ps.setNull(4, Types.NULL);
                        ps.setNull(5, Types.NULL);
                    } else {
                        ps.setTimestamp(4, new Timestamp(detail.getEarliestReading().getPointDataTimeStamp().getTime()));
                        ps.setDouble(5, detail.getEarliestReading().getValue());
                    }
                    if (detail.getLatestReading() == null) {
                        ps.setNull(6, Types.NULL);
                        ps.setNull(7, Types.NULL);
                    } else {
                        ps.setTimestamp(6, new Timestamp(detail.getLatestReading().getPointDataTimeStamp().getTime()));
                        ps.setDouble(7, detail.getLatestReading().getValue());
                    }
                    if (detail.getDelta() == null) {
                        ps.setNull(8, Types.NULL);
                    } else {
                        ps.setDouble(8, detail.getDelta());
                    }
                }

                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });
            log.debug("Size=" + batch.size());
        });
        log.debug("Done inserting into UsageThresholdReportRow");      
    }
}
