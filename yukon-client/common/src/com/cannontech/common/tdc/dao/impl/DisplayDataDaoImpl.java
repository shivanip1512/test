package com.cannontech.common.tdc.dao.impl;

import static com.cannontech.common.tdc.model.IDisplay.EVENT_VIEWER_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.SOE_LOG_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.TAG_LOG_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.GLOBAL_ALARM_DISPLAY;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
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
import com.cannontech.common.search.result.SearchResults;
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
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.SystemLog;
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
    private final YukonRowMapper<DisplayData> createAlarmHistoryRowMapper =
            createRowMapper(GLOBAL_ALARM_DISPLAY);
        
    @Override
    public SearchResults<DisplayData> getAlarmHistoryDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, DateTime date) {
        if (date == null) {
            date = new DateTime(timeZone);
        }
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TIME_STAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        SqlStatementBuilder allRowsSql = new SqlStatementBuilder();
        allRowsSql.append("SELECT s.Datetime as Timestamp, y.PAOName as DeviceName, y.PAObjectID,  p.Pointname as PointName, s.Description as Description, Username, s.Pointid, s.Soe_tag");
        allRowsSql.append("FROM SystemLog s");
        allRowsSql.append("JOIN Point p ON s.PointId = p.PointId");
        allRowsSql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        allRowsSql.append("WHERE s.Datetime").gt(from);
        allRowsSql.append("    AND s.Datetime").lte(to);
        allRowsSql.append("    AND s.Type").eq_k(SystemLog.TYPE_ALARM);
        allRowsSql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        
        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createAlarmHistoryRowMapper);
        yukonJdbcTemplate.query(allRowsSql, rse);

        SearchResults<DisplayData> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getAlarmHistoryDisplayDataCount(date));
        searchResults.setResultList(rse.getResultList());
        
        return searchResults;
        
    }
    
    @Override
    public int getAlarmHistoryDisplayDataCount(DateTime date) {
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s");
        sql.append("JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE s.Datetime").gt(from);
        sql.append("  AND s.Datetime").lte(to);
        sql.append("    AND s.Type").eq_k(SystemLog.TYPE_ALARM);

        return yukonJdbcTemplate.queryForInt(sql);
    }

    
    @Override
    public SearchResults<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, DateTime date) {
        if (date == null) {
            date = new DateTime(timeZone);
        }
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TIME_STAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        SqlStatementBuilder allRowsSql = new SqlStatementBuilder();
        allRowsSql.append("SELECT s.Datetime as Timestamp, y.PAOName as DeviceName, y.PAObjectID,  p.Pointname as PointName, s.Description as Description, s.Action as AdditionalInfo, Username, s.Pointid, s.Soe_tag");
        allRowsSql.append("FROM SystemLog s");
        allRowsSql.append("JOIN Point p ON s.PointId = p.PointId");
        allRowsSql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        allRowsSql.append("WHERE s.Datetime").gt(from);
        allRowsSql.append("    AND s.Datetime").lte(to);
        allRowsSql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        
        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createEventViewerRowMapper);
        yukonJdbcTemplate.query(allRowsSql, rse);

        SearchResults<DisplayData> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getEventViewerDisplayDataCount(date));
        searchResults.setResultList(rse.getResultList());
        
        return searchResults;
    }
    
    @Override
    public int getEventViewerDisplayDataCount(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE s.Datetime").gt(from);
        sql.append("  AND s.Datetime").lte(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public int getEventViewerDisplayDataCount(DateTime date) {
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE s.Datetime").gt(from);
        sql.append("  AND s.Datetime").lte(to);
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
        Iterable<Integer> deviceIds = displayData.stream().map(DisplayData::getDeviceId).collect(Collectors.toSet());
        Map<Integer, SimpleDevice> deviceMap = Maps.uniqueIndex(deviceDao.getYukonDeviceObjectByIds(deviceIds), d -> d.getDeviceId());
        Map<Integer, PointValueQualityTagHolder> pointValues = Maps.uniqueIndex(Lists.newArrayList(asyncDynamicDataSource.getPointValuesAndTags(pointIds)), p -> p.getId());
   
        for (DisplayData data : displayData) {
            PointValueQualityTagHolder pointValue = pointValues.get(data.getPointId());
            data.setDevice(deviceMap.get(data.getDeviceId()));
            LitePoint point = pointMap.get(data.getPointId());
            data.setPointName(point.getPointName());
            Cog cog = new Cog();
            data.setCog(cog);
            // status points are not supported by the highcharts tag
            cog.setTrend(!data.getPointType().isStatus());
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
    public SearchResults<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, DateTime date) {
        if (date == null) {
            date = new DateTime(timeZone);
        }
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TIME_STAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        SqlStatementBuilder allRowsSql = new SqlStatementBuilder();
        allRowsSql.append("SELECT s.Datetime as Timestamp, y.PAOName as DeviceName, y.PAObjectID, p.Pointname as PointName, p.PointId, s.Description as Description, s.Action as AdditionalInfo, s.Millis, p.LogicalGroup");
        allRowsSql.append("FROM SystemLog s");
        allRowsSql.append("JOIN Point p ON s.PointId = p.PointId");
        allRowsSql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        allRowsSql.append("WHERE p.LogicalGroup").eq_k(PointLogicalGroups.SOE);
        allRowsSql.append("    AND s.Datetime").gt(from);
        allRowsSql.append("    AND s.Datetime").lte(to);
        allRowsSql = addSortByClause(allRowsSql, sortBy, direction);
        
        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createSoeRowMapper);
        yukonJdbcTemplate.query(allRowsSql, rse);

        SearchResults<DisplayData> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getSoeLogDisplayDataCount(date));
        searchResults.setResultList(rse.getResultList());
        
        return searchResults;
    }

    @Override
    public int getSoeLogDisplayDataCount(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE p.LogicalGroup").eq_k(PointLogicalGroups.SOE);
        sql.append("    AND s.Datetime").gt(from);
        sql.append("    AND s.Datetime").lte(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getSoeLogDisplayDataCount(DateTime date) {
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SystemLog s JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE p.LogicalGroup").eq_k(PointLogicalGroups.SOE);
        sql.append("    AND s.Datetime").gt(from);
        sql.append("    AND s.Datetime").lte(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public SearchResults<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, DateTime date) {
        if (date == null) {
            date = new DateTime(timeZone);
        }
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TIME_STAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        SqlStatementBuilder allRowsSql = new SqlStatementBuilder();
        allRowsSql.append("SELECT l.Tagtime as Timestamp, y.PAOName as DeviceName, y.PAObjectID, p.Pointname as PointName, p.PointId, l.Description as Description, l.Action as AdditionalInfo, Username, Tagname");
        allRowsSql.append("FROM TagLog l");
        allRowsSql.append("JOIN Point p ON l.PointId = p.PointId");
        allRowsSql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        allRowsSql.append("JOIN Tags t ON t.Tagid = l.Tagid");
        allRowsSql.append("    AND l.Tagtime").gt(from);
        allRowsSql.append("    AND l.Tagtime").lte(to);
        allRowsSql.append("ORDER BY").append(sortBy.getDbString()).append(direction);

        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createTagRowMapper);
        yukonJdbcTemplate.query(allRowsSql, rse);

        SearchResults<DisplayData> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getTagLogDisplayDataCount(date));
        searchResults.setResultList(rse.getResultList());
        
        return searchResults;
    }
    
    @Override
    public int getTagLogDisplayDataCount(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM  TagLog l JOIN Point p ON l.Pointid=p.Pointid");
        sql.append("JOIN YukonPAObject y ON y.PAObjectID=p.PAObjectID");
        sql.append("JOIN Tags t ON t.Tagid = l.Tagid");
        sql.append("   AND l.tagtime").gt(from);
        sql.append("   AND l.tagtime").lte(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getTagLogDisplayDataCount(DateTime date) {
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM  TagLog l JOIN Point p ON l.Pointid=p.Pointid");
        sql.append("JOIN YukonPAObject y ON y.PAObjectID=p.PAObjectID");
        sql.append("JOIN Tags t ON t.Tagid = l.Tagid");
        sql.append("   AND l.tagtime").gt(from);
        sql.append("   AND l.tagtime").lte(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    private YukonRowMapper<DisplayData> createRowMapper(final Integer displayId) {
        final YukonRowMapper<DisplayData> mapper = new YukonRowMapper<DisplayData>() {
            @Override
            public DisplayData mapRow(YukonResultSet rs) throws SQLException {
                DisplayData row = new DisplayData();
                row.setDeviceName(rs.getString("DeviceName"));
                row.setPointName(rs.getString("PointName"));
                row.setDescription(rs.getString("description"));
                if (displayId != GLOBAL_ALARM_DISPLAY) {
                    row.setAdditionalInfo(rs.getString("AdditionalInfo"));
                }
                row.setPointId(rs.getInt("pointid"));
                if (displayId == EVENT_VIEWER_DISPLAY_NUMBER || displayId == GLOBAL_ALARM_DISPLAY) {
                    row.setUserName(rs.getString("username"));
                    row.setDate(rs.getInstant("Timestamp"));
                    row.setTextMessage(rs.getString("description"));
                }
                else if (displayId == TAG_LOG_DISPLAY_NUMBER) {
                    row.setUserName(rs.getString("username"));
                    row.setTagName(rs.getString("tagname"));
                    row.setDate(rs.getInstant("Timestamp"));
                }
                else if (displayId == SOE_LOG_DISPLAY_NUMBER) {
                    row.setDate(rs.getInstant("Timestamp"));
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
    
    public SqlStatementBuilder addSortByClause(SqlStatementBuilder sql, SortBy sortBy, Direction direction) {
        sql.append("ORDER BY");
        if (sortBy == SortBy.TIME_STAMP) {
            return sql.append(sortBy.getDbString()).append(direction).append(",").append("s.Millis").append(direction);
        } else {
            return sql.append(sortBy.getDbString()).append(direction);
        }
    }
    
}
