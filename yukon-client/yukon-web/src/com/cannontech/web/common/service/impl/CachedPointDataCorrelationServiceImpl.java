package com.cannontech.web.common.service.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.util.Command;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.service.CachedPointDataCorrelationService;
import com.cannontech.web.updater.point.PointUpdateBackingService;
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
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private EmailService emailService;
    @Autowired private ConfigurationSource configSource;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    private ScheduledFuture<?> futureSchedule;

    @PostConstruct
    public void schedule() {
        reschedule(Minutes.minutes(5).getMinutes());
    }

    @Override
    public void reschedule(Integer initialDelay) {
        if(futureSchedule != null) {
            futureSchedule.cancel(true);
            futureSchedule = null;
        }
        
        if (configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE)) {
            yukonSimulatorSettingsDao.initYukonSimulatorSettings();
        } else {
            return;
        }
        
        String email = yukonSimulatorSettingsDao
                .getStringValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_NOTIFICATION_EMAIL);
        if(StringUtils.isEmpty(email)) {
            log.info("Data Cache Correlation task is not started, email is empty");
            return;
        }
        String hours = yukonSimulatorSettingsDao
                .getStringValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_FREQUENCY_HOURS);
        String groups = yukonSimulatorSettingsDao
                .getStringValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_GROUPS);
        log.info("Rescheduled correlation task to run every {} hours.", hours);
        futureSchedule = executor.scheduleAtFixedRate(() -> {
            Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(Arrays.asList(groups.split(",")));
            List<Integer> deviceIds = deviceGroupService.getDevices(deviceGroups).stream()
                    .map(device -> device.getDeviceId()).collect(Collectors.toList());
            log.info("Running correlation task on {} devices.", deviceIds.size());
            try {
                boolean hasMismatch = correlateAndLog(deviceIds, YukonUserContext.system);
                if (hasMismatch) {
                    log.info("Mismatches found. Sending email.");
                    EmailMessage emailMessage = new EmailMessage(InternetAddress.parse(email),
                            CtiUtilities.getIPAddress() + " Point Data Cache Correlation task found mismatches.",
                            "File located at " + CtiUtilities.getCacheCollerationDirPath() + ".");
                    emailService.sendMessage(emailMessage);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }, initialDelay, Integer.valueOf(hours), TimeUnit.HOURS);
    }
        
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
    public boolean correlateAndLog(List<Integer> deviceIds, YukonUserContext userContext) throws Exception {
        List<CorrelationSummary> summary = new ArrayList<>();
        
        List<LitePoint> points = pointDao.getLitePointsByDeviceIds(deviceIds);
        Map<Integer, LitePoint> pointIdsToPoint = Maps.uniqueIndex(points, LitePoint::getLiteID);
        
        Set<Integer> pointIds = pointIdsToPoint.keySet();
        Map<Integer, PointValueQualityHolder> dispatchValues = Maps
                .uniqueIndex(getDynamicPointDispatchValues(pointIds), PointValueQualityHolder::getId);
        
        Map<Integer, PointValueQualityHolder> pointUpdateBackingServiceCachedValues = Maps
                .uniqueIndex(pointUpdateBackingService.getCachedValues(pointIds), PointValueQualityHolder::getId);
        log.info("Got pointUpdateBackingService cached values {}", pointUpdateBackingServiceCachedValues.size());
        
        Map<Integer, ? extends PointValueQualityHolder> asyncDataSourceValues = Maps
                .uniqueIndex(asyncDataSource.getPointValues(pointIds), PointValueQualityHolder::getId);
        log.info("Got asyncDataSourceValues cached values {}", asyncDataSourceValues.size());

        pointIds.forEach(pointId -> {
            // 2 latest RPH values
            List<PointValueHolder> historicalValues = getMostRecentValues(pointId, userContext);
            if (!historicalValues.isEmpty()) {
                LitePoint point = pointIdsToPoint.get(pointId);
                PointValueHolder pointUpdateBackingServiceCachedValue = pointUpdateBackingServiceCachedValues.get(pointId);
                PointValueHolder asyncDataSourceValue = asyncDataSourceValues.get(pointId);
                CorrelationSummary result = checkForMatch(point, userContext, pointUpdateBackingServiceCachedValue,
                        historicalValues,
                        asyncDataSourceValue, dispatchValues.get(pointId));
                if (result != null) {
                    summary.add(result);
                }
            }
        });
        if(summary.isEmpty()) {
            log.info("Device data correlation complete. No mismatches found.");
            return false;
        }
        summary.sort(Comparator.comparingInt(value -> value.getPoint().getPaobjectID()));
        createFile(summary, userContext);
        return true;
    }
    
    private void createFile(List<CorrelationSummary> summary, YukonUserContext userContext) throws Exception {
        List<List<String>> dataRows = getDataRows(summary);
        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        String fileName = "CacheCorrelation_"+ now + ".csv";
        List<String> result = new ArrayList<>();
        result.add(String.join(",", getHeader()));
        dataRows.forEach(row -> result.add(row.stream()
                .map(value -> StringEscapeUtils.escapeCsv(value))
                .collect(Collectors.joining (","))));
        FileUtils.writeLines(new File(CtiUtilities.getCacheCollerationDirPath(), fileName), result);
        log.info("Device data correlation complete. Mismatches found. File generated: {}",
                CtiUtilities.getCacheCollerationDirPath() + File.separator + fileName);
    }
        
    private List<String> getHeader() {
        ArrayList<String> header = new ArrayList<>();
        header.add("Device Id");
        header.add("Device Name");
        header.add("Point Id");
        header.add("Point Name");
        header.add("Update Backing Service Cache");
        header.add("Async Data Source Cache");
        header.add("DYNAMICPOINTDISPATCH");
        header.add("RPH Value #1");
        header.add("RPH Value #2");
        return header;
    }
    
    private List<List<String>> getDataRows(List<CorrelationSummary> summary) {
        ArrayList<List<String>> rows = new ArrayList<>();
        summary.forEach(s -> {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(s.getPoint().getPaobjectID()));
            row.add(dbCache.getAllPaosMap().get(s.getPoint().getPaobjectID()).getPaoName());
            row.add(String.valueOf(s.getPoint().getLiteID()));
            row.add(s.getPoint().getPointName());
            row.add(s.getPointUpdateCacheFormattedValue());
            row.add(s.getAsyncDataSourceFormattedValue());
            row.add(s.getDispatchValue());
            row.add(s.getHistoricalValue1());
            row.add(s.getHistoricalValue2());
            rows.add(row);
        });
        return rows;
    }
    
    /**
     * Logs cache and historical values. if problem found returns summary
     */
    private CorrelationSummary correlate(LitePoint point, YukonUserContext userContext) {
        List<PointValueHolder> historicalValues = getMostRecentValues(point.getPointID(), userContext);
        if(historicalValues.isEmpty()) {
            return null;
        }
        
        if(log.isDebugEnabled()){
            historicalValues.forEach(value -> {
                log.debug("{}", pointFormattingService.getValueString(value, Format.FULL, userContext) + " "
                        + pointFormattingService.getValueString(value, Format.DATE, userContext));
            });
        }
        List<PointValueQualityHolder> dispatchValues = getDynamicPointDispatchValues(Sets.newHashSet(point.getPointID()));
        PointValueQualityHolder dispatchValue = dispatchValues.size() > 0 ? dispatchValues.get(0) : null;
        PointValueQualityHolder pointUpdateBackingServiceCachedValue = pointUpdateBackingService
                .getCachedValue(point.getPointID());
        PointValueQualityHolder asyncDataSourceValue = asyncDataSource.getPointValue(point.getPointID());


    /*   if (true) {
            // test file creation without mismatches
            CorrelationSummary summary = new CorrelationSummary(userContext, point, pointUpdateBackingServiceCachedValue,
                    historicalValues,
                    asyncDataSourceValue, dispatchValue);
            return summary;
        }*/
        
        return checkForMatch(point, userContext, pointUpdateBackingServiceCachedValue, historicalValues, asyncDataSourceValue, dispatchValue);
    }

    private CorrelationSummary checkForMatch(LitePoint point, YukonUserContext userContext,
            PointValueHolder pointUpdateBackingServiceCachedValue, 
            List<PointValueHolder> historicalValues,
            PointValueHolder asyncDataSourceValue, 
            PointValueHolder dispatchValue) {
        
      /* if (true) {
            // test file creation without mismatches
            CorrelationSummary summary = new CorrelationSummary(userContext, point, pointUpdateBackingServiceCachedValue,
                    historicalValues,
                    asyncDataSourceValue, dispatchValue);
            summary.logMismatch();
            return summary;
        }*/
        
        if (!isMatched(historicalValues.get(0), pointUpdateBackingServiceCachedValue, asyncDataSourceValue, userContext)) {
            notifyDispatch(point.getPointID(), historicalValues.toString() + "," + pointUpdateBackingServiceCachedValue + ","
                    + asyncDataSourceValue);
            // found a problem, returning object with information
            CorrelationSummary summary = new CorrelationSummary(userContext, point, 
                    pointUpdateBackingServiceCachedValue,
                    historicalValues,
                    asyncDataSourceValue, 
                    dispatchValue);
            summary.logMismatch();
            return summary;
        }
        return null;
    }

    private class CorrelationSummary {
        private LitePoint point;
        private PointValueHolder pointUpdateBackingServiceCachedValue;
        private List<PointValueHolder> historicalValues;
        private PointValueHolder asyncDataSourceValue;
        private PointValueHolder dispatchValue;
        private YukonUserContext userContext;

        public CorrelationSummary(YukonUserContext userContext, LitePoint point,
                PointValueHolder pointUpdateBackingServiceCachedValue,
                List<PointValueHolder> historicalValues,
                PointValueHolder asyncDataSourceValue,
                PointValueHolder dispatchValue) {
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
            return historicalValues.size() > 1 ? formatValue(historicalValues.get(1)) : "";
        }

        public String getDispatchValue() {
            return formatValue(dispatchValue);
        }

        public String getAsyncDataSourceFormattedValue() {
            return formatValue(asyncDataSourceValue);
        }

        public LitePoint getPoint() {
            return point;
        }

        public void logMismatch() {
            StringBuffer buffer = new StringBuffer(
                    "Cached values do not match historical values. Notifying Dispatch to log information pointId:"
                            + point.getLiteID());
            buffer.append(" Point name: " + point.getPointName());
            buffer.append(" Type: " + point.getPointTypeEnum());
            buffer.append(getLogString(pointUpdateBackingServiceCachedValue, "PointUpdateBackingService cache"));
            buffer.append(getLogString(asyncDataSourceValue, "AsyncDynamicDataSource cache"));
            buffer.append(getLogString(dispatchValue, "DYNAMICPOINTDISPATCH"));
            historicalValues.forEach(value -> buffer.append(getLogString(value, "RAWPOINTHISTORY")));
            log.info(buffer.toString());
        }

        private String getLogString(PointValueHolder value, String description) {
            if (value != null) {
                return " [" + description + ": " + formatValue(value) + "]";
            }
            return "";
        }

        private String formatValue(PointValueHolder value) {
            if (value == null) {
                return "";
            }
            try {
                return pointFormattingService.getValueString(value, Format.FULL, userContext) + " "
                        + pointFormattingService.getValueString(value, Format.DATE, userContext);
            } catch (IllegalArgumentException e) {
                return pointFormattingService.getValueString(value, Format.RAWVALUE, userContext);
            }
        }
    }

    /**
     * Returns true if cached values match historical values.
     */
    boolean isMatched(PointValueHolder historyValue,
            PointValueHolder pointUpdateBackingServiceCachedValue,
            PointValueHolder asyncDataSourceValue,
            YukonUserContext userContext) {
        
        log.debug(
                "Comparing values: [historicalValue {} {}] [pointUpdateBackingServiceCachedValue {} {}] [asyncDataSourceValue {} {}]",
                historyValue.getValue(), 
                pointFormattingService.getValueString(historyValue, Format.DATE, userContext),
                pointUpdateBackingServiceCachedValue.getValue(),
                pointFormattingService.getValueString(pointUpdateBackingServiceCachedValue, Format.DATE, userContext),
                asyncDataSourceValue.getValue(),
                pointFormattingService.getValueString(asyncDataSourceValue, Format.DATE, userContext)
                );
        
        if (historyValue.getPointDataTimeStamp().getTime() < pointUpdateBackingServiceCachedValue.getPointDataTimeStamp().getTime()) {
            log.debug("Historical values haven't been written to RPH yet");
            return true;
        }

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
    
    public List<PointValueHolder> getMostRecentValues(Integer pointId, YukonUserContext userContext) {        
      DateTime startOfMonth =
          new DateTime(userContext.getJodaTimeZone()).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
      DateTime endDate = new DateTime(userContext.getJodaTimeZone()).plusDays(1).withTimeAtStartOfDay();
      DateTime startDate = startOfMonth.minusMonths(1);
      Range<Date> dateRange = new Range<>(startDate.toDate(), true, endDate.toDate(), false);

      List<PointValueHolder> data =
          rawPointHistoryDao.getLimitedPointData(pointId, dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), false,
                  Order.REVERSE, 2);
        return data;
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
