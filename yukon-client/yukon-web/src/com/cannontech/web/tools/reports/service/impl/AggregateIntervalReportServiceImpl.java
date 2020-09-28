package com.cannontech.web.tools.reports.service.impl;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService;
import com.google.common.collect.ListMultimap;

public class AggregateIntervalReportServiceImpl implements AggregateIntervalReportService {

    private static final Logger log = YukonLogManager.getLogger(AggregateIntervalReportServiceImpl.class);
            
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceDao deviceDao;
    final static DecimalFormat decimalFormatter = new DecimalFormat("0.00");
    
    /* example of adding new 5 min interval: TimeIntervals.MINUTES_5, ChronoUnit.MINUTES
    no other changes required */
    private static final Map<TimeIntervals, ChronoUnit> intervalDefinition = Map.of(
            TimeIntervals.HOURS_1, ChronoUnit.HOURS,
            TimeIntervals.DAYS_1, ChronoUnit.DAYS,
            TimeIntervals.MINUTES_15, ChronoUnit.MINUTES,
            TimeIntervals.MINUTES_30, ChronoUnit.MINUTES);
    
    @Override
    public List<List<String>> getIntervalDataReport(AggregateIntervalReportFilter filter, YukonUserContext context) {
        
        List<PaoIdentifier> devices = getDevices(filter);
        log.info("Generating report date range:{}-{} devices:{} interval:{}", format(filter.getStartDate(), context),
                format(filter.getEndDate(), context), devices.size(), filter.getInterval());
        if(devices.isEmpty()) {
            return new ArrayList<>();
        }
         
        log.debug(filter);
        
        IntervalParser intervalParser = new IntervalParser(filter.getStartDate(), filter.getEndDate(), filter.getInterval(),
                context);
        if(!intervalParser.hasValidInterval()) {
            return new ArrayList<>();
        }

        // > start date <= end date
    /*   Range<Instant> range = Range.exclusiveInclusive(filter.getStartDate(), filter.getEndDate());

       ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                rawPointHistoryDao.getAttributeData(devices,
                                                    filter.getAttribute(),
                                                    range, //> start date <= end date
                                                    null,
                                                    false, //excludes disabled devices
                                                    Order.FORWARD,
                                                    null,
                                                    null);*/
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                rawPointHistoryDao.getAttributeData(devices,
                                                    filter.getAttribute(),
                                                    intervalParser.getRange(),
                                                    null,
                                                    false, //excludes disabled devices
                                                    Order.FORWARD,
                                                    null,
                                                    null);
                
        if(attributeData.isEmpty()) {
            return new ArrayList<>();
        }
        
        //group by interval
        Map<Date, List<PointValueQualityHolder>> intervalData =  attributeData.values().stream()
                .sorted(Comparator.comparing(PointValueQualityHolder::getPointDataTimeStamp))
                .filter(value -> intervalParser.containsInterval(value.getPointDataTimeStamp()))
                .collect(Collectors.groupingBy(PointValueQualityHolder::getPointDataTimeStamp));

        List<List<String>> report = getReport(filter, intervalData, intervalParser.getIntervals(), devices, context);
        log.info("Generated report date range:{}-{} devices:{} interval:{} report rows:{}", format(filter.getStartDate(), context),
                format(filter.getEndDate(), context), devices.size(), filter.getInterval(), report.size());
        return report;
    }
     
    private List<List<String>> getReport(AggregateIntervalReportFilter filter,
            Map<Date, List<PointValueQualityHolder>> intervalData, Set<Date> intervals, List<PaoIdentifier> devices,
            YukonUserContext context) {
        List<List<String>> report = new ArrayList<>();
        intervals.forEach(interval -> {
            List<PointValueQualityHolder> data = intervalData.get(interval);
            
            if(data.size() > devices.size()) {
                /* multiple rows per point
                 * example: point id = 4485645 09/27/2020 23:00:00 value:1 and  point id = 4485645 09/27/2020 23:00:00 value:2
                 */
                
                //keep first row
                data = data.stream().collect(Collectors.groupingBy(p -> p.getId())).values().stream()
                        .map(values -> values.stream().findFirst().get())
                        .collect(Collectors.toList());
            }
            // one row per device
            boolean isCompleteData =  data != null && data.size() == devices.size();

            String value = null;
            if (isCompleteData) {
                value = getValue(data, filter.getOperation());
            } else {
                if (filter.getMissingIntervalData() == MissingIntervalData.PARTIAL) {
                    value = data.isEmpty() ? "0" : getValue(data, filter.getOperation());
                } else if (filter.getMissingIntervalData() == MissingIntervalData.BLANK) {
                    value = "";
                } else if (filter.getMissingIntervalData() == MissingIntervalData.FIXED_VALUE) {
                    value = filter.getMissingIntervalDataValue();
                }
            }
            if (value != null) {
                report.add(createRow(interval, value, context));
            }
        });
        return report;
    }

