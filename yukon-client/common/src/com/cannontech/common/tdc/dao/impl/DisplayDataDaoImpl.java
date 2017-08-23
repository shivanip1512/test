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
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT s.Datetime, y.PAOName, y.PAObjectID,  p.Pointname, s.Description, s.Action, s.Username, s.Pointid, s.Soe_tag");
        sql.append("FROM SystemLog s");
        sql.append("JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE s.Datetime").gte(from);
        sql.append("    AND s.Datetime").lt(to);
        sql.append("ORDER BY s.Datetime DESC");
        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createEventViewerRowMapper);
        yukonJdbcTemplate.query(sql, rse);
        return rse.getResultList();
    }
    
    @Override
    public List<DisplayData> getEventViewerDisplayData(DateTime date, PagingParameters paging) {
        
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT s.Datetime, y.PAOName, y.PAObjectID,  p.Pointname, s.Description, s.Action, s.Username, s.Pointid, s.Soe_tag");
        sql.append("FROM SystemLog s");
        sql.append("JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE s.Datetime").gte(from);
        sql.append("    AND s.Datetime").lt(to);
        sql.append("ORDER BY s.Datetime DESC");
        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createEventViewerRowMapper);
        yukonJdbcTemplate.query(sql, rse);
        return rse.getResultList();
    }
    
    @Override
    public SearchResults<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, DateTime date) {
        DateTime from = date.withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.SYS_LOG_DATE_TIME;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        SqlStatementBuilder allRowsSql = new SqlStatementBuilder();
        allRowsSql.append("SELECT s.Datetime, y.PAOName, y.PAObjectID,  p.Pointname, s.Description, s.Action, s.Username, s.Pointid, s.Soe_tag");
        allRowsSql.append("FROM SystemLog s");
        allRowsSql.append("JOIN Point p ON s.PointId = p.PointId");
        allRowsSql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        allRowsSql.append("WHERE s.Datetime").gte(from);
        allRowsSql.append("    AND s.Datetime").lt(to);
        allRowsSql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        
        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createEventViewerRowMapper);
        yukonJdbcTemplate.query(allRowsSql, rse);

        SearchResults<DisplayData> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getEventViewerDisplayDataCount(timeZone));
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
        sql.append("WHERE s.Datetime").gte(from);
        sql.append("  AND s.Datetime").lt(to);
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
    public List<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT s.Datetime, y.PAOName, y.PAObjectID, p.Pointname, p.PointId, s.Description, s.Action, s.Millis, p.LogicalGroup");
        sql.append("FROM SystemLog s");
        sql.append("JOIN Point p ON s.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("WHERE p.LogicalGroup").eq_k(PointLogicalGroups.SOE);
        sql.append("    AND s.Datetime").gte(from);
        sql.append("    AND s.Datetime").lt(to);
        sql.append("ORDER BY s.Datetime DESC, s.Millis DESC");

        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createSoeRowMapper);
        yukonJdbcTemplate.query(sql, rse);
        
        return rse.getResultList();
    }

    @Override
    public SearchResults<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.SYS_LOG_DATE_TIME;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        SqlStatementBuilder allRowsSql = new SqlStatementBuilder();
        allRowsSql.append("SELECT s.Datetime, y.PAOName, y.PAObjectID, p.Pointname, p.PointId, s.Description, s.Action, s.Millis, p.LogicalGroup");
        allRowsSql.append("FROM SystemLog s");
        allRowsSql.append("JOIN Point p ON s.PointId = p.PointId");
        allRowsSql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        allRowsSql.append("WHERE p.LogicalGroup").eq_k(PointLogicalGroups.SOE);
        allRowsSql.append("    AND s.Datetime").gte(from);
        allRowsSql.append("    AND s.Datetime").lt(to);
        allRowsSql = addSortByClause(allRowsSql, sortBy, direction);
        
        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createSoeRowMapper);
        yukonJdbcTemplate.query(allRowsSql, rse);

        SearchResults<DisplayData> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getSoeLogDisplayDataCount(timeZone));
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
        sql.append("    AND s.Datetime").gte(from);
        sql.append("    AND s.Datetime").lt(to);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT l.Tagtime, y.PAOName, y.PAObjectID, p.Pointname, p.PointId, l.Description, l.Action, l.Username, t.Tagname");
        sql.append("FROM TagLog l");
        sql.append("JOIN Point p ON l.PointId = p.PointId");
        sql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        sql.append("JOIN Tags t ON t.Tagid = l.Tagid");
        sql.append("    AND l.Tagtime").gte(from);
        sql.append("    AND l.Tagtime").lt(to);
        sql.append("ORDER BY l.Tagtime DESC");

        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createTagRowMapper);
        yukonJdbcTemplate.query(sql, rse);
        
        return rse.getResultList();
    }
    
    @Override
    public SearchResults<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction) {
        DateTime from = new DateTime(timeZone).withTimeAtStartOfDay();
        DateTime to = from.plusDays(1);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TAG_LOG_TAG_TIME;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        SqlStatementBuilder allRowsSql = new SqlStatementBuilder();
        allRowsSql.append("SELECT l.Tagtime, y.PAOName, y.PAObjectID, p.Pointname, p.PointId, l.Description, l.Action, l.Username, t.Tagname");
        allRowsSql.append("FROM TagLog l");
        allRowsSql.append("JOIN Point p ON l.PointId = p.PointId");
        allRowsSql.append("JOIN YukonPaobject y ON p.PaobjectId = y.PaobjectId");
        allRowsSql.append("JOIN Tags t ON t.Tagid = l.Tagid");
        allRowsSql.append("    AND l.Tagtime").gte(from);
        allRowsSql.append("    AND l.Tagtime").lt(to);
        allRowsSql.append("ORDER BY").append(sortBy.getDbString()).append(direction);

        PagingResultSetExtractor<DisplayData> rse = new PagingResultSetExtractor<>(start, count, createTagRowMapper);
        yukonJdbcTemplate.query(allRowsSql, rse);

        SearchResults<DisplayData> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getTagLogDisplayDataCount(timeZone));
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
        sql.append("   AND l.tagtime").gte(from);
        sql.append("   AND l.tagtime").lt(to);
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
    
    public SqlStatementBuilder addSortByClause(SqlStatementBuilder sql, SortBy sortBy, Direction direction) {
        sql.append("ORDER BY");
        if (sortBy == SortBy.SYS_LOG_DATE_TIME) {
            return sql.append(sortBy.getDbString()).append(direction).append(",").append(SortBy.SYS_LOG_MILLLIS.getDbString()).append(direction);
        } else {
            return sql.append(sortBy.getDbString()).append(direction);
        }
    }
    
}
