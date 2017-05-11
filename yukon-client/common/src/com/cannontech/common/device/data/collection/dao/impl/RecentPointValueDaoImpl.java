package com.cannontech.common.device.data.collection.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
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
    public List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, DeviceGroup subGroup,
            boolean includeDisabled) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectId, PAOName, Type");
        sql.append("FROM YukonPAObject");
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "PAObjectId");
        sql.append("WHERE").appendFragment(groupSqlWhereClause);
        if (subGroup != null) {
            SqlFragmentSource subGroupSqlWhereClause =
                deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(subGroup), "PAObjectId");
            sql.append("AND").appendFragment(subGroupSqlWhereClause);
        }
        if (!includeDisabled) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        sql.append("AND PAObjectId NOT IN (SELECT PAObjectId FROM RecentPointValue)");

        List<DeviceCollectionDetail> values = jdbcTemplate.query(sql, new YukonRowMapper<DeviceCollectionDetail>() {
            @Override
            public DeviceCollectionDetail mapRow(YukonResultSet rs) throws SQLException {
                DeviceCollectionDetail detail = new DeviceCollectionDetail();
                detail.setPaoIdentifier(rs.getPaoIdentifier("PAObjectId", "Type"));
                detail.setDeviceName(rs.getString("PAOName"));
                return detail;
            }
        });
        return values;
    }
    
    @Override
    public List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, DeviceGroup subGroup,
            boolean includeDisabled, Range<Instant> range) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT rpv.PAObjectId, rpv.PointId, Timestamp, Quality, Value, ypo.Type, p.PointType, dmg.MeterNumber, ypo.PAOName, dcs.Address, dr.RouteId, rypo.PAOName as Route, rfna.SerialNumber");
        sql.append("FROM RecentPointValue rpv");
        sql.append("JOIN Point p ON rpv.PointId = p.PointId");
        sql.append("JOIN YukonPaObject ypo ON rpv.PAObjectId = ypo.PAObjectID");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON rpv.PAObjectId = dmg.deviceId");
        sql.append("LEFT JOIN DeviceCarrierSettings dcs ON rpv.PAObjectId = dcs.deviceId");
        sql.append("LEFT JOIN DeviceRoutes dr ON rpv.PAObjectId = dr.deviceId");
        sql.append("LEFT JOIN YukonPaObject rypo ON dr.RouteId = rypo.PAObjectID");
        sql.append("LEFT JOIN RFNAddress rfna ON rpv.PAObjectId = rfna.DeviceId");
   
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "rpv.PAObjectId");
        sql.append("WHERE").appendFragment(groupSqlWhereClause);

        if (subGroup != null) {
            SqlFragmentSource subGroupSqlWhereClause =
                deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(subGroup), "rpv.PAObjectId");
            sql.append("AND").appendFragment(subGroupSqlWhereClause);
        }

        if (!includeDisabled) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        appendTimeStampClause(sql, range);
        
        List<DeviceCollectionDetail> values = jdbcTemplate.query(sql, new YukonRowMapper<DeviceCollectionDetail>() {
            @Override
            public DeviceCollectionDetail mapRow(YukonResultSet rs) throws SQLException {
                DeviceCollectionDetail detail = new DeviceCollectionDetail();
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
                detail.setPaoIdentifier(rs.getPaoIdentifier("PAObjectId", "Type"));
                detail.setDeviceName(rs.getString("PAOName"));
                if (detail.getPaoIdentifier().getPaoType().isRfn()) {
                    detail.setMeterSerialNumber(rs.getString("SerialNumber"));
                } else if (detail.getPaoIdentifier().getPaoType().isPlc()) {
                    detail.setRoute(rs.getString("Route"));
                    detail.setAddress(rs.getInt("Address"));
                    detail.setMeterSerialNumber(rs.getString("MeterNumber"));
                }
                return detail;
            }
        });
        return values;
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
    public int getDeviceCount(DeviceGroup group, boolean includeDisabled, Range<Instant> range) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM RecentPointValue rpv");
        if (!includeDisabled) {
            sql.append("JOIN YukonPaObject ypo ON rpv.PAObjectId = ypo.PAObjectID");
        }
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "rpv.PAObjectId");
        sql.append("WHERE").appendFragment(groupSqlWhereClause);
        if (!includeDisabled) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        appendTimeStampClause(sql, range);
        return jdbcTemplate.queryForInt(sql);
    }
    
    private void appendTimeStampClause(SqlBuilder sql, ReadableRange<Instant> dateRange) {
        
        Instant startDate = dateRange == null ? null : dateRange.getMin();
        if (startDate != null) {
            if (dateRange.isIncludesMinValue()) {
                sql.append("AND rpv.timestamp").gte(startDate);
            } else {
                sql.append("AND rpv.timestamp").gt(startDate);
            }
        }
        Instant stopDate = dateRange == null ? null : dateRange.getMax();
        if (stopDate != null) {
            if (dateRange.isIncludesMaxValue()) {
                sql.append("AND rpv.timestamp").lte(stopDate);
            } else {
                sql.append("AND rpv.timestamp").lt(stopDate);
            }
        }
    }
}
