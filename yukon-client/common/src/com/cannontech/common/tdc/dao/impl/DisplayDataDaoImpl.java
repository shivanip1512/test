package com.cannontech.common.tdc.dao.impl;

import static com.cannontech.common.tdc.model.IDisplay.EVENT_VIEWER_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.SOE_LOG_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.TAG_LOG_DISPLAY_NUMBER;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
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
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DisplayDataDaoImpl implements DisplayDataDao{
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DeviceDao deviceDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PointDao pointDao;
    private static final Logger log = YukonLogManager.getLogger(DisplayDataDaoImpl.class);
    
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
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
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
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
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
        Set<Integer> pointIds = displayData.stream().map(DisplayData::getPointId).collect(Collectors.toSet());
        Map<Integer, LitePoint> pointMap = Maps.uniqueIndex(pointDao.getLitePoints(pointIds), p -> p.getLiteID());
        Iterable<Integer> deviceIds = displayData.stream().map(DisplayData::getDeviceId).collect(Collectors.toList());
        Map<Integer, SimpleDevice> deviceMap = Maps.uniqueIndex(deviceDao.getYukonDeviceObjectByIds(deviceIds), d -> d.getDeviceId());
        Map<Integer, PointValueQualityTagHolder> pointValues = Maps.uniqueIndex(Lists.newArrayList(asyncDynamicDataSource.getPointValuesAndTags(pointIds)), p -> p.getId());
   
        for (DisplayData data : displayData) {
            PointValueQualityTagHolder pointValue = pointValues.get(data.getPointId());
            data.setDevice(deviceMap.get(data.getDeviceId()));
            LitePoint point = pointMap.get(data.getPointId());
            data.setPointName(point.getPointName());
            Cog cog = new Cog();
            data.setCog(cog);
            // status points are not supported by the flot tag
            cog.setTrend(data.getPointType() != PointType.Status);
            long tags = pointValue.getTags();
            boolean inService = !TagUtils.isDeviceOutOfService(tags) && !TagUtils.isPointOutOfService(tags);
            boolean isValidTypeForManualEntry = inService &&
                data.getPointType() == PointType.Analog
                        || data.getPointType() == PointType.PulseAccumulator
                        || data.getPointType() == PointType.DemandAccumulator
                        || data.getPointType() == PointType.CalcAnalog
                        || data.getPointType() == PointType.Status
                        || data.getPointType() == PointType.CalcStatus;
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
    public List<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        if (sortBy == null) {
            sortBy = SortBy.SYS_LOG_DATE_TIME;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT s.Datetime, y.PAOName, y.PAObjectID, p.Pointname, p.PointId, s.Description, s.Action, s.Millis, p.LogicalGroup");
        sql.append("FROM SystemLog s");
        sql.append("JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE p.LogicalGroup").eq(PointLogicalGroups.DEFAULT.getDbValue());
        sql.append("    AND s.Datetime").gte(from);
        sql.append("    AND s.Datetime").lt(to);
        sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        sql.append("    OFFSET").append(paging.getOneBasedStartIndex()).append("ROWS");
        sql.append("    FETCH NEXT").append(paging.getOneBasedEndIndex()).append("ROWS ONLY");
        List<DisplayData> data = yukonJdbcTemplate.query(sql, createSoeRowMapper);
        return data;
    }

    @Override
    public int getSoeLogDisplayDataCount(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE p.LogicalGroup").eq(PointLogicalGroups.SOE.getDbValue());
        sql.append("  AND s.Datetime").gte(from);
        sql.append("  AND s.Datetime").lt(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
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
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
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

    @Override
    @Transactional
    public void updateDisplay2Waydata(Integer displayId, List<Integer> pointIds) {
        if (CollectionUtils.isEmpty(pointIds)) {
            return;
        }
        
        log.debug("Inserting in Display2waydata");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Display2waydata");
        sql.append("WHERE DisplayNum").eq(displayId);
        yukonJdbcTemplate.update(sql);

        AtomicInteger ordering = new AtomicInteger(0);
        
        List<List<Object>> values = pointIds.stream()
                .map(pointId -> {
                    List<Object> row = Lists.newArrayList(displayId,
                                                          pointId,
                                                          ordering.incrementAndGet());
                    return row;
                }).collect(Collectors.toList());
        
        sql.batchInsertInto("Display2waydata")
           .columns("DisplayNum", "PointId", "Ordering")
           .values(values);
        
        yukonJdbcTemplate.yukonBatchUpdate(sql);
        log.debug("Done inserting in Display2waydata");
    }
}
