package com.cannontech.common.device.data.collection.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.Lists;

public class RecentPointValueDaoImpl implements RecentPointValueDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DeviceGroupService deviceGroupService;

    private static final Logger log = YukonLogManager.getLogger(RecentPointValueDaoImpl.class);

    @Override
    public SearchResults<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group1, DeviceGroup group2,
            boolean includeDisabled, Map<RangeType, Range<Instant>> ranges, PagingParameters paging, SortBy sortBy, Direction direction) {

        SqlStatementBuilder allRowsSql = buildDetailSelect(group1, group2, includeDisabled, ranges, sortBy, direction);
        SqlStatementBuilder countSql = buildDetailSelect(group1, group2, includeDisabled, ranges, null, null);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        PagingResultSetExtractor<DeviceCollectionDetail> rse = new PagingResultSetExtractor<>(start, count, new DetailRowMapper());
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<DeviceCollectionDetail> searchResult = new SearchResults<>();
        int totalCount = jdbcTemplate.queryForInt(countSql);
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());
        
        System.out.println(allRowsSql.getDebugSql());
        return searchResult;
    }

    /**
     * If sortBy is not returns count sql, otherwise returns all the fields
     */
    private SqlStatementBuilder buildDetailSelect(DeviceGroup group1, DeviceGroup group2, boolean includeDisabled,
            Map<RangeType, Range<Instant>> ranges, SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (sortBy == null) {
            sql.append( "SELECT count(*)");
        } else {
            sql.append( "SELECT ypo.PAObjectId, rpv.PointId, Timestamp, Quality, Value, ypo.Type, p.PointType, COALESCE(rfna.SerialNumber, dmg.MeterNumber) as SerialNumber, ypo.PAOName, dcs.Address, dr.RouteId, rypo.PAOName as Route");
        }
        sql.append("FROM YukonPaObject ypo");
        if (ranges.containsKey(RangeType.UNAVAILABLE)) {
            sql.append("LEFT JOIN RecentPointValue rpv ON ypo.PAObjectId = rpv.PAObjectID");
        } else {
            sql.append("JOIN RecentPointValue rpv ON ypo.PAObjectId = rpv.PAObjectID");
        }
        sql.append("LEFT JOIN Point p ON rpv.PointId = p.PointId");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON ypo.PAObjectId = dmg.deviceId");
        sql.append("LEFT JOIN DeviceCarrierSettings dcs ON ypo.PAObjectId = dcs.deviceId");
        sql.append("LEFT JOIN DeviceRoutes dr ON ypo.PAObjectId = dr.deviceId");
        sql.append("LEFT JOIN YukonPaObject rypo ON dr.RouteId = rypo.PAObjectID");
        sql.append("LEFT JOIN RFNAddress rfna ON ypo.PAObjectId = rfna.DeviceId");
   
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group1), "ypo.PAObjectId");
        sql.append("WHERE").appendFragment(groupSqlWhereClause);

        if (group2 != null) {
            SqlFragmentSource subGroupSqlWhereClause =
                deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group2), "ypo.PAObjectId");
            sql.append("AND").appendFragment(subGroupSqlWhereClause);
        }

        if (!includeDisabled) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        
        sql.append(getRangeSql(ranges));

        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
        return sql;
    }
    
    /**
     * Examples:
     * 
     * sql generated for all 4 
     * AND ( rpv.Timestamp is NULL 
     * OR ( ( rpv.timestamp >= ? AND rpv.timestamp < ? ) 
     * OR ( rpv.timestamp >= ? AND rpv.timestamp < ? ) 
     * OR ( rpv.timestamp >= ? AND rpv.timestamp < ? ) 
     * OR  rpv.timestamp < ? ) ) 
     * 
     * AVAILABLE and OUTDATED only
     * AND ( ( rpv.timestamp >= ? AND rpv.timestamp < ? ) 
     * OR ( rpv.timestamp >= ? AND rpv.timestamp < ? ) )
     * 
     * UNAVAILABLE only
     * AND ( rpv.Timestamp is NULL OR (  rpv.timestamp < ? ) )
     * 
     * 
     * NOTE: ranges sorted by keys in alphabetical order -  UNAVAILABLE is always last
     *       rpv.Timestamp is NULL - needs only to be checked in case of UNAVAILABLE, group contains a device that doesn't have an entry in RecentPointValue
     */    
    private SqlStatementBuilder getRangeSql(Map<RangeType, Range<Instant>> ranges){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("AND (");
        if(ranges.containsKey(RangeType.UNAVAILABLE)){
            sql.append("rpv.Timestamp is NULL OR (");
        }
        int i = 0;
        for (Entry<RangeType, Range<Instant>> entry : ranges.entrySet()) {
            if (i != 0) {
                sql.append("OR");
            }
            if (entry.getKey() == RangeType.UNAVAILABLE) {
                appendTimeStampClause(sql, entry.getValue());
            } else {
                sql.append("(");
                appendTimeStampClause(sql, entry.getValue());
                sql.append(")");
            }
            System.out.println(entry.getValue());
            i++;
        }
        if (ranges.containsKey(RangeType.UNAVAILABLE)) {
            sql.append(")");
        }
        sql.append(")");
        return sql;
    }

    
    private class DetailRowMapper implements YukonRowMapper<DeviceCollectionDetail> {
        @Override
        public DeviceCollectionDetail mapRow(YukonResultSet rs) throws SQLException {
            DeviceCollectionDetail detail = new DeviceCollectionDetail();
            if (rs.getString("PointType") != null) {
                int pointId = rs.getInt("PointId");
                Date pointDataTimeStamp = rs.getDate("Timestamp");
                double pointValue = rs.getDouble("Value");
                PointQuality quality = PointQuality.getPointQuality(rs.getInt("Quality"));
                PointType pointType = PointType.getForString(rs.getString("PointType"));
                PointValueQualityHolder value = new PointValueQualityHolder() {
                    @Override
                    public int getId() {
                        return pointId;
                    }

                    @Override
                    public Date getPointDataTimeStamp() {
                        return pointDataTimeStamp;
                    }

                    @Override
                    public int getType() {
                        return pointType.getPointTypeId();
                    }

                    @Override
                    public double getValue() {
                        return pointValue;
                    }

                    @Override
                    public PointQuality getPointQuality() {
                        return quality;
                    }

                    @Override
                    public PointType getPointType() {
                        return pointType;
                    }
                };
                detail.setValue(value);
            }
            detail.setPaoIdentifier(rs.getPaoIdentifier("PAObjectId", "Type"));
            detail.setDeviceName(rs.getString("PAOName"));
            detail.setMeterSerialNumber(rs.getString("SerialNumber"));
            if (detail.getPaoIdentifier().getPaoType().isPlc()) {
                detail.setRoute(rs.getString("Route"));
                detail.setAddress(rs.getInt("Address"));
            }
            return detail;
        }
    }

    @Override
    @Transactional
    public void collectData(Map<PaoIdentifier, PointValueQualityHolder> recentValues) {
        
        log.debug("Inserting values:" + recentValues.size());
        List<List<PaoIdentifier>> ids =
            Lists.partition(Lists.newArrayList(recentValues.keySet()), ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
            //delete 1000 rows
            SqlStatementBuilder deleteSql = new SqlStatementBuilder();
            deleteSql.append("DELETE FROM RecentPointValue");
            deleteSql.append("WHERE PAObjectId").in(idBatch.stream().map(PaoIdentifier::getPaoId).collect(Collectors.toList()));
            jdbcTemplate.update(deleteSql);
            
            //insert 1000 rows
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO RecentPointValue");
            insertSql.append("(PAObjectId, PointId, Timestamp, Quality, Value)");
            insertSql.append("values");
            insertSql.append("(?, ?, ?, ?, ?)");

            jdbcTemplate.batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    PointValueQualityHolder holder = recentValues.get(idBatch.get(i));
                    ps.setInt(1, idBatch.get(i).getPaoId());
                    ps.setInt(2, holder.getId());
                    ps.setTimestamp(3, new Timestamp(holder.getPointDataTimeStamp().getTime()));
                    ps.setString(4, holder.getPointQuality().getDatabaseRepresentation().toString());
                    ps.setDouble(5, holder.getValue());
                }

                @Override
                public int getBatchSize() {
                    return idBatch.size();
                }
            });
            log.debug("Size=" + idBatch.size());
        });
        log.debug("Done inserting in RecentPointValue");
    }

    @Override
    public int getDeviceCount(DeviceGroup group, boolean includeDisabled, RangeType type, Range<Instant> range) {
        
        Map<RangeType, Range<Instant>> ranges = new HashMap<>();
        ranges.put(type, range);
        SqlStatementBuilder countSql = buildDetailSelect(group, null, includeDisabled, ranges, null, null);
        return jdbcTemplate.queryForInt(countSql);
    }
    
    
    private void appendTimeStampClause(SqlBuilder sql, ReadableRange<Instant> dateRange) {

        Instant startDate = dateRange == null ? null : dateRange.getMin();
        if (startDate != null) {
            if (dateRange.isIncludesMinValue()) {
                sql.append("rpv.timestamp").gte(startDate);
            } else {
                sql.append("rpv.timestamp").gt(startDate);
            }
        }
        Instant stopDate = dateRange == null ? null : dateRange.getMax();
        if (stopDate != null) {
            String end = startDate == null ? "": " AND ";
            if (dateRange.isIncludesMaxValue()) {
                sql.append(end).append("rpv.timestamp").lte(stopDate);
            } else {
                sql.append(end).append("rpv.timestamp").lt(stopDate);
            }
        }
    }
}
