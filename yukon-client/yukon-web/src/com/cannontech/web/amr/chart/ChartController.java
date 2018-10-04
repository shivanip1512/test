package com.cannontech.web.amr.chart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.cannontech.web.common.chart.service.impl.GraphDetail;
import com.cannontech.web.input.EnumPropertyEditor;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/chart/*")
public class ChartController {
    
    private Logger log = YukonLogManager.getLogger(ChartController.class);
    
    @Autowired private FlotChartService flotChartService;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private AttributeService attributeService;

    @RequestMapping(value="chart", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> chart(YukonUserContext userContext,
                        String pointIds,
                        ChartInterval interval,
                        long startDate,
                        long endDate,
                        Double yMin,
                        Double yMax,
                        @RequestParam(defaultValue = "LINE") GraphType graphType,
                        @RequestParam(defaultValue = "RAW") ConverterType converterType) {
        Set<Integer> ids = Sets.newHashSet(StringUtils.parseIntStringForList(pointIds));

        LitePoint point = pointDao.getLitePoint(Iterables.get(ids, 0));
        LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String chartIntervalString = messageSourceAccessor.getMessage(interval.getIntervalString());
        String y1LabelUnits = messageSourceAccessor.getMessage(converterType.getFormattedUnits(unitMeasure, chartIntervalString));
        
        Map<Integer, GraphDetail> graphDetailMap = new LinkedHashMap<>();
        graphDetailMap.put(Iterables.get(ids, 0), new GraphDetail(converterType, y1LabelUnits, 1, "left"));
        
        // TODO : Below code may require some code reactor after (YUK-18799) 
        // Get primary weather station and add it to the graph map 
        // This also need some control coming from UI (may be a check box selected for temp).
        boolean isTempratureChecked = false; // Needs to be updated
        if (isTempratureChecked) {
            try {
                int paoId = 0; // This paoId will be replaced with primary weather station Id (YUK-18799)
                LitePoint litePoint = attributeService.getPointForAttribute(
                    new PaoIdentifier(paoId, PaoType.WEATHER_LOCATION), BuiltInAttribute.TEMPERATURE);
                if (litePoint != null) {
                    String y2LabelUnits = messageSourceAccessor.getMessage("yukon.common.chart.yLabel.temperature");
                    graphDetailMap.put(litePoint.getPointID(),
                        new GraphDetail(ConverterType.RAW, y2LabelUnits, 2, "right"));
                }
            } catch (IllegalUseOfAttribute e) {
                log.info("No temprature point found for primary weather station.");
            }
        }
        Instant start = new DateTime(startDate).withTimeAtStartOfDay().toInstant();
        Instant stop = new DateTime(endDate).withTimeAtStartOfDay().plusDays(1).toInstant();
        
        Map<String, Object> graphAsJSON =
                   flotChartService.getMeterGraphData(graphDetailMap, start, stop, yMin, yMax, graphType, interval, userContext);
        return graphAsJSON;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        EnumPropertyEditor<ConverterType> converterTypeEditor = new EnumPropertyEditor<>(ConverterType.class);
        EnumPropertyEditor<ChartInterval> chartIntervalEditor = new EnumPropertyEditor<>(ChartInterval.class);
        EnumPropertyEditor<GraphType> graphTypeEditor = new EnumPropertyEditor<>(GraphType.class);
        binder.registerCustomEditor(ConverterType.class, converterTypeEditor);
        binder.registerCustomEditor(ChartInterval.class, chartIntervalEditor);
        binder.registerCustomEditor(GraphType.class, graphTypeEditor);
    }
}
