package com.cannontech.common.device.data.collection.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class RecentPointValueDaoImpl implements RecentPointValueDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DatabaseVendorResolver databaseConnectionVendorResolver;
    @Autowired private RfnGatewayService rfnGatewayService;

    private static final Logger log = YukonLogManager.getLogger(RecentPointValueDaoImpl.class);
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public SearchResults<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, List<DeviceGroup> groups,
            boolean includeDisabled, Integer[] selectedGatewayIds, Map<RangeType, Range<Instant>> ranges, PagingParameters paging, SortBy sortBy, Direction direction) {

        SqlStatementBuilder allRowsSql = buildDetailSelect(group, groups, includeDisabled, selectedGatewayIds, ranges, sortBy, direction);
        SqlStatementBuilder countSql = buildDetailSelect(group, groups, includeDisabled, selectedGatewayIds, ranges, null, null);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        PagingResultSetExtractor<DeviceCollectionDetail> rse = new PagingResultSetExtractor<>(start, count, new DetailRowMapper(ranges));
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<DeviceCollectionDetail> searchResult = new SearchResults<>();
        int totalCount = jdbcTemplate.queryForInt(countSql);
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());
        
        return searchResult;
    }
    
    @Override
    public List<RfnGateway> getRfnGatewayList(DeviceGroup group, List<DeviceGroup> groups, boolean includeDisabled) {       
        SqlStatementBuilder allGatewaysSql = buildGatewaySelect(group, groups, includeDisabled);

        return jdbcTemplate.query(allGatewaysSql, TypeRowMapper.INTEGER)
                            .stream()
                            .map(rfnGatewayService::getGatewayByPaoId)
                            .collect(Collectors.toList());
    }
    
    /**
     * Creates sql string that selects all non-null gateways in the specified group/s
     */
    private SqlStatementBuilder buildGatewaySelect(DeviceGroup group, List<DeviceGroup> groups, boolean includeDisabled) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append( "SELECT DISTINCT drdd.GatewayId");      
        sql.append("FROM YukonPaObject ypo");
        sql.append("LEFT JOIN DynamicRfnDeviceData drdd on ypo.PAObjectID = drdd.DeviceId");
        
        sql.append("WHERE").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "ypo.PAObjectId"));

        if(groups != null && !groups.isEmpty()){
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(groups, "ypo.PAObjectId"));
        }
   
        if (!includeDisabled) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        
        sql.append("AND drdd.GatewayId IS NOT NULL");

        sql.append("ORDER BY drdd.GatewayId asc");

        return sql;
    }

    /**
     * If sortBy is not returns count sql, otherwise returns all the fields
     */
    private SqlStatementBuilder buildDetailSelect(DeviceGroup group, List<DeviceGroup> groups, boolean includeDisabled,
            Integer[] selectedGatewayIds, Map<RangeType, Range<Instant>> ranges, SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        String combineSerialNumberAndAddress="COALESCE(rfna.SerialNumber, CAST(dcs.Address AS varchar))";
        if(databaseVendor.isOracle()){
            combineSerialNumberAndAddress="COALESCE(rfna.SerialNumber, TO_CHAR(dcs.Address))";
        }
        if (sortBy == null) {
            sql.append( "SELECT count(ypo.PAObjectId)");
        } else {
            sql.append( "SELECT "
                           + "ypo.PAObjectId, "
                           + "rpv.PointId, "
                           + "rpv.Timestamp, "
                           + "rpv.Quality, "
                           + "rpv.Value, "
                           + "ypo.Type, "
                           + "p.PointType, "
                           + "dmg.MeterNumber, "
                           + combineSerialNumberAndAddress+" as SerialNumberAddress, "
                           + "ypo.PAOName, "
                           + "ypo.DisableFlag, "
                           + "dr.RouteId, "
                           + "rypo.PAOName as Route, "
                           + "drdd.GatewayId, "
                           + "gypo.PAOName AS GatewayName, "
                           + "gypo.Type AS GatewayType");
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
        sql.append("LEFT JOIN DynamicRfnDeviceData drdd on ypo.PAObjectID = drdd.DeviceId");
        sql.append("LEFT JOIN YukonPAObject gypo ON drdd.GatewayId = gypo.PAObjectID");
   
        sql.append("WHERE").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "ypo.PAObjectId"));

        if(groups != null && !groups.isEmpty()){
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(groups, "ypo.PAObjectId"));
        }
   
        if (!includeDisabled) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        
        if (selectedGatewayIds != null) {
            HashSet<Integer> gatewaySet = new HashSet<>(Arrays.asList(selectedGatewayIds));
            sql.append("AND drdd.GatewayId").in(gatewaySet);
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
     * OR ( ( rpv.timestamp > ? AND rpv.timestamp <= ? ) 
     * OR ( rpv.timestamp > ? AND rpv.timestamp <= ? ) 
     * OR ( rpv.timestamp > ? AND rpv.timestamp <= ? ) 
     * OR  rpv.timestamp <= ? ) ) 
     * 
     * AVAILABLE and OUTDATED only
     * AND ( ( rpv.timestamp > ? AND rpv.timestamp <= ? ) 
     * OR ( rpv.timestamp > ? AND rpv.timestamp <= ? ) )
     * 
     * UNAVAILABLE only
     * AND ( rpv.Timestamp is NULL OR (  rpv.timestamp <= ? ) )
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
            i++;
        }
        if (ranges.containsKey(RangeType.UNAVAILABLE)) {
            sql.append(")");
        }
        sql.append(")");
        return sql;
    }

    
    private class DetailRowMapper implements YukonRowMapper<DeviceCollectionDetail> {
        private Map<Range<Instant>, RangeType> rangeToType;
        public DetailRowMapper(Map<RangeType, Range<Instant>> ranges){
            rangeToType = ranges.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        }
        
        @Override
        public DeviceCollectionDetail mapRow(YukonResultSet rs) throws SQLException {
            DeviceCollectionDetail detail = new DeviceCollectionDetail();
            detail.setRange(RangeType.UNAVAILABLE);
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
                detail.setRange(getRangeType(new Instant(pointDataTimeStamp)));
            }
            detail.setPaoIdentifier(rs.getPaoIdentifier("PAObjectId", "Type"));
            if (rs.getEnum("GatewayType", PaoType.class) != null) {
                detail.setGatewayPaoIdentifier(rs.getPaoIdentifier("GatewayId", "GatewayType"));
            }
            detail.setDeviceName(rs.getString("PAOName"));
            detail.setGatewayName(rs.getString("gatewayName"));
            detail.setMeterNumber(Objects.toString(rs.getString("MeterNumber"), ""));
            detail.setRoute(Objects.toString(rs.getString("Route"), ""));
            detail.setAddressSerialNumber(Objects.toString(rs.getString("SerialNumberAddress"), ""));
            detail.setEnabled(rs.getBoolean("DisableFlag"));
            return detail;
        }

        /**
         * Finds range type based on the point data timestamp.
         */
        private RangeType getRangeType(Instant pointDate) {       
            for (Range<Instant> range : rangeToType.keySet()) {
                boolean containsPointDate = (pointDate.isAfter(range.getMin()) && !pointDate.isAfter(range.getMax()));
                if (containsPointDate) {
                    return rangeToType.get(range);
                }
            }
            return RangeType.UNAVAILABLE;
        }
    }

    @Override
    @Transactional
    public void collectData(Map<PaoIdentifier, PointValueQualityHolder> recentValues) {
          
        log.debug("Attempting to insert values:" + recentValues.size());
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);
        List<Integer> pointIds = recentValues.values().stream().map(point -> point.getId()).collect(Collectors.toList());
        
        Multimap<Integer, Date> existingData = template.multimappedQuery(subList -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT PAObjectId, PointId, Timestamp");
            sql.append("FROM RecentPointValue");
            sql.append("WHERE PointId").in(subList);
            return sql;
        }, pointIds, rs -> Maps.immutableEntry(rs.getInt("PointId"), rs.getDate("Timestamp")), Functions.identity());

      
        for (Entry<PaoIdentifier, PointValueQualityHolder> value : recentValues.entrySet()) {
            Date newTime = value.getValue().getPointDataTimeStamp();
            if (!existingData.get(value.getValue().getId()).isEmpty()) {
                Date timeInTheTable = existingData.get(value.getValue().getId()).iterator().next();
                if (timeInTheTable.after(newTime) || timeInTheTable.equals(newTime)) {
                    log.debug("---RecentPointValue[pointId=" + value.getValue().getId() + ",time:"
                        + DATE_FORMAT.format(timeInTheTable) + "]. The new time " + DATE_FORMAT.format(newTime)
                        + " will not be inserted in RecentPointValue because it is older or the same as value in RecentPointValue.");
                    recentValues.remove(value.getKey());
                }
            }
        }
        
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
    public int getDeviceCount(DeviceGroup group, boolean includeDisabled, Integer[] selectedGatewayIds, RangeType type, Range<Instant> range) {
        
        Map<RangeType, Range<Instant>> ranges = new HashMap<>();
        ranges.put(type, range);
        SqlStatementBuilder countSql = buildDetailSelect(group, null, includeDisabled, selectedGatewayIds, ranges, null, null);
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
