package com.cannontech.web.amr.usageThresholdReport.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.InstantRangeLogHelper;
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
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReport;
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
    public ThresholdReport getReportDetail(int reportId, Range<Instant> criteriaRange,
            ThresholdReportFilter filter, PagingParameters paging, SortBy sortBy, Direction direction) {
        SqlStatementBuilder allRowsSql = buildDetailSelect(reportId, criteriaRange, filter, sortBy, direction);
        SqlStatementBuilder countSql = buildDetailSelect(reportId, criteriaRange, filter, null, null);
        int totalCount = jdbcTemplate.queryForInt(countSql);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        PagingResultSetExtractor<ThresholdReportDetail> rse = new PagingResultSetExtractor<>(start, count, new DetailRowMapper(criteriaRange));
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<ThresholdReportDetail> searchResult = new SearchResults<>();
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());
        
        List<ThresholdReportDetail> allDetails = jdbcTemplate.query(allRowsSql, new DetailRowMapper(criteriaRange));
        return new ThresholdReport(searchResult, allDetails);
    }
    
    private class DetailRowMapper implements YukonRowMapper<ThresholdReportDetail> { 
        private Range<Instant> partialRange;

        public DetailRowMapper(Range<Instant> criteriaRange) {
            partialRange = getPartialRange(criteriaRange);
        }
        
        @Override
        public ThresholdReportDetail mapRow(YukonResultSet rs) throws SQLException {
            ThresholdReportDetail detail = new ThresholdReportDetail();
            if (rs.getString("PointType") != null) {
                PointType type = PointType.getForString(rs.getString("PointType"));
                int pointId = rs.getInt("PointId");
                detail.setPointId(pointId);
                int pointTypeId = type.getPointTypeId();
                detail.setEarliestReading(getPointValueQualityHolder(pointId, pointTypeId, type, rs.getDate("FirstTimestamp"), rs.getNullableDouble("FirstValue")));
                detail.setLatestReading(getPointValueQualityHolder(pointId, pointTypeId, type, rs.getDate("LastTimestamp"), rs.getNullableDouble("LastValue")));
            }
            setAvailability(detail, partialRange);
            detail.setDelta(rs.getNullableDouble("Delta"));
            detail.setPaoIdentifier(rs.getPaoIdentifier("PaoId", "Type"));
            detail.setDeviceName(rs.getString("PAOName"));
            detail.setMeterNumber(Objects.toString(rs.getString("MeterNumber"), ""));
            detail.setAddressSerialNumber(Objects.toString(rs.getString("SerialNumberAddress"), ""));
            detail.setEnabled(rs.getBoolean("DisableFlag"));
            return detail;
        }
    }
    
    /**
     * Adds availability information to detail
     */
    private void setAvailability(ThresholdReportDetail detail, Range<Instant> partialRange){
        Date min =new Date(partialRange.getMin().getMillis());
        Date max =new Date(partialRange.getMax().getMillis());
        if (detail.getPointId() == null) {
            detail.setAvailability(DataAvailability.UNSUPPORTED);
        } else if (detail.getEarliestReading() == null) {
            detail.setAvailability(DataAvailability.NONE);
        } else if (detail.getEarliestReading().getPointDataTimeStamp().before(min)
            && (detail.getLatestReading().getPointDataTimeStamp().after(max)
                || detail.getLatestReading().getPointDataTimeStamp().equals(max))) {
            /*
             * sql.append("FirstTimestamp").lt(partialRange.getMin());
             * sql.append("AND LastTimestamp").gte(partialRange.getMax())
             */
            detail.setAvailability(DataAvailability.COMPLETE);
        } else {
            detail.setAvailability(DataAvailability.PARTIAL);
        } 
    }

    /**
     * If sortBy is null returns count sql, otherwise returns search sql.
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
            sql.append( "SELECT count(utrr.PaoId)");
        } else {
            sql.append( "SELECT utrr.PaoId, utrr.PointId, utrr.FirstTimestamp, utrr.FirstValue, utrr.LastTimestamp, utrr.LastValue, utrr.Delta, ypo.Type, p.PointType, dmg.MeterNumber, "+combineSerialNumberAndAddress+" as SerialNumberAddress, ypo.PAOName, ypo.DisableFlag");
        }
    
        sql.append("FROM UsageThresholdReportRow utrr");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectId = utrr.PaoId");
        sql.append("LEFT JOIN Point p ON utrr.PointId = p.PointId");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON utrr.PaoId = dmg.deviceId");
        sql.append("LEFT JOIN DeviceCarrierSettings dcs ON utrr.PaoId = dcs.deviceId");
        sql.append("LEFT JOIN RFNAddress rfna ON utrr.PaoId = rfna.DeviceId");

        sql.append("WHERE UsageThresholdReportId").eq(reportId);

        if (filter.getAvailability() != null  && filter.getAvailability().size() > 0) {
            log.debug("Criteria Range=" + InstantRangeLogHelper.getPartialString(criteriaRange));
            Range<Instant> partialRange = getPartialRange(criteriaRange);
            log.debug("Partial Range=" + InstantRangeLogHelper.getPartialString(partialRange) + " ["
                + partialRange.getMin() + "," + partialRange.getMax() + "]");
            String OR = "";
            sql.append("AND (");
            for (DataAvailability availability : filter.getAvailability()) {
                if (availability == DataAvailability.UNSUPPORTED) {
                    sql.append(OR);
                    sql.append("p.PointId IS NULL");
                } else if (availability == DataAvailability.NONE) {
                    sql.append(OR);
                    sql.append("(FirstValue IS NULL AND p.PointId IS NOT NULL)");
                } else if (availability == DataAvailability.COMPLETE) {
                    sql.append(OR);
                    sql.append("(");
                    sql.append("FirstTimestamp").lt(partialRange.getMin());
                    sql.append("AND LastTimestamp").gte(partialRange.getMax());
                    sql.append(")");
                    log.debug(
                        availability + ":FirstTimestamp < " + InstantRangeLogHelper.getLogString(partialRange.getMin())
                            + " AND LastTimestamp >= " + InstantRangeLogHelper.getLogString(partialRange.getMax()));
                } else if (availability == DataAvailability.PARTIAL) {
                    sql.append(OR);
                    sql.append("(");
                    sql.append("FirstTimestamp").gte(partialRange.getMin());
                    sql.append("OR LastTimestamp").lt(partialRange.getMax());
                    sql.append(")");
                    log.debug(
                        availability + ":FirstTimestamp >= " + InstantRangeLogHelper.getLogString(partialRange.getMin())
                            + " OR LastTimestamp < " + InstantRangeLogHelper.getLogString(partialRange.getMax()));
                }
                OR = "OR";
            }
            sql.append(")");
        }

        if(!CollectionUtils.isEmpty(filter.getGroups())){
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "utrr.PaoId"));
        }
   
        if (filter.getThresholdDescriptor() != null) {
            if (filter.getThresholdDescriptor() == ThresholdDescriptor.GREATOR_OR_EQUAL) {
                if (filter.getAvailability().contains(DataAvailability.UNSUPPORTED)) {
                    sql.append("AND (utrr.Delta").gte(filter.getThreshold()).append("OR utrr.Delta IS NULL)");
                } else {
                    sql.append("AND utrr.Delta").gte(filter.getThreshold());
                }
            } else if (filter.getThresholdDescriptor() == ThresholdDescriptor.LESS_OR_EQUAL) {
                if (filter.getAvailability().contains(DataAvailability.UNSUPPORTED)) {
                    sql.append("AND (utrr.Delta").lte(filter.getThreshold()).append("OR utrr.Delta IS NULL)");
                } else {
                    sql.append("AND utrr.Delta").lte(filter.getThreshold());
                }
            }
        }

        if (!filter.isIncludeDisabled()) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        
        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
        log.debug(sql.getDebugSql());
        return sql;
    }

    /**
     * Returns partial range
     * 
     * CR 1 day                    1 day CR
     * |-------|------------------|-------|
     *         --------PR----------
     *         
     * CR - criteria range
     * PR - partial range
     */
    private Range<Instant> getPartialRange(Range<Instant> criteriaRange){
        Instant min = new Instant(criteriaRange.getMin().toDateTime().withTimeAtStartOfDay().plusDays(1));
        Instant max = new Instant(criteriaRange.getMax().toDateTime().withTimeAtStartOfDay().minusDays(1));
        return new Range<>(min, true, max, false);
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
        
        List<List<Object>> values = details.stream()
                .map(detail -> {
                    Date firstDate = null;
                    Double firstValue = null;
                    Date lastDate = null;
                    Double lastValue = null;
                    if(detail.getEarliestReading() != null){
                        firstDate = detail.getEarliestReading().getPointDataTimeStamp();
                        firstValue = detail.getEarliestReading().getValue();
                        lastDate = detail.getLatestReading().getPointDataTimeStamp();
                        lastValue = detail.getLatestReading().getValue();
                    }

                    List<Object> row = Lists.newArrayList(reportId,
                                                          detail.getPaoIdentifier().getPaoId(),
                                                          detail.getPointId(),
                                                          firstDate,
                                                          firstValue,
                                                          lastDate,
                                                          lastValue,
                                                          detail.getDelta());
                    return row;
                }).collect(Collectors.toList());
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.batchInsertInto("UsageThresholdReportRow")
           .columns("UsageThresholdReportId", "PaoId", "PointId", "FirstTimestamp", "FirstValue", "LastTimestamp", "LastValue", "Delta")
           .values(values);
        
        jdbcTemplate.yukonBatchUpdate(sql);
        log.debug("Done inserting into UsageThresholdReportRow");      
    }
    
    /**
     * Creates a PointValueQualityHolder for the parameters provided
     */
    private PointValueQualityHolder getPointValueQualityHolder(int pointId, int pointTypeId, PointType type, Date timeStamp, Double value){
        if(value == null){
           return null; 
        }
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
