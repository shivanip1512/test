package com.cannontech.web.amr.meter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.tools.points.PointBackingBean;
import com.cannontech.web.updater.point.CachedPointDataCorrelationService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/historicalReadings/*")
public class HistoricalReadingsController {
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private CachedPointDataCorrelationService cachedPointDataCorrelationService;
    @Autowired private PointService pointService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private GlobalSettingDao globalSettingDao;
    
    private static final String baseKey = "yukon.web.modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings.";
    private Logger log = YukonLogManager.getLogger(HistoricalReadingsController.class);
    
    private static int MAX_ROWS_DISPLAY = 1536;
    
    private final Validator validator = new SimpleValidator<PointBackingBean>(PointBackingBean.class) {
        @Override
        protected void doValidation(PointBackingBean bean, Errors errors) {
            if (bean.getValue() != null) {
                YukonValidationUtils.checkIsValidDouble(errors, "value", bean.getValue());
            }
        }
    };
    
    private static final Map<String, OrderBy> sorters = ImmutableMap.of(
            "timestamp", OrderBy.TIMESTAMP, 
            "value", OrderBy.VALUE);

    private enum Duration implements DisplayableEnum {
        ONE_MONTH,
        THREE_MONTH,
        ONE_YEAR,
        ALL;
        
        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
    
    @RequestMapping("view")
    public String view(ModelMap model, 
            Integer deviceId, 
            String attribute,
            int pointId,
            final YukonUserContext context) {
        
        //default sort
        OrderBy orderBy =  OrderBy.TIMESTAMP;
        Order order = Order.REVERSE;

        setupTable(model, context, order, orderBy, pointId);

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String attributeMsg = getAttributeMessage(deviceId, attribute, pointId, context);
        String attributeMessage = StringUtils.isBlank(attributeMsg) ? StringUtils.EMPTY : attributeMsg;
        String title = accessor.getMessage(baseKey + "readings", attributeMessage);
        
        
        model.addAttribute("pointId", pointId);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("attribute", attribute);
        model.addAttribute("resultLimit", accessor.getMessage(baseKey + "resultLimit", MAX_ROWS_DISPLAY));
        model.addAttribute("maxRowsDisplay", MAX_ROWS_DISPLAY);
        
        Map<Duration, String> duration = new LinkedHashMap<>();
        duration.put(Duration.ONE_MONTH, getDownloadUrl(Duration.ONE_MONTH, pointId));
        duration.put(Duration.THREE_MONTH, getDownloadUrl(Duration.THREE_MONTH, pointId));
        duration.put(Duration.ONE_YEAR, getDownloadUrl(Duration.ONE_YEAR, pointId));
        duration.put(Duration.ALL, getDownloadUrl(Duration.ALL, pointId));
       
        model.addAttribute("duration", duration);
        model.addAttribute("title", title);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        model.addAttribute("showTrend", !litePoint.getPointTypeEnum().isStatus());
        cachedPointDataCorrelationService.correlateAndLog(pointId);
        
        return "historicalReadings/view.jsp";
    }
    
    @RequestMapping("values")
    public String values(ModelMap model, int pointId, SortingParameters sorting, YukonUserContext context) {
        
        OrderBy orderBy =  OrderBy.TIMESTAMP;
        Order order = Order.REVERSE;
        
        if (sorting != null) {
            orderBy = sorters.get(sorting.getSort());
            if (sorting.getDirection() == Direction.desc) {
                order = Order.FORWARD;
            } else {
                order = Order.REVERSE; 
            }
        }
        
        setupTable(model, context, order, orderBy, pointId);
        
        return "historicalReadings/values.jsp";
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_POINT_DATA, level = HierarchyPermissionLevel.OWNER)
    @RequestMapping(value = "delete", method=RequestMethod.POST)
    public String deletePointValue(YukonUserContext userContext, int pointId, double value,
            String timestamp, FlashScope flash, ModelMap model) {
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.BOTH, userContext);
        DateTime dateTime = formatter.parseDateTime(timestamp).withZone(userContext.getJodaTimeZone());
        pointService.deletePointData(pointId, value, dateTime.toInstant(), userContext);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "value.deleteConfirmation", timestamp));
        return values(model, pointId, null, userContext);
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_POINT_DATA, level = HierarchyPermissionLevel.UPDATE)
    @RequestMapping(value = "edit", method=RequestMethod.GET)
    public String editPointValue(YukonUserContext userContext, ModelMap model, int pointId, double value,
            String timestamp) {
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.BOTH, userContext);
        DateTime dateTime = formatter.parseDateTime(timestamp).withZone(userContext.getJodaTimeZone());
        PointBackingBean backingBean = new PointBackingBean();
        backingBean.setPointId(pointId);
        backingBean.setTimestamp(dateTime.toInstant());
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        if (litePoint.getPointTypeEnum() == PointType.Status
            || litePoint.getPointTypeEnum() == PointType.CalcStatus) {
            LiteStateGroup group = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
            model.put("stateList", group.getStatesList());
            backingBean.setStateId((int) value);
        } else {
            backingBean.setValue(value);
        }
        LiteYukonPAObject liteYukonPAO = databaseCache.getAllPaosMap().get(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        model.put("oldValue",  value);
        model.addAttribute("backingBean", backingBean);
        return "historicalReadings/editValue.jsp";
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_POINT_DATA, level = HierarchyPermissionLevel.UPDATE)
    @RequestMapping(value = "edit", method=RequestMethod.POST)
    public String editPointValueSubmit(HttpServletResponse response, YukonUserContext userContext, 
           @ModelAttribute("backingBean") PointBackingBean backingBean, BindingResult bindingResult, 
           Double oldValue, String editTimestamp, ModelMap model, FlashScope flashScope) throws IOException {
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.BOTH, userContext);
        DateTime dateTime = formatter.parseDateTime(editTimestamp).withZone(userContext.getJodaTimeZone());
        backingBean.setTimestamp(dateTime.toInstant());
        double newValue;
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = databaseCache.getAllPaosMap().get(litePoint.getPaobjectID());
        if (litePoint.getPointTypeEnum() == PointType.Status
            || litePoint.getPointTypeEnum() == PointType.CalcStatus) {
            newValue = backingBean.getStateId();
        } else {
            validator.validate(backingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                model.put("deviceName", liteYukonPAO.getPaoName());
                model.put("pointName", litePoint.getPointName());
                model.put("oldValue",  oldValue);
                model.addAttribute("backingBean", backingBean);
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setError(messages);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return "historicalReadings/editValue.jsp";
            }

            newValue = backingBean.getValue();
        }

        pointService.updatePointData(backingBean.getPointId(), oldValue, newValue, backingBean.getTimestamp(),
            userContext);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "value.editConfirmation", editTimestamp));

        return values(model, litePoint.getPointID(), null, userContext);
    }
    
    @RequestMapping("download")
    public String download(Duration duration, int pointId, HttpServletResponse response,
            YukonUserContext context) throws IOException {

        BlockingQueue<PointValueHolder> queue = new ArrayBlockingQueue<>(100000);

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String[] headerRow = new String[6];
        headerRow[0] = accessor.getMessage(baseKey + "tableHeader.devicename.linkText");
        headerRow[1] = accessor.getMessage(baseKey + "tableHeader.pointname.linkText");
        headerRow[2] = accessor.getMessage(baseKey + "tableHeader.timestamp.linkText");
        headerRow[3] = accessor.getMessage(baseKey + "tableHeader.value.linkText");
        headerRow[4] = accessor.getMessage(baseKey + "tableHeader.uom.linkText");
        headerRow[5] = accessor.getMessage(baseKey + "tableHeader.quality.linkText");
        AtomicBoolean isCompleted = new AtomicBoolean(false);
        
        PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointId);
        String deviceName = databaseCache.getAllPaosMap().get(paoPointIdentifier.getPaoIdentifier().getPaoId()).getPaoName();
        String pointName = pointDao.getPointName(pointId);
        
        String fileName = deviceName + "_" + pointName + ".csv";
        queueLimitedPointData(duration,
                              context,
                              Order.REVERSE,
                              OrderBy.TIMESTAMP,
                              pointId,
                              queue,
                              isCompleted);
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
                CSVWriter csvWriter = new CSVWriter(writer);) {
            csvWriter.writeNext(headerRow);

            while (true) {
                if (!isCompleted.compareAndSet(true, false)) {
                    if (!queue.isEmpty()) {
                        PointValueHolder pointValueHolder = queue.take();
                        if (pointValueHolder != null) {
                            List<String> row = Lists.newArrayList();
                            row.add(deviceName);
                            row.add(pointName);
                            row.add(pointFormattingService.getValueString(pointValueHolder,
                                                                          Format.DATE,
                                                                          context));
                            row.add(pointFormattingService.getValueString(pointValueHolder,
                                                                          Format.VALUE,
                                                                          context));
                            row.add(pointFormattingService.getValueString(pointValueHolder,
                                                                          Format.UNIT,
                                                                          context));
                            row.add(pointFormattingService.getValueString(pointValueHolder,
                                                                          Format.QUALITY,
                                                                          context));

                            String[] dataRows = new String[row.size()];
                            dataRows = row.toArray(dataRows);
                            csvWriter.writeNext(dataRows);
                        }
                        if (queue.size() == 0) {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }

        } catch (InterruptedException e) {
            log.error("Error while downloading " + e);
        }
        return null;
    }
    
    @RequestMapping(value = "trend", method = RequestMethod.POST)
    public String trend(YukonUserContext userContext, ModelMap model, @RequestParam int pointId,
            @RequestParam(defaultValue = "ONE_MONTH") Duration duration) {

        LitePoint litePoint = pointDao.getLitePoint(pointId);
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.addAttribute("deviceId", liteYukonPAO.getPaoIdentifier().getPaoId());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("pointId", litePoint.getLiteID());
        int monthsToSubtract = 0;
        StringBuilder durationMessage = new StringBuilder();
        switch (duration) {
        case ONE_MONTH:
            monthsToSubtract = Months.ONE.getMonths();
            durationMessage.append(messageSourceAccessor.getMessage(Duration.ONE_MONTH.getFormatKey()));
            break;
        case THREE_MONTH:
            monthsToSubtract = Months.THREE.getMonths();
            durationMessage.append(messageSourceAccessor.getMessage(Duration.THREE_MONTH.getFormatKey()));
            break;
        case ONE_YEAR:
            monthsToSubtract = Months.TWELVE.getMonths();
            durationMessage.append(messageSourceAccessor.getMessage(Duration.ONE_YEAR.getFormatKey()));
            break;
        case ALL:
            Months months = Months.months(globalSettingDao.getInteger(GlobalSettingType.TRENDS_HISTORICAL_MONTHS));
            monthsToSubtract = months.getMonths();
            durationMessage.append(monthsToSubtract);
            durationMessage.append(" ");
            durationMessage.append(messageSourceAccessor.getMessage(baseKey + "months"));
            break;
        }
        Date endDate = new Date();
        Date startDate = endDate;
        startDate = DateUtils.addMonths(startDate, -monthsToSubtract);
        startDate = DateUtils.truncate(startDate, Calendar.DATE);
        ChartPeriod chartPeriod = ChartPeriod.MONTH;
        ConverterType converterType = ConverterType.RAW;
        if (UnitOfMeasure.getForId(litePoint.getUofmID()) == UnitOfMeasure.KWH) {
            // "Usage" data can be "normalized" delta, since it is an ever increasing number
            if (liteYukonPAO.getPaoType().isRfn()) {
                converterType = ConverterType.DAILY_USAGE;
            } else {
                converterType = ConverterType.NORMALIZED_DELTA;
            }
        } else if (UnitOfMeasure.getForId(litePoint.getUofmID()) == UnitOfMeasure.GALLONS) {
            // water usage can be delta also.
            converterType = ConverterType.DELTA_WATER;
        } else { // everything is raw
            converterType = ConverterType.RAW;
        }
        ChartInterval chartInterval = chartPeriod.getChartUnit(Range.inclusive(startDate, endDate), converterType);
        model.addAttribute("interval", chartInterval);
        model.addAttribute("converterType", converterType);
        model.addAttribute("graphType", GraphType.LINE);
        model.addAttribute("startDateMillis", startDate.getTime());
        model.addAttribute("endDateMillis", endDate.getTime());

        YukonMessageSourceResolvable title =
            new YukonMessageSourceResolvable(baseKey + "trend.description",
                durationMessage.toString(), litePoint.getPointName());
        model.addAttribute("title", messageSourceAccessor.getMessage(title));

        return "historicalReadings/trendPopup.jsp";
    }
    
    private void setupTable(ModelMap model, YukonUserContext context, Order order, OrderBy orderBy, int pointId) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        model.addAttribute("pointId", pointId);
        List<PointValueHolder> points = getLimitedPointData(context, order, orderBy, pointId);
        model.addAttribute("points", points);
        
        String timestampHeader = accessor.getMessage(baseKey + "tableHeader.timestamp.linkText");
        SortableColumn timestamp = SortableColumn.of(order == Order.FORWARD ? Direction.desc : Direction.asc, 
                orderBy == OrderBy.TIMESTAMP, timestampHeader, "timestamp");
        model.addAttribute("timestamp", timestamp);
        
        String valueHeader = accessor.getMessage(baseKey + "tableHeader.value.linkText");
        SortableColumn value = SortableColumn.of(order == Order.FORWARD ? Direction.desc : Direction.asc, 
                orderBy != OrderBy.TIMESTAMP, valueHeader, "value");
        model.addAttribute("value", value);
        //Find the latest timestamp
        if (points.size() > 0) {
            Date maxDate = points.stream().map(PointValueHolder::getPointDataTimeStamp).max(Date::compareTo).get();
            model.addAttribute("maxTimestamp", maxDate);
        }
        
    }
    
    /**
     * Get the i18n'd message for an attribute.
     * @param deviceId - the deviceId for the device used for lookup of the attribute
     * @param pointId - the pointId for the device's point for which the attribute might exist
     * @param attribute - the attribute's default description (if known)
     * @return the i18n'd message for the attribute if it exists, an empty string otherwise.
     */
    private String getAttributeMessage(Integer deviceId, String attribute, int pointId, YukonUserContext context) {
        
        String attributeMsg = "";
        if (attribute != null) {
            attributeMsg = objectFormattingService.formatObjectAsString(BuiltInAttribute.valueOf(attribute), context);
        } else if (deviceId != null) {
            // We have a deviceId and a pointId, we can find the attribute.
            Map<Integer, PointInfo> pointInfoByPointIds = pointDao.getPointInfoByPointIds(Sets.newHashSet(pointId));
            PointInfo pointInfo = pointInfoByPointIds.get(pointId);
            if (pointInfo != null) {
                PaoType paoType = databaseCache.getAllPaosMap().get(deviceId).getPaoIdentifier().getPaoType();
                PointIdentifier pointIdentifier = pointInfo.getPointIdentifier();
                BuiltInAttribute builtInAttribute = 
                        paoDefinitionDao.findOneAttributeForPaoTypeAndPoint(
                                PaoTypePointIdentifier.of(paoType, pointIdentifier));
                if (builtInAttribute != null) {
                    attributeMsg = objectFormattingService.formatObjectAsString(builtInAttribute, context);
                }
            }
        }
        return attributeMsg;
    }
  
    private List<PointValueHolder> getLimitedPointData(final YukonUserContext userContext, Order order,
            OrderBy orderBy, int pointId) {

        DateTime startOfMonth =
            new DateTime(userContext.getJodaTimeZone()).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime(userContext.getJodaTimeZone()).plusDays(1).withTimeAtStartOfDay();
        DateTime startDate = startOfMonth.minusMonths(1);
        Range<Date> dateRange = new Range<>(startDate.toDate(), true, endDate.toDate(), false);

        List<PointValueHolder> data =
            rawPointHistoryDao.getLimitedPointData(pointId, dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), false,
                order, MAX_ROWS_DISPLAY);
              
        return sort(data, order, orderBy);
    }
    
    /**
     * Create a thread to queue point data.
     */
    private void queueLimitedPointData(Duration duration, final YukonUserContext userContext,
            Order order, OrderBy orderBy, int pointId, BlockingQueue<PointValueHolder> queue, AtomicBoolean isCompleted) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Range<Date> dateRange = null;
        DateTime startDate = null;

        DateTime startOfMonth =
            new DateTime(userContext.getJodaTimeZone()).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime(userContext.getJodaTimeZone()).plusDays(1).withTimeAtStartOfDay();

        if (duration == Duration.ONE_MONTH) {
            startDate = startOfMonth.minusMonths(1);
        } else if (duration == Duration.THREE_MONTH) {
            startDate = startOfMonth.minusMonths(3);
        } else if (duration == Duration.ONE_YEAR) {
            startDate = startOfMonth.minusYears(1);
        } else if (duration == Duration.ALL) {
            Instant startDateInstant = new Instant(0);
            dateRange = new Range<>(startDateInstant.toDate(), true, null, true);
        }
        if (dateRange == null) {
            dateRange = new Range<>(startDate.toDate(), true, endDate.toDate(), false);
        }
        executorService.submit(new DataQueuer(queue, pointId, dateRange, order, isCompleted));
    }
    
    /**
     * Thread to execute query for queuing point data.
     */
    private class DataQueuer extends Thread {
        BlockingQueue<PointValueHolder> queue;
        int pointId;
        Range<Date> dateRange;
        Order order;
        AtomicBoolean isCompleted;

        DataQueuer(BlockingQueue<PointValueHolder> queue, int pointId, Range<Date> dateRange,
                Order order, AtomicBoolean isCompleted) {
            this.queue = queue;
            this.pointId = pointId;
            this.dateRange = dateRange;
            this.order = order;
            this.isCompleted = isCompleted;
        }

        @Override
        public void run() {
            rawPointHistoryDao.queuePointData(pointId,
                                              dateRange.translate(CtiUtilities.INSTANT_FROM_DATE),
                                              order,
                                              queue, isCompleted);
        }
    }
    
 
    
    private String getDownloadUrl(Duration duration, int pointId) {
        return "/meter/historicalReadings/download?duration=" + duration + "&pointId=" + pointId;
    }

    private List<PointValueHolder> sort(List<PointValueHolder> data, final Order order, OrderBy orderBy) {
        
        List<PointValueHolder> modifiableList = new ArrayList<>(data);
        if (orderBy == OrderBy.TIMESTAMP) {
            Collections.sort(modifiableList, new Comparator<PointValueHolder>() {
                @Override
                public int compare(PointValueHolder pvh1, PointValueHolder pvh2) {
                    if (order == Order.FORWARD) {
                        return pvh1.getPointDataTimeStamp().compareTo(pvh2.getPointDataTimeStamp());
                    } else {
                        return - pvh1.getPointDataTimeStamp().compareTo(pvh2.getPointDataTimeStamp());
                    }
                }
            });
        } else if (orderBy == OrderBy.VALUE) {
            Collections.sort(modifiableList, new Comparator<PointValueHolder>() {
                @Override
                public int compare(PointValueHolder pvh1, PointValueHolder pvh2) {
                    if (order == Order.FORWARD) {
                        return - new Double(pvh1.getValue()).compareTo(pvh2.getValue());
                    } else {
                        return new Double(pvh1.getValue()).compareTo(pvh2.getValue());
                    }
                }
            });
        }
        return modifiableList;
    }
    
}