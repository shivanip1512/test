package com.cannontech.web.tools.trends;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.graph.GDSTypes;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.trends.data.GraphDataState;
import com.cannontech.web.tools.trends.data.GraphType;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.cannontech.common.gui.util.Colors;

@Controller // @RestController when we get spring 4
@CheckRole(YukonRole.TRENDING)
public class TrendDataController {
    
    @Autowired private GraphDao graphDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DurationFormattingService durationFormatting;
    
    private static final Logger log = YukonLogManager.getLogger(TrendDataController.class);
    
    public enum LegacySeriesType {
        
        LINE(0), // Line
        LINE_SHAPES(1), // Line/Shapes
        LINE_AREA(2), // Line/Area
        LINE_AREA_SHAPES(3), // Line/Area/Shapes
        STEP(4), // Step
        STEP_SHAPES(5), // Step/Shapes
        STEP_AREA(6), // Step/Area
        STEP_AREA_SHAPES(7), // Step/Area/Shapes
        BAR(8), // Bar
        BAR_3D(9); // 3D Bar
        
        private final static Set<LegacySeriesType> lines = ImmutableSet.of(LINE, LINE_AREA, LINE_AREA_SHAPES, LINE_SHAPES);
        
        private final static Set<LegacySeriesType> steps = ImmutableSet.of(STEP, STEP_AREA, STEP_AREA_SHAPES, STEP_SHAPES);
        
        private final static Set<LegacySeriesType> bars = ImmutableSet.of(BAR, BAR_3D);
        
        private final static BiMap<Integer, LegacySeriesType> idMap;
        
        private final static Builder<Integer, LegacySeriesType> b = new ImmutableBiMap.Builder<>();
        
        static {
            for (LegacySeriesType type : values()) b.put(type.id, type);
            idMap = b.build();
        }
        
        private int id;
        
        private LegacySeriesType(int id) {
            this.id = id;
        }
        
        public int getId() {
            return id;
        }
        
        public boolean isLine() {
            return lines.contains(this);
        }
        
        public boolean isStep() {
            return steps.contains(this);
        }
        
        public boolean isBar() {
            return bars.contains(this);
        }
        
