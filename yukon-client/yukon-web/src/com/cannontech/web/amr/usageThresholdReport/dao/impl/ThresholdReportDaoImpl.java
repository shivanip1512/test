package com.cannontech.web.amr.usageThresholdReport.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao;
import com.cannontech.web.amr.usageThresholdReport.model.DataAvailability;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdDescriptor;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;
import com.google.common.collect.Lists;

public class ThresholdReportDaoImpl implements ThresholdReportDao {
    
    private static final Logger log = YukonLogManager.getLogger(ThresholdReportDaoImpl.class);
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DatabaseVendorResolver databaseConnectionVendorResolver;
    @Autowired private DeviceGroupService deviceGroupService;
    
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
    public int getDeviceCount(int reportId, Range<Instant> criteriaRange, ThresholdReportFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction) {
        SqlStatementBuilder countSql = buildDetailSelect(reportId, criteriaRange, filter, null, null);
        return jdbcTemplate.queryForInt(countSql);
    }

    @Override
    public SearchResults<ThresholdReportDetail> getReportDetail(int reportId, Range<Instant> criteriaRange,
            ThresholdReportFilter filter, PagingParameters paging, SortBy sortBy, Direction direction) {
        SqlStatementBuilder allRowsSql = buildDetailSelect(reportId, criteriaRange, filter, sortBy, direction);
        SqlStatementBuilder countSql = buildDetailSelect(reportId, criteriaRange, filter, null, null);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        PagingResultSetExtractor<ThresholdReportDetail> rse = new PagingResultSetExtractor<>(start, count, new DetailRowMapper());
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<ThresholdReportDetail> searchResult = new SearchResults<>();
        int totalCount = jdbcTemplate.queryForInt(countSql);
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());
        
        return searchResult;
    }
    
    private class DetailRowMapper implements YukonRowMapper<ThresholdReportDetail> {   
        @Override
        public ThresholdReportDetail mapRow(YukonResultSet rs) throws SQLException {
            ThresholdReportDetail detail = new ThresholdReportDetail();
            if (rs.getString("PointType") != null) {
                PointType type = PointType.getForString(rs.getString("PointType"));
                int pointId = rs.getInt("PointId");
                int pointTypeId = type.getPointTypeId();
                detail.setEarliestReading(getPointValueQualityHolder(pointId, pointTypeId, type, rs.getDate("FirstTimestamp"), rs.getDouble("FirstValue")));
                detail.setLatestReading(getPointValueQualityHolder(pointId, pointTypeId, type, rs.getDate("LastTimestamp"), rs.getDouble("LastValue")));
            }
            if (detail.getPointId() == null) {
                detail.setAvailability(DataAvailability.UNSUPPORTED);
            } else if (detail.getEarliestReading() == null) {
                detail.setAvailability(DataAvailability.NONE);
            } else if(isComplete(detail)){
                detail.setAvailability(DataAvailability.COMPLETE);
            } else if(isPartial(detail)){
                detail.setAvailability(DataAvailability.PARTIAL);
            }
            detail.setDelta(rs.getDouble("Delta"));
            detail.setPaoIdentifier(rs.getPaoIdentifier("PaoId", "Type"));
            detail.setDeviceName(rs.getString("PAOName"));
            detail.setMeterNumber(Objects.toString(rs.getString("MeterNumber"), ""));
            detail.setAddressSerialNumber(Objects.toString(rs.getString("SerialNumberAddress"), ""));
            detail.setEnabled(rs.getBoolean("DisableFlag"));
            return detail;
        }

        private boolean isPartial(ThresholdReportDetail detail) {
            // TODO Auto-generated method stub
            return false;
        }

        private boolean isComplete(ThresholdReportDetail detail) {
            // TODO Auto-generated method stub
            return false;
        }
    }

    /**
     * If sortBy is not returns count sql, otherwise returns all the fields
     */
    private SqlStatementBuilder buildDetailSelect(int reportId, Range<Instant> criteriaRange,
            ThresholdReportFilter filter, SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        String combineSerialNumberAndAddress="COALESCE(rfna.SerialNumber, CAST(dcs.Address AS varchar))";
        if(databaseVendor.isOracle()){
            combineSerialNumberAndAddress="COALESCE(rfna.SerialNumber, TO_CHAR(dcs.Address))";
        }
        
        if (sortBy == null) {
            sql.append( "SELECT count(ypo.PAObjectId)");
        } else {
            sql.append( "SELECT utrr.PaoId, utrr.PointId, utrr.FirstTimestamp, utrr.FirstValue, utrr.LastTimestamp, utrr.LastValue, utrr.Delta, ypo.Type, p.PointType, dmg.MeterNumber, "+combineSerialNumberAndAddress+" as SerialNumberAddress, ypo.PAOName, ypo.DisableFlag");
        }
    
        sql.append("FROM UsageThresholdReportRow utrr");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectId = utrr.PaoId");
        sql.append("LEFT JOIN Point p ON utrr.PointId = p.PointId");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON utrr.PaoId = dmg.deviceId");
        sql.append("LEFT JOIN DeviceCarrierSettings dcs ON utrr.PaoId = dcs.deviceId");
        sql.append("LEFT JOIN RFNAddress rfna ON utrr.PaoId = rfna.DeviceId");

        sql.append("AND UsageThresholdReportId").eq(reportId);
        filter.getAvailability().forEach(availability -> {
            if (availability == DataAvailability.UNSUPPORTED) {
                sql.append("AND p.PointId IS NULL");
            } else if (availability == DataAvailability.NONE) {
                sql.append("AND FirstValue IS NULL");
            } else if (availability == DataAvailability.COMPLETE) {

            } else if (availability == DataAvailability.PARTIAL) {

            }
        });

        if(!CollectionUtils.isEmpty(filter.getGroups())){
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "utrr.PaoId"));
        }
   
        if (filter.getThresholdDescriptor() == ThresholdDescriptor.GREATOR_OR_EQUAL) {
            sql.append("AND utrr.Delta").gte(filter.getThreshold());
        } else if (filter.getThresholdDescriptor() == ThresholdDescriptor.LESS_OR_EQUAL) {
            sql.append("AND utrr.Delta").lte(filter.getThreshold());
        }
        
        if (!filter.isIncludeDisabled()) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        
        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
        System.out.println(sql.getDebugSql());
        return sql;
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
    
    private PointValueQualityHolder getPointValueQualityHolder(int pointId, int pointTypeId, PointType type, Date timeStamp, double value){
        return new PointValueQualityHolder() {
            @Override
            public int getId() {
                return pointId;
            }

            @Override
            public Date getPointDataTimeStamp() {
                return timeStamp;
            }

            @Override
            public int getType() {
                return pointTypeId;
            }

            @Override
            public double getValue() {
                return value;
            }

            @Override
            public PointQuality getPointQuality() {
                return PointQuality.Normal;
            }

            @Override
            public PointType getPointType() {
                return type;
            }
        };
    }
}