    private List<String> createRow(Date interval, String value, YukonUserContext context){
        List<String> row = new ArrayList<>();
        row.add(dateFormattingService.format(interval, DateFormatEnum.DATE, context));
        row.add(dateFormattingService.format(interval, DateFormatEnum.TIME, context));
        row.add(value);
        return row;
    }

    private String getValue(List<PointValueQualityHolder> data, Operation operation) {
        if(operation == Operation.ADD) {
           return decimalFormatter.format(data.stream()
                   .map(value -> value.getValue())
                   .mapToDouble(value -> value.doubleValue())
                   .sum())
                   .toString();
        } else if(operation == Operation.MAX) {
            return decimalFormatter.format(data.stream()
                    .map(value -> value.getValue())
                    .mapToDouble(value -> value.doubleValue())
                    .max()
                    .getAsDouble())
                    .toString();
        }
        throw new RuntimeException("Operation "+ operation +" is invalid");
    }

    private List<PaoIdentifier> getDevices(AggregateIntervalReportFilter filter) {
        List<SimpleDevice> devices = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filter.getDevices())) {
            devices.addAll(deviceDao.getYukonDeviceObjectByIds(filter.getDevices()));
        } else {
            DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(filter.getDeviceGroup());
            devices.addAll(deviceGroupService.getDevices(List.of(deviceGroup)));
        }
        return devices.stream()
                .map(device -> device.getPaoIdentifier())
                .collect(Collectors.toList());
    }

    private String format(Instant date, YukonUserContext context) {
        return dateFormattingService.format(date, DateFormatEnum.BOTH, context);
    }

    private class IntervalParser {
        private boolean hasValidInterval = true;
        private Map<Long, Date> intervals = new LinkedHashMap<>();
        private Range<Instant> range;

        IntervalParser(Instant startDate, Instant stopDate, TimeIntervals interval,
                YukonUserContext context) {
            // time of the first interval
            Instant firstInterval = findInterval(startDate, interval, true, context);
            // time of the last interval
            Instant lastInterval= findInterval(stopDate, interval, false, context);
            range = Range.inclusive(firstInterval, lastInterval);
            if (firstInterval.isAfter(lastInterval)) {
                log.info("Interval {} is not valid for date range {}-{}", interval, format(startDate, context),
                        format(stopDate, context));
                hasValidInterval = false;
            }
            Instant first = firstInterval;
            while (first.isBefore(lastInterval) || first.equals(lastInterval)) {
                intervals.put(first.toDate().getTime(), first.toDate());
                first = first.toDateTime().plusSeconds(interval.getSeconds()).toInstant();
            }
            log.info("user selected:[{}-{}] intervals created:[{}]", format(startDate, context), format(stopDate, context),
                    intervals.values().stream().map(time -> format(new Instant(time.getTime()), context))
                            .collect(Collectors.joining(",")));
            
            //15 min intervals
            //user selected:[02/04/2020 01:27:00-02/04/2020 02:27:00] intervals created:[02/04/2020 01:30:00,02/04/2020 01:45:00,02/04/2020 02:00:00,02/04/2020 02:15:00]
            //user selected:[02/04/2020 01:00:00-02/04/2020 02:00:00] intervals created:[02/04/2020 01:15:00,02/04/2020 01:30:00,02/04/2020 01:45:00,02/04/2020 02:00:00]
            //user selected:[02/04/2020 01:05:00-02/04/2020 02:10:00] intervals created:[02/04/2020 01:15:00,02/04/2020 01:30:00,02/04/2020 01:45:00,02/04/2020 02:00:00]
            
            //30 min intervals
            //user selected:[02/04/2020 01:27:00-02/04/2020 02:27:00] intervals created:[02/04/2020 01:30:00,02/04/2020 02:00:00]
            //user selected:[02/04/2020 01:27:00-02/04/2020 01:47:00] intervals created:[02/04/2020 01:30:00]
            //user selected:[02/04/2020 01:15:00-02/04/2020 01:30:00] intervals created:[02/04/2020 01:30:00]
            //user selected:[02/04/2020 01:15:00-02/04/2020 01:20:00] intervals created:[]
            //(logged as info -  Interval MINUTES_30 is not valid for date range 02/04/2020 01:15:00-02/04/2020 01:20:00)
            
            //1 hour
            // user selected:[02/04/2020 01:15:00-02/04/2020 05:30:00] intervals created:[02/04/2020 02:00:00,02/04/2020 03:00:00,02/04/2020 04:00:00,02/04/2020 05:00:00]
            // user selected:[02/04/2020 00:00:00-02/05/2020 00:00:00] intervals created:[
            // 02/04/2020 01:00:00,
            // 02/04/2020 02:00:00,
            // 02/04/2020 03:00:00,
            // 02/04/2020 04:00:00,
            // 02/04/2020 05:00:00,
            // 02/04/2020 06:00:00,
            // 02/04/2020 07:00:00,
            // 02/04/2020 08:00:00,
            // 02/04/2020 09:00:00,
            // 02/04/2020 10:00:00,
            // 02/04/2020 11:00:00,
            // 02/04/2020 12:00:00,
            // 02/04/2020 13:00:00,
            // 02/04/2020 14:00:00,
            // 02/04/2020 15:00:00,
            // 02/04/2020 16:00:00,
            // 02/04/2020 17:00:00,
            // 02/04/2020 18:00:00,
            // 02/04/2020 19:00:00,
            // 02/04/2020 20:00:00,
            // 02/04/2020 21:00:00,
            // 02/04/2020 22:00:00,
            // 02/04/2020 23:00:00,
            // 02/05/2020 00:00:00]
            
            //1 day
            //user selected:[02/01/2020 00:00:00-02/05/2020 00:00:00] intervals created:[02/02/2020 00:00:00,02/03/2020 00:00:00,02/04/2020 00:00:00,02/05/2020 00:00:00]
            //user selected:[02/01/2020 00:15:00-02/05/2020 00:45:00] intervals created:[02/02/2020 00:00:00,02/03/2020 00:00:00,02/04/2020 00:00:00,02/05/2020 00:00:00]

        }

        boolean hasValidInterval() {
            return hasValidInterval;
        }

        boolean containsInterval(Date interval) {
            return intervals.containsKey(interval.getTime());
        }

        Set<Date> getIntervals() {
            return intervals.values().stream()
                    .sorted()
                    .collect(Collectors.toSet());
        }
        
        Range<Instant> getRange() {
            return range;
        }

        private Instant findInterval(Instant date, TimeIntervals interval, boolean isFirstInterval, YukonUserContext context) {
            ZoneId zone = context.getTimeZone().toZoneId();
            LocalDateTime time = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(date.getMillis()), zone);
            ChronoUnit unit = intervalDefinition.get(interval);

            if (unit == null) {
                throw new RuntimeException("Interval definition doesn't exist");
            }

            LocalDateTime localTime = null;

            if (unit == ChronoUnit.MINUTES) {
                long minutes = TimeUnit.SECONDS.toMinutes(interval.getSeconds());
                localTime = time.truncatedTo(ChronoUnit.HOURS).plusMinutes(minutes * (time.getMinute() / minutes));
            } else if (unit == ChronoUnit.HOURS) {
                long hours = TimeUnit.SECONDS.toHours(interval.getSeconds());
                localTime = time.truncatedTo(ChronoUnit.DAYS).plusHours(hours * (time.getHour() / hours));
            } else if (unit == ChronoUnit.DAYS) {
                localTime = time.truncatedTo(ChronoUnit.DAYS);
            }

            if (localTime == null) {
                throw new RuntimeException("Unable to calculate interval for " + format(date, context) + " interval:" + interval
                        + " isFirstInterval:" + isFirstInterval);
            }

            if (isFirstInterval) {
                localTime = localTime.plus(interval.getSeconds(), ChronoUnit.SECONDS);
            }
            ZonedDateTime zonedDateTime = localTime.atZone(zone);
            Instant newDate = new Instant(zonedDateTime.toInstant().toEpochMilli());
            return newDate;
        }
    }
}
