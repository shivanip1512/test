package com.cannontech.web.tools.trends;

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
import com.cannontech.web.tools.trends.data.PointValueHolderImpl;
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
    
    @RequestMapping("/trends/{id}/data")
    public @ResponseBody Map<String, Object> trend(YukonUserContext userContext, @PathVariable int id) {

        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        
        List<GraphDataSeries> graphDataSeriesList = graphDao.getGraphDataSeries(trend.getGraphDefinitionID());
        
        List<GraphDataSeries> dateGraphDataSeriesList = new ArrayList<>();
        
        List<Map<String, Object>> seriesList = new ArrayList<>();
        
        Duration dbTime = new Duration(0);
        
        List<Map<String, Object>> plotLines  = new ArrayList<>();
        
        Map<String, Object> yAxisProperties = new HashMap<>();
        
        Map<String, Object> json = new HashMap<>();
        
        Date chartYPrime = new Date();
        
        Date chartYLimit = new Date();
        
        boolean hasCurrent = false;
        
        boolean showRightAxis = false;
        
        for  (GraphDataSeries seriesItem : graphDataSeriesList) {
            /*create locals*/
            log.debug("Graph Type:" + seriesItem.getType() + ":" + graphTypeLabel(seriesItem.getType()) );
            if(seriesItem.getType().equals(GDSTypes.DATE_GRAPH_TYPE) || seriesItem.getType().equals(GDSTypes.DATE_TYPE))
            {
                dateGraphDataSeriesList.add(seriesItem);
                continue;
            }
            
            Map<String, Object> seriesProperties = new HashMap<>();

            List<PointValueHolder> seriesItemResult = new ArrayList<>();  
            
            List<Object[]> data = new ArrayList<>(); 
            
            switch (seriesItem.getType())
            {
            case GDSTypes.USAGE_GRAPH_TYPE:
                seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
                dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
                data = graphDataProvider(seriesItemResult);
            break;
            case GDSTypes.YESTERDAY_GRAPH_TYPE:
            case GDSTypes.YESTERDAY_TYPE:
                seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
                dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
                data = yesterdayGraphDataProvider(seriesItemResult);
            break;
            
            case GDSTypes.PEAK_GRAPH_TYPE:
            /*case GDSTypes.PEAK_TYPE:
             * Map<String, Object> markerValues = new HashMap<>();
                values =  graphDataProvider(data);
                seriesProperties.put("lineWidth", 0);
                markerValues.put("enabled", true);
                markerValues.put("radius", 2);
                seriesProperties.put("marker", markerValues);
                if(!json.containsKey("rangeSelector"))
                {
                    Map<String, Object> rangeSelectorValues = new HashMap<>();
                    rangeSelectorValues.put("selected", 2);
                    json.put("rangeSelector", rangeSelectorValues);
                }
            break;*/
            case GDSTypes.GRAPH_TYPE:
            case GDSTypes.BASIC_GRAPH_TYPE:
                seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
                dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
                data = graphDataProvider(seriesItemResult);
            break;
            case GDSTypes.MARKER_TYPE:
                seriesProperties.put("threshold-value", seriesItem.getMultiplier());
                Map<String, Object> plotLineProperties = new HashMap<>();
                plotLineProperties.put("color",colorPaletteToWeb(seriesItem.getColor()));
                plotLineProperties.put("width", 2);
                plotLineProperties.put("value", seriesItem.getMultiplier());
                plotLines.add(plotLineProperties);
                yAxisProperties.put("plotLines",plotLines);
            break;
            }
            seriesProperties.put("data", data);

            if (seriesItem.isRight()) {
                seriesProperties.put("yAxis", 1);
                showRightAxis = true;
            }
            else{
                seriesProperties.put("yAxis",0);
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
            Date compareDTPrime = seriesItemResult.get(0).getPointDataTimeStamp();
            Date compareDTLimit = seriesItemResult.get(data.size() -1).getPointDataTimeStamp();
            if (hasCurrent) {
                DateTime valuePrimeDT = new DateTime(chartYPrime);
                DateTime valueLimitDT = new DateTime(chartYLimit);
                DateTime valueDeltaPrimeDT = new DateTime(compareDTPrime);
                DateTime valueDeltaLimitDT = new DateTime(compareDTLimit);
                
                chartYPrime = (valuePrimeDT.compareTo(valueDeltaLimitDT) > 0 ) 
                ? valueDeltaPrimeDT.toDate() : valuePrimeDT.toDate();
                chartYLimit = (valueLimitDT.compareTo(valueDeltaLimitDT) < 0 ) 
                ? valueDeltaLimitDT.toDate() : valueLimitDT.toDate(); 
                
            }
            else {
                chartYPrime = compareDTPrime;
                chartYLimit = compareDTLimit;
                hasCurrent = true;
            }
            seriesProperties.put("name", seriesItem.getLabel() + graphTypeLabel(seriesItem.getType()));
            seriesProperties.put("color", colorPaletteToWeb(seriesItem.getColor()));
            seriesList.add(seriesProperties);
        }

        for  (GraphDataSeries seriesItem : dateGraphDataSeriesList) {
                        
            Map<String, Object> seriesProperties = new HashMap<>();
            
            List<PointValueHolder> seriesItemResult = new ArrayList<>();
            
            List<Object[]> data = new ArrayList<>();
            
            seriesItemResult = rawPointHistoryDataProvider(seriesItem.getPointID());
            dbTime = logRPHPoint(seriesItem.getPointID(), dbTime, durationFormatting, userContext);
            data = dateGraphDataProvider(seriesItemResult, seriesItem.getSpecificDate(),  chartYPrime, chartYLimit);
            seriesProperties.put("data", data);
            
            if (seriesItem.isRight()) {
                seriesProperties.put("yAxis", 1);
                showRightAxis = true;
            }
            else{
                seriesProperties.put("yAxis",0);
            }
            if (!seriesItemResult.isEmpty()) {
                if (PointType.getForId(seriesItemResult.get(0).getType()).isStatus()) {
                    seriesProperties.put("dataGrouping", ImmutableMap.of("enabled", false));
                }
            }
            seriesProperties.put("name", seriesItem.getLabel() + graphTypeLabel(seriesItem.getType()));
            seriesProperties.put("color", colorPaletteToWeb(seriesItem.getColor()));
            seriesList.add(seriesProperties);
        }
        /*log transaction*/
        dbTime  = logRPHTrend(trend, dbTime, durationFormatting, userContext);
        
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
    
    private List<Object[]> yesterdayGraphDataProvider(List<PointValueHolder> data){
        log.debug("YesterdayDataProvider Called"); 
        List<Object[]> values = new ArrayList<>();
        for (PointValueHolder pvh : data) {
            Object[] value;
            DateTime timestamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
            timestamp = timestamp.minusDays(1);
            value  = new Object[] {timestamp.getMillis() , pvh.getValue()};
            values.add(value);
        }
        return values;
    }
    private List<Object[]> peakGraphDataProvider(List<PointValueHolder> data) {
        log.debug("peakGraphDataProvider Called");
        List<Object[]> values = new ArrayList<>();
        ListIterator<PointValueHolder> litr = data.listIterator();
        Double previous = - 99999999.1;
        Double previousSlope = 0.0;
        for (PointValueHolder p : data) {
            if ( previous == - 99999999.1) { previous = p.getValue(); continue; }
            double slope = p.getValue() - previous;
            if (slope * previousSlope < 0) { //look for sign changes
                DateTime item_ts = new DateTime(p.getPointDataTimeStamp().getTime());
                double item_point = p.getValue();
                Object[] value = new Object[]{item_ts.getMillis(), item_point};
                values.add(value);
            }
                previousSlope = slope;
                previous = p.getValue();
            }
        return values;
    }
    
    private List<Object[]> dateGraphDataProvider(List<PointValueHolder> data, Date date, Date chartYPrime, Date chartYLimit) {
       log.debug("dateGraphDataProvider Called");
       List<Object[]> values = new ArrayList<>();
       DateTime dateTime = new DateTime(date);
       
       DateTime datePrime = new DateTime(data.get(0).getPointDataTimeStamp());
       DateTime dateLimit = new DateTime(data.get(data.size() -1).getPointDataTimeStamp());
       
       DateTime compareDatePrime = new DateTime(chartYPrime);
       DateTime compareDateLimit = new DateTime(chartYLimit);
       
       datePrime = (datePrime.compareTo(compareDatePrime) > 0 ) 
               ? compareDatePrime : datePrime;
       dateLimit = (dateLimit.compareTo(compareDateLimit) < 0) 
                       ? compareDateLimit : dateLimit; 
                       
       long timestamp_start = dateTime.getMillis();
       long timestamp_end = dateTime.plusDays(1).getMillis();
       log.debug("dateGraphDataProvider:timestamp_start:"+ dateTime.toString());
       log.debug("dateGraphDataProvider:timestamp_end:"+ dateLimit.toString());
       int days = Days.daysBetween(datePrime, dateLimit).getDays();
       log.debug("dateGraphDataProvider:number of days:"+ days);
       
       List<PointValueHolder> rangeList = new ArrayList<>();
       
       for (PointValueHolder pvh : data) {
           DateTime item_ts = new DateTime(pvh.getPointDataTimeStamp().getTime());
           long compare_ts = item_ts.getMillis(); 
           if(compare_ts >= timestamp_start && compare_ts <= timestamp_end) {
               PointValueHolderImpl update_pvh = new PointValueHolderImpl();
               int year = datePrime.getYear();
               int monthOfYear =datePrime.getMonthOfYear();
               int dayOfMonth = datePrime.getDayOfMonth();
               item_ts = item_ts.withDate(year, monthOfYear, dayOfMonth);
               update_pvh.setPointDataTimeStamp(item_ts.toDate());
               update_pvh.setPointValue(pvh.getValue());
               PointValueHolder set = update_pvh;
               rangeList.add(set);
           }
       }
       log.debug("dateGraphDataProvider:rangeList Amount:"+ rangeList.size());
       int daysCtr = 0;
       while (daysCtr <= days) {
           for(PointValueHolder pvh:rangeList) {
               DateTime item_ts = new DateTime(pvh.getPointDataTimeStamp().getTime());
               item_ts = item_ts.plusDays(daysCtr);
               double item_point = pvh.getValue();
               Object[] value = new Object[]{item_ts.getMillis(), item_point};
               values.add(value);
           }
           daysCtr++;
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
        log.debug("dateGraphDataProvider:Amount Returned:" + values.size());
        return values;
    }
    
    private String graphTypeLabel(int graphType) {
        String retval = ":" + GDSTypes.BASIC_GRAPH_TYPE_STRING;
        switch(graphType){
        case GDSTypes.DATE_GRAPH_TYPE:
            retval = ":" + GDSTypes.DATE_GRAPH_TYPE_STRING;
        break;
        
        case GDSTypes.PEAK_GRAPH_TYPE:
            retval = ":" + GDSTypes.PEAK_GRAPH_TYPE_STRING;
        break;
        
        case GDSTypes.USAGE_GRAPH_TYPE:
            retval = ":" + GDSTypes.USAGE_GRAPH_TYPE_STRING;
        break;
        
        case GDSTypes.YESTERDAY_GRAPH_TYPE:
            retval = ":" + GDSTypes.YESTERDAY_GRAPH_TYPE_STRING;
        break;
        
        }
        return retval;
    }
    
    private String colorPaletteToWeb(int color){
        String retval = "#FFFFFF";
        switch(color)
        {
        case Colors.BLACK_ID:
            retval = "#000000";
        break;
        case Colors.BLUE_ID:
            retval = "#0008FF";
        break;
        case Colors.CYAN_ID:
            retval = "#00D5FF";
        break;
        case Colors.GRAY_ID:
            retval = "#808080";
        break;
        case Colors.GREEN_ID:
            retval = "#15FF00";
        break;
        case Colors.MAGENTA_ID:
            retval = "#FF00AE";
        break;
        case Colors.ORANGE_ID:
            retval = "#FF9500";
        break;
        case Colors.PINK_ID:
            retval = "#FFB5E8";
        break;
        case Colors.RED_ID:
            retval = "#FF0000";
        break;
       }
        return retval;
        
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