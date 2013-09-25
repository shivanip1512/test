package com.cannontech.common.tdc.impl;

import static com.cannontech.common.tdc.model.IDisplay.EVENT_VIEWER_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.SOE_LOG_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.TAG_LOG_DISPLAY_NUMBER;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.tdc.dao.DisplayDataDao;
import com.cannontech.common.tdc.model.Cog;
import com.cannontech.common.tdc.model.ColumnTypeEnum;
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

    private Comparator<DisplayData> sortByDate = new Comparator<DisplayData>() {
        @Override
        public int compare(DisplayData d1, DisplayData d2) {
            return -d1.getDate().compareTo(d2.getDate());
        }
    };
    
    @Override
    public List<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT s.Datetime, y.PAOName, y.PAObjectID,  p.Pointname, s.Description, s.Action, s.Username, s.Pointid, s.Soe_tag");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE s.Datetime").gte(from);
        sql.append("and s.datetime").lt(to);
        List<DisplayData> data = yukonJdbcTemplate.query(sql, createEventViewerRowMapper);
        Collections.sort(data, sortByDate);
        return data;
    }

    @Override
    public List<DisplayData> getCustomDisplayData(Display display) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT d.Pointid, d.Deviceid, d.Pointtype, d.Pointname, d.Devicename, d.Pointstate, d.Devicecurrentstate");
        sql.append("FROM Display2waydata_view d join display2waydata y on d.Pointid = y.pointid");
        sql.append(" WHERE y.Displaynum").eq(display.getDisplayId());
        sql.append(" AND d.Pointid > 0");
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
                public Integer apply(LitePoint p) {
                    return p.getPointID();
                }
            });

        Iterable<Integer> deviceIds =
            ImmutableSet.copyOf(Iterables.transform(displayData,
                                                    new Function<DisplayData, Integer>() {
                                                        public Integer apply(DisplayData data) {
                                                            return data.getDeviceId();
                                                        }
                                                    })).asList();
        List<SimpleDevice> devices = deviceDao.getYukonDeviceObjectByIds(deviceIds);
        Map<Integer, SimpleDevice> deviceMap =
            Maps.uniqueIndex(devices, new Function<SimpleDevice, Integer>() {
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
            cog.setManualEntry(display.hasColumn(ColumnTypeEnum.POINT_VALUE)
                               && isValidTypeForManualEntry
                               && pointValue.getPointQuality() != PointQuality.Constant);
            cog.setAltScan(DeviceTypesFuncs.hasDeviceScanRate(data.getDevice().getType()));
            if(pointValue.getPointType() == PointType.Analog || pointValue.getPointType() == PointType.Status){
                cog.setManualControl(TagUtils.isControllablePoint(tags) && TagUtils.isControlEnabled(tags));
            }
            cog.setEnableDisable(true);
            cog.setTags(true);
        }
        return displayData;
    }

    @Override
    public List<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT s.datetime, y.PAOName, y.PAObjectID, p.pointname, p.pointid, s.description, s.action, s.pointid, s.millis");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE p.LogicalGroup").eq(PointLogicalGroups.LGRP_STRS[PointLogicalGroups.LGRP_SOE]);
        sql.append(" AND s.Datetime").gte(from);
        sql.append(" AND s.Datetime").lt(to);
        sql.append("ORDER BY s.Datetime, s.Millis");
        List<DisplayData> data = yukonJdbcTemplate.query(sql, createSoeRowMapper);
        Collections.sort(data, sortByDate);
        return data;
    }

    @Override
    public List<DisplayData> getTagLogDisplayData(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT s.tagtime, y.PAOName, y.PAObjectID, p.pointname, p.pointid, s.description, s.action, s.username, t.tagname");
        sql.append("FROM  TagLog s JOIN Point p ON s.Pointid=p.Pointid");
        sql.append("JOIN YukonPAObject y ON y.PAObjectID=p.PAObjectID");
        sql.append("JOIN Tags t ON t.Tagid = s.Tagid");
        sql.append(" AND s.tagtime").gte(from);
        sql.append(" AND s.tagtime").lt(to);
        sql.append("ORDER BY s.tagtime, s.tagid");
        List<DisplayData> data = yukonJdbcTemplate.query(sql, createTagRowMapper);
        Collections.sort(data, sortByDate);
        return data;
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
