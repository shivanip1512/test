package com.cannontech.web.tools.trends;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Controller // @RestController when we get spring 4
@CheckRole(YukonRole.TRENDING)
public class TrendDataController {
    
    @Autowired private GraphDao graphDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
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
        
        boolean showRightAxis = false;
        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        List<GraphDataSeries> series = graphDao.getGraphDataSeries(trend.getGraphDefinitionID());
        List<Map<String, Object>> seriesData = new ArrayList<>();
        
        for (GraphDataSeries serie : series) {
            
            Map<String, Object> valueMap = new HashMap<>();
            valueMap.put("name", serie.getLabel());
            List<Object[]> values = new ArrayList<>();
            
            Instant end = Instant.now();
            Instant start = end.minus(Duration.standardDays(365 * 2));
            Range<Instant> instantRange = new Range<>(start, true, end, true);
            List<PointValueHolder> data = rphDao.getPointData(serie.getPointID(), instantRange, Order.FORWARD);
            
            /** If this is a status point we need to turn data grouping off. */
            if (!data.isEmpty()) {
                PointType pointType = PointType.getForId(data.get(0).getType());
                if (pointType.isStatus()) {
                    valueMap.put("dataGrouping", ImmutableMap.of("enabled", false));
                }
            }
            
            for (PointValueHolder pvh : data) {
                Object[] value = new Object[] {pvh.getPointDataTimeStamp().getTime(), pvh.getValue()};
                values.add(value);
            }
            
            valueMap.put("data", values);
            if (serie.isRight()) {
                valueMap.put("yAxis", 1);
                showRightAxis = true;
            } else {
                valueMap.put("yAxis", 0);
            }
            LegacySeriesType type = LegacySeriesType.getForId(serie.getRenderer());
            if (type.isBar()) {
                valueMap.put("type", "column");
            } else if (type.isStep()) {
                valueMap.put("step", true);
            }
            
            seriesData.add(valueMap);
        }
        
        Map<String, Object> json = new HashMap<>();
        json.put("name", trend.getName());
        json.put("series", seriesData);
        
        List<Map<String, Object>> yAxis = new ArrayList<>();
        Map<String, Object> primaryAxis = new HashMap<>();
        ImmutableMap<String, ImmutableMap<String, String>> labels = ImmutableMap.of("style", ImmutableMap.of("color", "#555"));
        primaryAxis.put("labels", labels);
        yAxis.add(primaryAxis);
        
        if (showRightAxis) {
            addRightAxis(userContext, seriesData, yAxis, labels);
        }
        json.put("yAxis", yAxis);
        
        return json;
    }
    
    /** Add a secondary Y axis to the right side of the graph */
    private void addRightAxis(YukonUserContext userContext, List<Map<String, Object>> seriesData, 
            List<Map<String, Object>> yAxis, 
            ImmutableMap<String, ImmutableMap<String, String>> labels) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String left = accessor.getMessage("yukon.web.modules.tools.trend.axis", accessor.getMessage("yukon.common.left"));
        String right = accessor.getMessage("yukon.web.modules.tools.trend.axis", accessor.getMessage("yukon.common.right"));
        
        Map<String, Object> secondaryAxis = new HashMap<>();
        secondaryAxis.put("labels", labels);
        secondaryAxis.put("opposite", true);
        yAxis.add(secondaryAxis);
        
        for (Map<String, Object> serieData : seriesData) {
            boolean isRight = (Integer)serieData.get("yAxis") == 1;
            serieData.put("name", serieData.get("name") + " - " + (isRight ? right : left));
        }
    }
    
}