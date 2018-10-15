package com.cannontech.web.amr.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
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
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/chart/*")
public class ChartController {
    
    @Autowired private FlotChartService flotChartService;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private AttributeService attributeService;

    @RequestMapping(value="chart", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> chart(YukonUserContext userContext,
                        String pointIds,
                        Integer primaryWeatherLocationId,
                        boolean isTemperatureChecked,
                        ChartInterval interval,
                        long startDate,
                        long endDate,
                        Double yMin,
                        Double yMax,
                        @RequestParam(defaultValue = "LINE") GraphType graphType,
                        @RequestParam(defaultValue = "RAW") ConverterType converterType) {
        List<Integer> ids = Lists.newArrayList(StringUtils.parseIntStringForList(pointIds));
        Integer pointId = ids.get(0);
        LitePoint point = pointDao.getLitePoint(pointId);
        LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String chartIntervalString = messageSourceAccessor.getMessage(interval.getIntervalString());
        String leftYLabelUnits = messageSourceAccessor.getMessage(converterType.getFormattedUnits(unitMeasure, chartIntervalString));
        
        List<GraphDetail> graphDetails = new ArrayList<>();
        graphDetails.add(new GraphDetail(pointId, converterType, leftYLabelUnits, 1, "left"));
        
        if (isTemperatureChecked) {
            LitePoint litePoint = attributeService.findPointForAttribute(
                new PaoIdentifier(primaryWeatherLocationId, PaoType.WEATHER_LOCATION), BuiltInAttribute.TEMPERATURE);
            if (litePoint != null) {
                String rightYLabelUnits = messageSourceAccessor.getMessage("yukon.common.chart.yLabel.temperature");
                graphDetails.add(new GraphDetail(litePoint.getPointID(), ConverterType.RAW, rightYLabelUnits, 2, "right"));
            }
        }
        Instant start = new DateTime(startDate).withTimeAtStartOfDay().toInstant();
        Instant stop = new DateTime(endDate).withTimeAtStartOfDay().plusDays(1).toInstant();
        
        Map<String, Object> graphAsJSON =
                   flotChartService.getMeterGraphData(graphDetails, start, stop, yMin, yMax, graphType, interval, userContext);
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