        public static LegacySeriesType getForId(int id) {
            return idMap.get(id);
        }
    }
    /*
     * trend provides the headless (JSON) data transform to display chart data
     * */
    @RequestMapping("/trends/{id}/data")
    public @ResponseBody Map<String, Object> trend(YukonUserContext userContext, @PathVariable int id) {
        /*Local Variables
         * 
         * */
        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        
        List<GraphDataSeries> graphDataSeriesList = graphDao.getGraphDataSeries(trend.getGraphDefinitionID());
        
        List<GraphDataSeries> dateGraphDataSeriesList = new ArrayList<>();
        
        List<Map<String, Object>> seriesList = new ArrayList<>();
        
        Duration dbTime = new Duration(0);
        
        List<Map<String, Object>> plotLines  = new ArrayList<>();
        
        Map<String, Object> yAxisProperties = new HashMap<>();
        
        Map<String, Object> json = new HashMap<>();
        
        DateTime chartDatePrime = new DateTime();
        
        DateTime chartDateLimit = new DateTime();
        
        boolean hasCurrentDateBoundery = false;
        
        boolean showRightAxis = false;
        
        /*Series loop*/
        for  (GraphDataSeries seriesItem : graphDataSeriesList) {
            
            log.debug("Graph Type:" + seriesItem.getType() + ":" + graphTypeLabel(seriesItem.getType(), userContext));
            if(seriesItem.getType().equals(GDSTypes.DATE_GRAPH_TYPE) || seriesItem.getType().equals(GDSTypes.DATE_TYPE) || seriesItem.getType().equals(GDSTypes.PEAK_GRAPH_TYPE) || seriesItem.getType().equals(GDSTypes.PEAK_TYPE))
            {
                dateGraphDataSeriesList.add(seriesItem);
                continue;
            }
            
            Map<String, Object> seriesProperties = new HashMap<>();

            List<PointValueHolder> seriesItemResult = new ArrayList<>();  
            
            List<Object[]> data = new ArrayList<>(); 
            
            switch (seriesItem.getType()){
            
            case GDSTypes.USAGE_GRAPH_TYPE:
                //TODO:Figure out where to place the TS benchmark for the process of trend
                seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
                //dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
                data = usageGraphDataProvider(seriesItemResult);
            break;
            case GDSTypes.YESTERDAY_GRAPH_TYPE:
            case GDSTypes.YESTERDAY_TYPE:
                seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
                //dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
                data = yesterdayGraphDataProvider(seriesItemResult);
            break;
            case GDSTypes.GRAPH_TYPE:
            case GDSTypes.BASIC_GRAPH_TYPE:
                seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
                //dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
                data = graphDataProvider(seriesItemResult);
            break;
            case GDSTypes.MARKER_TYPE:
                seriesProperties.put("threshold-value", seriesItem.getMultiplier());
                Map<String, Object> plotLineProperties = new HashMap<>();
                plotLineProperties.put("color", colorPaletteToWeb(seriesItem.getColor()));
                plotLineProperties.put("width", 2);
                plotLineProperties.put("value", seriesItem.getMultiplier());
                plotLines.add(plotLineProperties);
                yAxisProperties.put("plotLines", plotLines);
            break;
            }
                if(data.size()<=1) {
                    seriesProperties.put("error", graphDataStateMessage(GraphDataState.NO_TREND_DATA_AVAILABLE, userContext));    
                }
                seriesProperties.put("data", data);

            if (seriesItem.isRight()) {
                seriesProperties.put("yAxis", 1);
                showRightAxis = true;
            }
            else {
                seriesProperties.put("yAxis", 0);
            }
            LegacySeriesType type = LegacySeriesType.getForId(seriesItem.getRenderer());
            if (type.isBar()) {
                seriesProperties.put("type", "column");
            } else if (type.isStep()) {
                seriesProperties.put("step", true);
            }
            /** If this is a status point we need to turn data grouping off.
             * edge case!!
             * */
            if (!seriesItemResult.isEmpty()) {
                if (PointType.getForId(seriesItemResult.get(0).getType()).isStatus()) {
                    seriesProperties.put("dataGrouping", ImmutableMap.of("enabled", false));
                }
            }
            
            /*Chart timestamp comparison block
             * Originally used for the bounderies of the Date Graph it provides the upper and lower 
             * bounderies of the timestamp to make sure it takes the largest and smalles bounds. 
             * */
            if (seriesItemResult.size() >= 1) {
                Date compareDTPrime = seriesItemResult.get(0).getPointDataTimeStamp();
                Date compareDTLimit = seriesItemResult.get(data.size() -1).getPointDataTimeStamp();
                if (hasCurrentDateBoundery) {
                    
                    DateTime valueDeltaPrimeDT = new DateTime(compareDTPrime);
                    DateTime valueDeltaLimitDT = new DateTime(compareDTLimit);
                    
                    chartDatePrime = (chartDatePrime.isAfter(valueDeltaLimitDT)) 
                    ? valueDeltaPrimeDT : chartDatePrime;
                    chartDateLimit = (chartDateLimit.isBefore(valueDeltaLimitDT)) 
                    ? valueDeltaLimitDT : chartDateLimit; 
                    
                }
                else {
                    chartDatePrime = new DateTime(compareDTPrime);
                    chartDateLimit = new DateTime(compareDTLimit);
                    hasCurrentDateBoundery = true;
                }    
            }
            
            seriesProperties.put("name", seriesItem.getLabel() + graphTypeLabel(seriesItem.getType(), userContext));
            seriesProperties.put("color", colorPaletteToWeb(seriesItem.getColor()));
            seriesList.add(seriesProperties);
        }
        
        /* We don't process any date graphs series until dead last. 
         * This ensures the chart display won't be busted or distorted.
         * 
         * */
        for (GraphDataSeries seriesItem : dateGraphDataSeriesList) {
                        
            Map<String, Object> seriesProperties = new HashMap<>();
            
            List<PointValueHolder> seriesItemResult = new ArrayList<>();
            
            List<Object[]> data = new ArrayList<>();
            Date specificDate;
            seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
            //dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
           
            if (seriesItem.getType().equals(GDSTypes.PEAK_GRAPH_TYPE) || seriesItem.getType().equals(GDSTypes.PEAK_GRAPH_TYPE)) {
                long ts = Long.valueOf(seriesItem.getMoreData()).longValue();
                specificDate = new Date(ts);
                data = dateGraphDataProvider(seriesItemResult, specificDate,  chartDatePrime, chartDateLimit);
            }
            else {
                specificDate = seriesItem.getSpecificDate();
                data = dateGraphDataProvider(seriesItemResult, specificDate,  chartDatePrime, chartDateLimit);    
            }
                if (data.size()< 1) {
                    seriesProperties.put("error", graphDataStateMessage(GraphDataState.NO_TREND_DATA_AVAILABLE, userContext));
                }
                seriesProperties.put("data", data);
                
                if (seriesItem.isRight()) {
                    seriesProperties.put("yAxis", 1);
                    showRightAxis = true;
                }
                else {
                    seriesProperties.put("yAxis", 0);
                }
                if (!seriesItemResult.isEmpty()) {
                    if (PointType.getForId(seriesItemResult.get(0).getType()).isStatus()) {
                        seriesProperties.put("dataGrouping", ImmutableMap.of("enabled", false));
                    }
                }
                DateFormat df = new SimpleDateFormat(" [MM/dd/yyyy] ");
                String reportDate = df.format(specificDate);
                seriesProperties.put("name", seriesItem.getLabel() + graphTypeLabel(seriesItem.getType(), userContext) + reportDate);
                seriesProperties.put("color", colorPaletteToWeb(seriesItem.getColor()));
                seriesList.add(seriesProperties);    
        }
        /*log transaction*/
        //dbTime  = logRPHTrend(trend, dbTime, durationFormatting, userContext);
        
        json.put("name", trend.getName());
        json.put("series", seriesList);
        
        List<Map<String, Object>> yAxis = new ArrayList<>();
        ImmutableMap<String, ImmutableMap<String, String>> labels = ImmutableMap.of("style", ImmutableMap.of("color", "#555"));
        yAxisProperties.put("labels", labels);
        yAxis.add(yAxisProperties);
        if (showRightAxis) {
            addRightAxis(userContext, seriesList, yAxis, labels);
        }
        json.put("yAxis", yAxis);
        return json;
    }
    
