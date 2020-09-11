package com.cannontech.web.common.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Sets;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.util.Command;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.service.CachedPointDataCorrelationService;
import com.cannontech.web.updater.point.PointUpdateBackingService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CachedPointDataCorrelationServiceImpl implements CachedPointDataCorrelationService {

    private static final Logger log = YukonLogManager.getLogger(CachedPointDataCorrelationServiceImpl.class);

    @Autowired private PointUpdateBackingService pointUpdateBackingService;
    @Autowired private AsyncDynamicDataSource asyncDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private DispatchClientConnection dispatch;
    @Autowired private PointDao pointDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
  //  private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void correlateAndLog(int pointId, YukonUserContext userContext) {
        boolean isErrorReportingEnabled = globalSettingDao.getBoolean(GlobalSettingType.ERROR_REPORTING);
        if (isErrorReportingEnabled) {
            try {
                CorrelationSummary summary = correlate(pointDao.getLitePoint(pointId), userContext);
                if(summary == null) {
                    log.info("Cached values match historical values for point id {} or there is no historical values.", pointId);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
    
    @Override
    public List<CorrelationSummary> correlateAndLog(List<Integer> deviceIds, YukonUserContext userContext) {
        List<CorrelationSummary> summary = new ArrayList<>();
        
        List<LitePoint> points = pointDao.getLitePointsByDeviceIds(deviceIds);
        Map<Integer, LitePoint> pointIdsToPoint = Maps.uniqueIndex(points, LitePoint::getLiteID);
        Map<Integer, List<PointValueQualityHolder>> history = getMostRecentValues(pointIdsToPoint.keySet(), 2)
                .stream().collect(Collectors.groupingBy(value -> value.getId()));
        
        if(history.isEmpty()) {
            return summary;
        }
        
        Set<Integer> pointIds = history.keySet();
        
        // if there is no history we can't correlate, skipping points without history
        Map<Integer, PointValueQualityHolder> dispatchValues = Maps
                .uniqueIndex(getDynamicPointDispatchValues(pointIds), PointValueQualityHolder::getId);
        
        Map<Integer, PointValueQualityHolder> pointUpdateBackingServiceCachedValues = Maps
                .uniqueIndex(pointUpdateBackingService.getCachedValues(pointIds), PointValueQualityHolder::getId);
        log.info("Got pointUpdateBackingService cached values {}", pointUpdateBackingServiceCachedValues.size());
        
        Map<Integer, ? extends PointValueQualityHolder> asyncDataSourceValues = Maps
                .uniqueIndex(asyncDataSource.getPointValues(pointIds), PointValueQualityHolder::getId);
        log.info("Got asyncDataSourceValues cached values {}", asyncDataSourceValues.size());

        pointIds.forEach(pointId -> {
            //2 latest history values
            List<PointValueQualityHolder> historicalValues = history.get(pointId);
            LitePoint point = pointIdsToPoint.get(pointId);
            PointValueQualityHolder pointUpdateBackingServiceCachedValue = pointUpdateBackingServiceCachedValues.get(pointId);
            PointValueQualityHolder asyncDataSourceValue = asyncDataSourceValues.get(pointId);
            CorrelationSummary result = checkForMatch(point, userContext, pointUpdateBackingServiceCachedValue, historicalValues,
                    asyncDataSourceValue, dispatchValues.get(pointId));
            if (result != null) {
                summary.add(result);
            }
        });
        summary.sort(Comparator.comparingInt(value -> value.getPoint().getPaobjectID()));
        return summary;
    }
    
    /**
     * Logs cache and historical values. if problem found returns summary
     */
    private CorrelationSummary correlate(LitePoint point, YukonUserContext userContext) {
        List<PointValueQualityHolder> historicalValues = getMostRecentValues(Sets.newHashSet(point.getPointID()), 2);
        if(historicalValues.isEmpty()) {
            return null;
        }
        List<PointValueQualityHolder> dispatchValues = getDynamicPointDispatchValues(Sets.newHashSet(point.getPointID()));
        PointValueQualityHolder dispatchValue = dispatchValues.size() > 0 ? dispatchValues.get(0) : null;
        PointValueQualityHolder pointUpdateBackingServiceCachedValue = pointUpdateBackingService
                .getCachedValue(point.getPointID());
        PointValueQualityHolder asyncDataSourceValue = asyncDataSource.getPointValue(point.getPointID());


     /*  if (true) {
      //test download without matches
            CorrelationSummary summary = new CorrelationSummary(userContext, point, pointUpdateBackingServiceCachedValue,
                    historicalValues,
                    asyncDataSourceValue);
            return summary;
        }*/
        
        return checkForMatch(point, userContext, pointUpdateBackingServiceCachedValue, historicalValues, asyncDataSourceValue, dispatchValue);
    }

    private CorrelationSummary checkForMatch(LitePoint point, YukonUserContext userContext,
            PointValueQualityHolder pointUpdateBackingServiceCachedValue, 
            List<PointValueQualityHolder> historicalValues,
            PointValueQualityHolder asyncDataSourceValue, 
            PointValueQualityHolder dispatchValue) {
        if (!isMatched(historicalValues.get(0), pointUpdateBackingServiceCachedValue, asyncDataSourceValue, userContext)) {
            notifyDispatch(point.getPointID(), historicalValues.toString() + "," + pointUpdateBackingServiceCachedValue + ","
                    + asyncDataSourceValue);
            // found a problem, returning object with information
            CorrelationSummary summary = new CorrelationSummary(userContext, point, pointUpdateBackingServiceCachedValue,
                    historicalValues,
                    asyncDataSourceValue, 
                    dispatchValue);
            summary.logMismatch();
            return summary;
        }
        return null;
    }

    public class CorrelationSummary {
        private LitePoint point;
        private PointValueQualityHolder pointUpdateBackingServiceCachedValue;
        private List<PointValueQualityHolder> historicalValues;
        private PointValueQualityHolder asyncDataSourceValue;
        private PointValueQualityHolder dispatchValue;
        private YukonUserContext userContext;

        public CorrelationSummary(YukonUserContext userContext, LitePoint point,
                PointValueQualityHolder pointUpdateBackingServiceCachedValue,
                List<PointValueQualityHolder> historicalValues, 
                PointValueQualityHolder asyncDataSourceValue,
                PointValueQualityHolder dispatchValue) {
            this.userContext = userContext;
            this.point = point;
            this.pointUpdateBackingServiceCachedValue = pointUpdateBackingServiceCachedValue;
            this.historicalValues = historicalValues;
            this.asyncDataSourceValue = asyncDataSourceValue;
            this.dispatchValue = dispatchValue;
        }

        public String getPointUpdateCacheFormattedValue() {
            return formatValue(pointUpdateBackingServiceCachedValue);
        }

        public String getHistoricalValue1() {
            return formatValue(historicalValues.get(0));
        }

        public String getHistoricalValue2() {
            if (historicalValues.size() > 1) {
                return formatValue(historicalValues.get(1));
            }
            return "";
        }

        public String getDispatchValue() {
            if (dispatchValue != null) {
                return formatValue(dispatchValue);
            }
            return "";
        }
        
        public String getAsyncDataSourceFormattedValue() {
            return formatValue(asyncDataSourceValue);
        }

        public LitePoint getPoint() {
            return point;
        }

        public void logMismatch() {
            StringBuffer buffer = new StringBuffer("Cached values do not match historical values. Notifying Dispatch to log information pointId:" + point.getLiteID());
            buffer.append(" Point name: "+  point.getPointName());
            buffer.append(" Type: "+  point.getPointTypeEnum());
            buffer.append(getLogString(pointUpdateBackingServiceCachedValue, "PointUpdateBackingService cache"));
            buffer.append(getLogString(asyncDataSourceValue, "AsyncDynamicDataSource cache"));
            buffer.append(getLogString(dispatchValue, "DYNAMICPOINTDISPATCH"));
            historicalValues.forEach(value -> buffer.append(getLogString(value, "RAWPOINTHISTORY")));
            log.info(buffer.toString());
        }

        private String getLogString(PointValueQualityHolder value, String description) {
            if (value != null) {
               return " ["+ description + ": " + formatValue(value)+"]";
            }
            return "";
        }

        private String formatValue(PointValueQualityHolder value) {
            String valueString;
            try {
                valueString = pointFormattingService.getValueString(value, Format.SHORT, userContext) + " "
                        + pointFormattingService.getValueString(value, Format.DATE, userContext);
            } catch (IllegalArgumentException e) {
                valueString = pointFormattingService.getValueString(value, Format.RAWVALUE, userContext);
            }
            return valueString;
        }
    }

    /**
     * Returns true if cached values match historical values.
     */
    boolean isMatched(PointValueQualityHolder historyValue,
            PointValueQualityHolder pointUpdateBackingServiceCachedValue,
            PointValueQualityHolder asyncDataSourceValue,
            YukonUserContext userContext) {
        if (historyValue.getPointDataTimeStamp().getTime() < pointUpdateBackingServiceCachedValue.getPointDataTimeStamp().getTime()) {
            log.debug("Historical values haven't been written to RPH yet");
            return true;
        }
        
        log.debug(
                "Comparing values: [historicalValue {} {}] [pointUpdateBackingServiceCachedValue {} {}] [asyncDataSourceValue {} {}]",
                historyValue.getValue(), 
                pointFormattingService.getValueString(historyValue, Format.DATE, userContext),
                pointUpdateBackingServiceCachedValue.getValue(),
                pointFormattingService.getValueString(pointUpdateBackingServiceCachedValue, Format.DATE, userContext),
                asyncDataSourceValue.getValue(),
                pointFormattingService.getValueString(asyncDataSourceValue, Format.DATE, userContext)
                );

        boolean matchedByValue = historyValue.getValue() == pointUpdateBackingServiceCachedValue.getValue()
            && pointUpdateBackingServiceCachedValue.getValue() == asyncDataSourceValue.getValue();
        boolean matchedByDate =
            historyValue.getPointDataTimeStamp().getTime() == pointUpdateBackingServiceCachedValue.getPointDataTimeStamp().getTime()
                && pointUpdateBackingServiceCachedValue.getPointDataTimeStamp().getTime() == asyncDataSourceValue.getPointDataTimeStamp().getTime();
        return matchedByValue && matchedByDate;
    }
    
    private void notifyDispatch(int pointId, String javaDebug) {
        final List<Integer> data = new ArrayList<Integer>(1);
        data.add(pointId);

        Command command = new Command();
        command.setUserName(YukonUserContext.system.getYukonUser().getUsername());
        command.setOpString(javaDebug);
        command.setOperation(Command.POINT_DATA_DEBUG);
        command.setOpArgList(data);

        dispatch.queue(command);
    }
    
    public List<PointValueQualityHolder> getMostRecentValues(Set<Integer> pointIds, int rows) {        
      List<PointValueQualityHolder> values = new ArrayList<>();
      List<List<Integer>> lists = Lists.partition(Lists.newArrayList(pointIds), ChunkingSqlTemplate.DEFAULT_SIZE);
        AtomicInteger proccessed = new AtomicInteger(0);
        AtomicInteger unproccessed = new AtomicInteger(0);
        lists.forEach(list -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("WITH TopRows AS (");
            sql.append("SELECT ROW_NUMBER() OVER ( PARTITION BY rph.pointId ORDER BY Timestamp DESC, Changeid DESC) as rowNumber, rph.pointId, rph.timestamp, rph.value, rph.quality, p.pointtype");
            sql.append("FROM RawPointHistory rph");
            sql.append("JOIN Point p ON rph.pointId = p.pointId");
            sql.append("WHERE rph.pointId").in(list);
            sql.append(")");
            sql.append("SELECT pointId, timestamp, value, quality, pointtype FROM TopRows");
            sql.append("WHERE rowNumber").lte(rows);
            try {
                values.addAll(jdbcTemplate.query(sql, new LiteRPHQualityRowMapper()));
                proccessed.addAndGet(list.size());
                log.debug("Proccessed rows:{}", list.size());
            } catch (Exception e) {
                log.error("Unable to proccess rows:{}", list.size(), e);
                unproccessed.addAndGet(list.size());
            }
        });

        log.info("Points:{} Success:{} Failure:{} RPH rows returned:{}", pointIds.size(),
                proccessed.get(), unproccessed.get(),
                values.size());
        return values;
    }
    
    private class LiteRPHQualityRowMapper implements YukonRowMapper<PointValueQualityHolder> {
        @Override
        public PointValueQualityHolder mapRow(YukonResultSet rs) throws SQLException {
            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            return builder.build();
        }
    }
    
    private List<PointValueQualityHolder> getDynamicPointDispatchValues(Set<Integer> pointIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT d.pointId, d.timestamp, d.value, d.quality, p.pointtype");
                sql.append("FROM DYNAMICPOINTDISPATCH d");
                sql.append("JOIN Point p ON (d.pointId = p.pointId)");
                sql.append("WHERE d.pointId").in(subList);
                return sql;
            }
        };
        
        List<PointValueQualityHolder> values = template.query(sqlGenerator, pointIds, new LiteRPHQualityRowMapper());
        log.info("Points:{} DYNAMICPOINTDISPATCH rows returned:{}", pointIds.size(), values.size());
        return values;
    }
    
}
