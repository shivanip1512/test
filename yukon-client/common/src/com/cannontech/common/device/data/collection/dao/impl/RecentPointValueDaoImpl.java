package com.cannontech.common.device.data.collection.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.google.common.collect.Lists;

public class RecentPointValueDaoImpl implements RecentPointValueDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DeviceGroupService deviceGroupService;
    private static final Logger log = YukonLogManager.getLogger(RecentPointValueDaoImpl.class);

    @Override
    public List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, boolean includeDisabled, Range<Instant> range) {
        // test LCR
        // populate point information
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT rpv.PAObjectId, PointId, Timestamp, Quality, Value, ypo.Type, dmg.MeterNumber, ypo.PAOName, dcs.Address, dr.RouteId, rypo.PAOName as Route, rfna.SerialNumber");
        sql.append("FROM RecentPointValue rpv");
        sql.append("JOIN YukonPaObject ypo ON rpv.PAObjectId = ypo.PAObjectID");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON rpv.PAObjectId = dmg.deviceId");
        sql.append("LEFT JOIN DeviceCarrierSettings dcs ON rpv.PAObjectId = dcs.deviceId");
        sql.append("LEFT JOIN DeviceRoutes dr ON rpv.PAObjectId = dr.deviceId");
        sql.append("LEFT JOIN YukonPaObject rypo ON dr.RouteId = rypo.PAObjectID");
        sql.append("LEFT JOIN RFNAddress rfna ON rpv.PAObjectId = rfna.DeviceId");
        
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "rpv.PAObjectId");
        sql.append("WHERE").appendFragment(groupSqlWhereClause);
        if (!includeDisabled) {
            sql.append("AND ypo.DisableFlag").eq_k(YNBoolean.NO);
        }
        List<DeviceCollectionDetail> values = jdbcTemplate.query(sql, new YukonRowMapper<DeviceCollectionDetail>() {
            @Override
            public DeviceCollectionDetail mapRow(YukonResultSet rs) throws SQLException {
                DeviceCollectionDetail value = new DeviceCollectionDetail();
                value.setPaoIdentifier(rs.getPaoIdentifier("PAObjectId", "Type"));
                value.setDeviceName(rs.getString("paoName"));
                if (value.getPaoIdentifier().getPaoType().isRfn()) {
                    value.setMeterSerialNumber(rs.getString("SerialNumber"));
                } else if (value.getPaoIdentifier().getPaoType().isPlc()) {
                    value.setRoute(rs.getString("Route"));
                    value.setAddress(rs.getInt("Address"));
                    value.setMeterSerialNumber(rs.getString("MeterNumber"));
                }
                return value;
            }
        });
        return values;
    }

    @Override
    @Transactional
    public void collectData(Map<PaoIdentifier, PointValueQualityHolder> recentValues) {
        log.debug("Deleting data from RecentPointValue");
        log.debug("Inserting values:" + recentValues.size());
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM RecentPointValue");
        jdbcTemplate.update(deleteSql);

        List<List<PaoIdentifier>> ids =
            Lists.partition(Lists.newArrayList(recentValues.keySet()), ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
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
}
