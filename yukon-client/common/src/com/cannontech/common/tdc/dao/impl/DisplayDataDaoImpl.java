package com.cannontech.common.tdc.dao.impl;

import static com.cannontech.common.tdc.model.IDisplay.EVENT_VIEWER_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.SOE_LOG_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.TAG_LOG_DISPLAY_NUMBER;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.tdc.dao.DisplayDataDao;
import com.cannontech.common.tdc.model.Cog;
import com.cannontech.common.tdc.model.ColumnType;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointType;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DisplayDataDaoImpl implements DisplayDataDao{
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private PointDao pointDao;
    
    private final YukonRowMapper<DisplayData> createCustomRowMapper =
        createCustomDisplayRowMapper();
    private final YukonRowMapper<DisplayData> createSoeRowMapper =
        createRowMapper(SOE_LOG_DISPLAY_NUMBER);
    private final YukonRowMapper<DisplayData> createTagRowMapper =
        createRowMapper(TAG_LOG_DISPLAY_NUMBER);
    private final YukonRowMapper<DisplayData> createEventViewerRowMapper =
        createRowMapper(EVENT_VIEWER_DISPLAY_NUMBER);
    
    @Override
    public List<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone, PagingParameters paging) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Datetime, PAOName, PAObjectID, Pointname, Description, Action, Username, Pointid, Soe_tag");
        sql.append("FROM (SELECT s.Datetime, y.PAOName, y.PAObjectID,  p.Pointname, s.Description, ");
        sql.append("             s.Action, s.Username, s.Pointid, s.Soe_tag, ");
        sql.append("             ROW_NUMBER() OVER (ORDER BY s.Datetime DESC) AS counter");
        sql.append("      FROM SystemLog s");
        sql.append("      JOIN Point p ON s.PointId = p.PointId");
        sql.append("      JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("      WHERE s.Datetime").gte(from);
        sql.append("        AND s.Datetime").lt(to).append(") tbl");
        sql.append("WHERE tbl.counter BETWEEN").append(paging.getOneBasedStartIndex());
        sql.append("AND").append(paging.getOneBasedEndIndex());
        List<DisplayData> data = yukonJdbcTemplate.query(sql, createEventViewerRowMapper);
        return data;
    }
    
    @Override
    public int getEventViewerDisplayDataCount(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE s.Datetime").gte(from);
        sql.append("  AND s.Datetime").lt(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<DisplayData> getCustomDisplayData(Display display) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT d.Pointid, d.Deviceid, d.Pointtype, d.Pointname, d.Devicename, d.Pointstate, d.Devicecurrentstate");
        sql.append("FROM Display2waydata_view d join display2waydata y on d.Pointid = y.pointid");
        sql.append(" WHERE y.Displaynum").eq(display.getDisplayId());
        sql.append("ORDER BY y.Ordering");
        List<DisplayData> displayData = yukonJdbcTemplate.query(sql, createCustomRowMapper);
        List<Integer> pointIds =
            Lists.transform(Lists.newArrayList(displayData), new Function<DisplayData, Integer>() {
                @Override
                public Integer apply(DisplayData data) {
                    return data.getPointId();
                }
            });

        List<LitePoint> points = pointDao.getLitePoints(pointIds);
        Map<Integer, LitePoint> pointMap =
            Maps.uniqueIndex(points, new Function<LitePoint, Integer>() {
                @Override
                public Integer apply(LitePoint p) {
                    return p.getPointID();
                }
            });

        Iterable<Integer> deviceIds =
            ImmutableSet.copyOf(Iterables.transform(displayData,
                                                    new Function<DisplayData, Integer>() {
                                                        @Override
                                                        public Integer apply(DisplayData data) {
                                                            return data.getDeviceId();
                                                        }
                                                    })).asList();
        List<SimpleDevice> devices = deviceDao.getYukonDeviceObjectByIds(deviceIds);
        Map<Integer, SimpleDevice> deviceMap =
            Maps.uniqueIndex(devices, new Function<SimpleDevice, Integer>() {
                @Override
                public Integer apply(SimpleDevice device) {
                    return device.getDeviceId();
                }
            });

        for (DisplayData data : displayData) {
            data.setDevice(deviceMap.get(data.getDeviceId()));
            LitePoint point = pointMap.get(data.getPointId());
            data.setPointName(point.getPointName());
            Cog cog = new Cog();
            data.setCog(cog);
            // status points are not supported by the flot tag
            cog.setTrend(data.getPointType() != PointType.Status);
            int tags = dynamicDataSource.getTags(data.getPointId());
            boolean inService = !TagUtils.isDeviceOutOfService(tags) && !TagUtils.isPointOutOfService(tags);
            boolean isValidTypeForManualEntry = inService &&
                data.getPointType() == PointType.Analog
                        || data.getPointType() == PointType.PulseAccumulator
                        || data.getPointType() == PointType.DemandAccumulator
                        || data.getPointType() == PointType.CalcAnalog
                        || data.getPointType() == PointType.Status
                        || data.getPointType() == PointType.CalcStatus;
            PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(data.getPointId());
            cog.setManualEntry(display.hasColumn(ColumnType.POINT_VALUE)
                               && isValidTypeForManualEntry
                               && pointValue.getPointQuality() != PointQuality.Constant);
            cog.setAltScan(DeviceTypesFuncs.hasDeviceScanRate(data.getDevice().getDeviceType()));
            if(pointValue.getPointType() == PointType.Analog || pointValue.getPointType() == PointType.Status){
                cog.setManualControl(TagUtils.isControllablePoint(tags) && TagUtils.isControlEnabled(tags));
            }
            cog.setEnableDisable(true);
            cog.setTags(true);
        }
        return displayData;
    }

    @Override
    public List<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Datetime, PAOName, PAObjectID, PointName, PointId, Description, Action, PointId, Millis");
        sql.append("FROM (SELECT s.Datetime, y.PAOName, y.PAObjectID, p.Pointname, p.PointId, s.Description,");
        sql.append("             s.Action, s.Millis, ROW_NUMBER() OVER (ORDER BY s.Datetime DESC, s.Millis DESC) AS counter");
        sql.append("      FROM SystemLog s");
        sql.append("      JOIN Point p ON s.PointId = p.PointId");
        sql.append("      JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("      WHERE p.LogicalGroup").eq(PointLogicalGroups.LGRP_STRS[PointLogicalGroups.LGRP_SOE]);
        sql.append("        AND s.Datetime").gte(from);
        sql.append("        AND s.Datetime").lt(to).append(") tbl");
        sql.append("WHERE tbl.counter BETWEEN").append(paging.getOneBasedStartIndex());
        sql.append("AND").append(paging.getOneBasedEndIndex());
        List<DisplayData> data = yukonJdbcTemplate.query(sql, createSoeRowMapper);
        return data;
    }
    
    @Override
    public int getSoeLogDisplayDataCount(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE p.LogicalGroup").eq(PointLogicalGroups.LGRP_STRS[PointLogicalGroups.LGRP_SOE]);
        sql.append("  AND s.Datetime").gte(from);
        sql.append("  AND s.Datetime").lt(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Tagtime, PAOName, PAObjectID, PointName, PointId, Description, Action, Username, Tagname");
        sql.append("FROM (SELECT l.Tagtime, y.PAOName, y.PAObjectID, p.Pointname, p.PointId, l.Description, l.Action, l.Username, t.Tagname,");
        sql.append("             ROW_NUMBER() OVER (ORDER BY l.tagtime DESC, l.tagid DESC) AS counter ");
        sql.append("      FROM TagLog l");
        sql.append("      JOIN Point p ON l.PointId = p.PointId");
        sql.append("      JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("      JOIN Tags t ON t.Tagid = l.Tagid");
        sql.append("        AND l.Tagtime").gte(from);
        sql.append("        AND l.Tagtime").lt(to).append(") tbl");
        sql.append("WHERE tbl.counter BETWEEN").append(paging.getOneBasedStartIndex());
        sql.append("AND").append(paging.getOneBasedEndIndex());
        List<DisplayData> data = yukonJdbcTemplate.query(sql, createTagRowMapper);
        return data;
    }
    
    @Override
    public int getTagLogDisplayDataCount(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM  TagLog s JOIN Point p ON s.Pointid=p.Pointid");
        sql.append("JOIN YukonPAObject y ON y.PAObjectID=p.PAObjectID");
        sql.append("JOIN Tags t ON t.Tagid = s.Tagid");
        sql.append(" AND s.tagtime").gte(from);
        sql.append(" AND s.tagtime").lt(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    private YukonRowMapper<DisplayData> createRowMapper(final Integer displayId) {
        final YukonRowMapper<DisplayData> mapper = new YukonRowMapper<DisplayData>() {
            @Override
            public DisplayData mapRow(YukonResultSet rs) throws SQLException {
                DisplayData row = new DisplayData();
                row.setDeviceName(rs.getString("PAOName"));
                row.setPointName(rs.getString("pointname"));
                row.setDescription(rs.getString("description"));
                row.setAdditionalInfo(rs.getString("action"));
                row.setPointId(rs.getInt("pointid"));
                if (displayId == EVENT_VIEWER_DISPLAY_NUMBER) {
                    row.setUserName(rs.getString("username"));
                    row.setDate(rs.getInstant("datetime"));
                    row.setTextMessage(rs.getString("description"));
                }
                else if (displayId == TAG_LOG_DISPLAY_NUMBER) {
                    row.setUserName(rs.getString("username"));
                    row.setTagName(rs.getString("tagname"));
                    row.setDate(rs.getInstant("tagtime"));
                }
                else if (displayId == SOE_LOG_DISPLAY_NUMBER) {
                    row.setDate(rs.getInstant("datetime"));
                }
                return row;
            }
        };
        return mapper;
    }

    private YukonRowMapper<DisplayData> createCustomDisplayRowMapper() {
        final YukonRowMapper<DisplayData> mapper = new YukonRowMapper<DisplayData>() {
            @Override
            public DisplayData mapRow(YukonResultSet rs) throws SQLException {
                DisplayData row = new DisplayData();
                row.setDeviceCurrentState(rs.getStringSafe("devicecurrentstate"));
                row.setDeviceId(rs.getInt("deviceid"));
                row.setPointId(rs.getInt("pointid"));
                row.setPointEnabled(!rs.getBoolean("pointstate"));
                row.setPointType(rs.getEnum("pointtype", PointType.class));
                row.setPointName(rs.getString("pointname"));
                row.setDeviceName(rs.getString("devicename"));
                return row;
            }
        };
        return mapper;
    }
}
