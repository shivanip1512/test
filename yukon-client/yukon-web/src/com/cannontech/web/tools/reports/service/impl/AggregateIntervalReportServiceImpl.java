package com.cannontech.web.tools.reports.service.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.IntervalParser;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService;
import com.google.common.collect.ListMultimap;

public class AggregateIntervalReportServiceImpl implements AggregateIntervalReportService {

    private static final Logger log = YukonLogManager.getLogger(AggregateIntervalReportServiceImpl.class);
            
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    
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
                dateFormattingService, context, log);
        if(!intervalParser.hasValidInterval()) {
            return new ArrayList<>();
        }
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                rawPointHistoryDao.getAttributeData(devices,
                                                    filter.getAttribute(),
                                                    intervalParser.getRange(), //range is adjusted to first interval (inclusive) last interval (inclusive)
                                                    null,
                                                    false, //excludes disabled devices
                                                    Order.FORWARD,
                                                    null,
                                                    null);
        
        log.info("Got report data date range:{}-{} devices:{} interval:{} data rows:{}", format(filter.getStartDate(), context),
                format(filter.getEndDate(), context), devices.size(), filter.getInterval(), attributeData.size());
        
        //group by interval
        Map<Date, List<PointValueQualityHolder>> intervalData =  attributeData.values().stream()
                .filter(value -> intervalParser.containsInterval(value.getPointDataTimeStamp()))
                .collect(Collectors.groupingBy(PointValueQualityHolder::getPointDataTimeStamp));

        List<List<String>> report = getReport(filter, intervalData, intervalParser.getIntervals(), devices, context);
        log.info("Generated report date range:{}-{} devices:{} interval:{} report rows:{}", format(filter.getStartDate(), context),
                format(filter.getEndDate(), context), devices.size(), filter.getInterval(), report.size());
        return report;
    }
     
    /**
     * Creates report for user selected values
     * @param filter - user selections
     * @param intervalData - data from RPH
     * @param intervals - list of intervals
     * @param devices
     * @param context
     * @return list data to write to CSV
     */
    private List<List<String>> getReport(AggregateIntervalReportFilter filter,
            Map<Date, List<PointValueQualityHolder>> intervalData, List<Date> intervals, List<PaoIdentifier> devices,
            YukonUserContext context) {
                
        List<List<String>> report = new ArrayList<>();
        intervals.forEach(interval -> {
            List<PointValueQualityHolder> data = intervalData.get(interval);
            
            boolean isCompleteData = false;
            if (data != null) {
                /*
                 * multiple rows per point
                 * example: point id = 4485645 09/27/2020 23:00:00 value:1 and point id = 4485645 09/27/2020 23:00:00 value:2
                 */

                // keep first row
                data = data.stream().collect(Collectors.groupingBy(p -> p.getId())).values().stream()
                        .map(values -> values.stream().findFirst().get())
                        .collect(Collectors.toList());

                // one row per device
                isCompleteData = data.size() == devices.size();
            }

            String value = getValue(filter, data, isCompleteData);
            if (value != null) {
                report.add(createRow(interval, value, context));
            }
        });
        return report;
    }

    /**
     * Returns value for the report row calculated based on user selections
     */
    private String getValue(AggregateIntervalReportFilter filter, List<PointValueQualityHolder> data, boolean isCompleteData) {
        String value = null;
        if (isCompleteData) {
            value = getValue(data, filter.getOperation());
        } else {
            if (filter.getMissingIntervalData() == MissingIntervalData.PARTIAL) {
                value = CollectionUtils.isEmpty(data) ? "0" : getValue(data, filter.getOperation());
            } else if (filter.getMissingIntervalData() == MissingIntervalData.BLANK) {
                value = "";
            } else if (filter.getMissingIntervalData() == MissingIntervalData.FIXED_VALUE) {
                value = filter.getMissingIntervalDataValue();
            }
        }
        return value;
    }

    /**
     * Returns a report row which consists of date, time and value
     */
    private List<String> createRow(Date interval, String value, YukonUserContext context){
        List<String> row = new ArrayList<>();
        row.add(dateFormattingService.format(interval, DateFormatEnum.DATE, context));
        row.add(dateFormattingService.format(interval, DateFormatEnum.TIME24H, context));
        row.add(value);
        return row;
    }

    /**
     * Returns a value for report row 
     * @param data - data from RPH
     * @param operation - instructions to add the data or find max value
     */
    private String getValue(List<PointValueQualityHolder> data, Operation operation) {
        DecimalFormat decimalFormatter = new DecimalFormat();
        RoundingMode roundingMode = globalSettingDao.getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class)
                .getRoundingMode();
        decimalFormatter.setRoundingMode(roundingMode);
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

    /**
     * Returns list of Pao Identifiers based on the user selection of device group or individual devices
     */
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
        return dateFormattingService.format(date, DateFormatEnum.DATEHM, context);
    }
}