    private  List<PointValueHolder> rawPointHistoryDataProvider(int pointId) {
        Instant end = Instant.now(); 
        Instant start = end.minus(Duration.standardDays(365 * 2));
        Range<Instant> instantRange = new Range<>(start, true, end, true);
        return rphDao.getPointData( pointId, instantRange, Order.FORWARD);
    } 
   
    private static Duration logRPHPoint(int pointId, Duration duration ,DurationFormattingService durationFormatting, YukonUserContext userContext) {
        Instant before = Instant.now();
        Instant after = Instant.now();
        Duration dbHit = new Duration(before, after);
        log.debug("RPH retrieval for point: " + pointId + " took " + 
                durationFormatting.formatDuration(dbHit, DurationFormat.DHMS_SHORT_REDUCED, userContext));
        return duration.plus(dbHit);
    }
    
    private static Duration logRPHTrend(LiteGraphDefinition trend, Duration duration ,DurationFormattingService durationFormatting, YukonUserContext userContext) {
        Instant before = Instant.now();
        Instant after = Instant.now();
        Duration dbHit = new Duration(before, after);
        log.debug("RPH retrieval for trend: " + trend + " took " + 
                durationFormatting.formatDuration(dbHit, DurationFormat.DHMS_SHORT_REDUCED, userContext));
        return duration.plus(dbHit);
    }
    
    private List<Object[]> yesterdayGraphDataProvider(List<PointValueHolder> data) {
        log.debug("YesterdayDataProvider Called"); 
        List<Object[]> values = new ArrayList<>();
        for (PointValueHolder pvh : data) {
            Object[] value;
            DateTime timestamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
            timestamp = timestamp.minusDays(1);
            value  = new Object[] {timestamp.getMillis(), pvh.getValue()};
            values.add(value);
        }
        return values;
    }
    
    private List<Object[]> usageGraphDataProvider(List<PointValueHolder> data) {
        log.debug("UsagegraphDataProvider Called");
        /*datePrime is 2 years prior to now*/
        List<Object[]> values = new ArrayList<>();
        
        DateTime dateNow = new DateTime();
        
        DateTime datePrime = dateNow.minusYears(2);
        
        double currentPoint = 0;
        
        double previousPoint = 0;
        
        for (PointValueHolder pvh : data)
        {
            DateTime item_ts = new DateTime(pvh.getPointDataTimeStamp().getTime());
            int flagStart = datePrime.compareTo(item_ts);
            int flagNow = dateNow.compareTo(item_ts);
            if (flagStart <= 0 && flagNow >= 0) {
                currentPoint = pvh.getValue();
                double mPoint = currentPoint - previousPoint;
                previousPoint = currentPoint;
                Object[] value;
                value = new Object[] {item_ts.getMillis(), mPoint};    
                values.add(value);
            }
            else {
                continue;
            }
            
        }
        log.debug("dateGraphDataProvider:Amount Returned:" + values.size());
        return values;
    }
    
