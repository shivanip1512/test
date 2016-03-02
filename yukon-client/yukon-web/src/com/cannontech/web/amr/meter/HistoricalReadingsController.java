package com.cannontech.web.amr.meter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.sort.SortableColumn;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/historicalReadings/*")
public class HistoricalReadingsController {
    
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private static String baseKey = "yukon.web.modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings.";
    private Logger log = YukonLogManager.getLogger(HistoricalReadingsController.class);
    
    private static int MAX_ROWS_DISPLAY = 100;
    private static final String PERIOD = "period";
    private static final String ALL = "all";
    private static final String ONE_MONTH = "oneMonth";
    private static final String DISPLAY = "display";
    
    private static final Map<String, OrderBy> sorters = ImmutableMap.of(
            "timestamp", OrderBy.TIMESTAMP, 
            "value", OrderBy.VALUE);

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
        String readings = accessor.getMessage(baseKey + "readings");
        String title = StringUtils.isBlank(attributeMsg) ? readings : attributeMsg + " " + readings;
        
        model.addAttribute("pointId", pointId);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("attribute", attribute);
        model.addAttribute("resultLimit", accessor.getMessage(baseKey + "resultLimit", MAX_ROWS_DISPLAY));
        model.addAttribute(ALL, accessor.getMessage(baseKey + ALL));
        model.addAttribute(ONE_MONTH,  accessor.getMessage(baseKey + ONE_MONTH));
        model.addAttribute("title", title);
        model.addAttribute("allUrl", getDownloadUrl(ALL, pointId));
        model.addAttribute("oneMonthUrl", getDownloadUrl(ONE_MONTH, pointId));
        
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
     
    @RequestMapping("download")
    public String download(String period, int pointId, HttpServletResponse response,
            YukonUserContext context) throws IOException {

        BlockingQueue<PointValueHolder> queue = new ArrayBlockingQueue<PointValueHolder>(100000);

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String[] headerRow = new String[2];
        headerRow[0] = accessor.getMessage(baseKey + "tableHeader.timestamp.linkText");
        headerRow[1] = accessor.getMessage(baseKey + "tableHeader.value.linkText");

        String fileName = "HistoryReadings.csv";
        queueLimitedPointData(period, context, Order.REVERSE, OrderBy.TIMESTAMP, pointId, queue);
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
                CSVWriter csvWriter = new CSVWriter(writer);) {
            csvWriter.writeNext(headerRow);

            while (true) {
                PointValueHolder pointValueHolder = queue.take();

                List<String> row = Lists.newArrayList();
                row.add(pointFormattingService.getValueString(pointValueHolder,
                                                              Format.DATE,
                                                              context));
                row.add(pointFormattingService.getValueString(pointValueHolder,
                                                              Format.SHORT,
                                                              context));

                String[] dataRows = new String[row.size()];
                dataRows = (String[]) row.toArray(dataRows);
                csvWriter.writeNext(dataRows);

                if (queue.size() == 0) {
                    break;
                }
            }

        } catch (InterruptedException e) {
            log.error("Error while downloading " + e);
        }
        return null;
    }
    
    private void setupTable(ModelMap model, YukonUserContext context, Order order, OrderBy orderBy, int pointId) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        List<List<String>> points  = getLimitedPointData(DISPLAY, context, order, orderBy, pointId); 
        model.addAttribute("points", points);
        
        String timestampHeader = accessor.getMessage(baseKey + "tableHeader.timestamp.linkText");
        SortableColumn timestamp = SortableColumn.of(order == Order.FORWARD ? Direction.desc : Direction.asc, 
                orderBy == OrderBy.TIMESTAMP, timestampHeader, "timestamp");
        model.addAttribute("timestamp", timestamp);
        
        String valueHeader = accessor.getMessage(baseKey + "tableHeader.value.linkText");
        SortableColumn value = SortableColumn.of(order == Order.FORWARD ? Direction.desc : Direction.asc, 
                orderBy != OrderBy.TIMESTAMP, valueHeader, "value");
        model.addAttribute("value", value);
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
                PaoType paoType = paoDao.getYukonPao(deviceId).getPaoIdentifier().getPaoType();
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
  
    private List<List<String>> getLimitedPointData(String period, 
            final YukonUserContext userContext, 
            Order order, 
            OrderBy orderBy, 
            int pointId) {
        
        List<PointValueHolder> data = null;
        if (period.equals(DISPLAY)) {
            DateTime startDate = new DateTime(userContext.getJodaTimeZone());
            startDate = startDate.minusDays(30);
            DateTime endDate = new DateTime(userContext.getJodaTimeZone());
            Range<Date> dateRange = new Range<Date>(startDate.toDate(), true, endDate.toDate(), false);
            data = rawPointHistoryDao.getLimitedPointData(pointId,
                    dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), 
                    false, 
                    order, 
                    MAX_ROWS_DISPLAY);
        }
        data = sort(data, order, orderBy);
        List<List<String>> points = Lists.transform(data, new Function<PointValueHolder, List<String>>() {
            @Override
            public List<String> apply(PointValueHolder pvh) {
                List<String> row = Lists.newArrayList();
                row.add(pointFormattingService.getValueString(pvh, Format.DATE, userContext));
                row.add(pointFormattingService.getValueString(pvh, Format.SHORT, userContext));
                return row;
            }
        });
        return points;
    }
    
    /**
     * Create a thread to queue point data.
     */
    private void queueLimitedPointData(String period, final YukonUserContext userContext,
            Order order, OrderBy orderBy, int pointId, BlockingQueue<PointValueHolder> queue) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Range<Date> dateRange = null;

        if (period.equals(ONE_MONTH)) {
            DateTime startDate = new DateTime(userContext.getJodaTimeZone());
            startDate = startDate.minusDays(30);
            DateTime endDate = new DateTime(userContext.getJodaTimeZone());
            dateRange = new Range<Date>(startDate.toDate(), true, endDate.toDate(), false);

        } else if (period.equals(ALL)) {
            Instant startDate = new Instant(0);
            dateRange = new Range<Date>(startDate.toDate(), true, null, true);
        }
        executorService.submit(new DataQueuer(queue, pointId, dateRange, order));
    }
    
    /**
     * Thread to execute query for queuing point data.
     */
    private class DataQueuer extends Thread {
        BlockingQueue<PointValueHolder> queue;
        int pointId;
        Range<Date> dateRange;
        Order order;

        DataQueuer(BlockingQueue<PointValueHolder> queue, int pointId, Range<Date> dateRange,
                Order order) {
            this.queue = queue;
            this.pointId = pointId;
            this.dateRange = dateRange;
            this.order = order;
        }

        public void run() {
            rawPointHistoryDao.queuePointData(pointId,
                                              dateRange.translate(CtiUtilities.INSTANT_FROM_DATE),
                                              order,
                                              queue);
        }
    }
    
 
    
    private String getDownloadUrl(String period, int pointId) {
        return "/meter/historicalReadings/download?" + PERIOD + "=" + period + "&pointId=" + pointId;
    }

    private List<PointValueHolder> sort(List<PointValueHolder> data, final Order order, OrderBy orderBy) {
        
        List<PointValueHolder> modifiableList = new ArrayList<PointValueHolder>(data);
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