    /*TODO: Determine actual functionality*/
    
    private List<Object[]> dateGraphDataProvider(List<PointValueHolder> data, Date date, DateTime compareDatePrime, DateTime compareDateLimit) {
       log.debug("dateGraphDataProvider Called");
       List<Object[]> values = new ArrayList<>();
       if(data.size()> 1) {
           DateTime dateTime = new DateTime(date);
           
           DateTime datePrime = new DateTime(data.get(0).getPointDataTimeStamp());
           
           DateTime dateLimit = new DateTime(data.get(data.size() -1).getPointDataTimeStamp());

           datePrime = (datePrime.isAfter(compareDatePrime)) 
                   ? compareDatePrime : datePrime;
           dateLimit = (dateLimit.isBefore(compareDateLimit)) 
                           ? compareDateLimit : dateLimit; 
           int days = Days.daysBetween(datePrime, dateLimit).getDays();
           
           List<PointValueHolder> rangeList = new ArrayList<>();
           
           for (PointValueHolder pvh : data) {
               DateTime item_ts = new DateTime(pvh.getPointDataTimeStamp().getTime());
               if (dateTime.compareTo(item_ts) <= 0 && dateTime.plusDays(1).compareTo(item_ts) >= 0) {
                   int year = datePrime.getYear();
                   int monthOfYear =datePrime.getMonthOfYear();
                   int dayOfMonth = datePrime.getDayOfMonth();
                   item_ts = item_ts.withDate(year, monthOfYear, dayOfMonth);
                   SimplePointValue update_pvh = new SimplePointValue( pvh.getId(), item_ts.toDate(), pvh.getType(), pvh.getValue());
                   PointValueHolder set = update_pvh;
                   rangeList.add(set);
               }
           }
           int daysCtr = 0;
           while (daysCtr <= days) {
               for (PointValueHolder pvh:rangeList) {
                   DateTime item_ts = new DateTime(pvh.getPointDataTimeStamp().getTime());
                   item_ts = item_ts.plusDays(daysCtr);
                   double item_point = pvh.getValue();
                   Object[] value = new Object[]{item_ts.getMillis(), item_point};
                   values.add(value);
               }
               daysCtr++;
           }

       }
              return values;
    }
    
    private List<Object[]> graphDataProvider(List<PointValueHolder> data){
        log.debug("graphDataProvider Called");
        List<Object[]> values = new ArrayList<>();
        for (PointValueHolder pvh : data) {
            Object[] value;
            value = new Object[] {pvh.getPointDataTimeStamp().getTime(), pvh.getValue()};    
            values.add(value);
        }
        return values;
    }

    private String graphDataStateMessage(GraphDataState state, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        return accessor.getMessage(state);
    }

    private String graphTypeLabel(int graphType, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        GraphType graph = GraphType.getByType(graphType);
        return accessor.getMessage(graph);
    }

    private String colorPaletteToWeb(int color){
        return Colors.colorPaletteToWeb(color);
    }
    /** Add a secondary Y axis to the right side of the graph */
    private void addRightAxis(YukonUserContext userContext, List<Map<String, Object>> seriesList, 
            List<Map<String, Object>> yAxis, 
            ImmutableMap<String, ImmutableMap<String, String>> labels) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String left = accessor.getMessage("yukon.web.modules.tools.trend.axis", accessor.getMessage("yukon.common.left"));
        String right = accessor.getMessage("yukon.web.modules.tools.trend.axis", accessor.getMessage("yukon.common.right"));
        
        Map<String, Object> secondaryAxis = new HashMap<>();
        secondaryAxis.put("labels", labels);
        secondaryAxis.put("opposite", true);
        yAxis.add(secondaryAxis);
        
        for (Map<String, Object> serieData : seriesList) {
            boolean isRight = (Integer)serieData.get("yAxis") == 1;
            serieData.put("name", serieData.get("name") + " - " + (isRight ? right : left));
        }
    }
    
}