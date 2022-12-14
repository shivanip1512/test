package com.cannontech.web.amr.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.chart.model.ChartColorsEnum;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.HighChartService;
import com.cannontech.web.common.chart.service.impl.GraphDetail;
import com.cannontech.web.input.EnumPropertyEditor;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/chart/*")
public class ChartController {
    
    @Autowired private HighChartService highChartService;
    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @GetMapping("getChartJson")
    public @ResponseBody Map<String, Object> getChartJson(YukonUserContext userContext,
            String pointIds,
            Integer temperaturePointId,
            boolean isTemperatureChecked,
            ChartInterval interval,
            long startDate,
            long endDate,
            Double yMin,
            Double yMax,
            @RequestParam(defaultValue = "LINE") GraphType graphType,
            @RequestParam(defaultValue = "RAW") ConverterType converterType,
            @RequestParam(required = false) ChartInterval temperatureChartInterval) {

        List<Integer> ids = Lists.newArrayList(StringUtils.parseIntStringForList(pointIds));
        Integer pointId = ids.get(0);
        LitePoint point = pointDao.getLitePoint(pointId);
        UnitOfMeasure unitMeasure = UnitOfMeasure.getForId(point.getUofmID());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String chartIntervalString = messageSourceAccessor.getMessage(interval.getIntervalString());
        String leftYLabelUnits = messageSourceAccessor
                .getMessage(converterType.getFormattedUnits(unitMeasure, chartIntervalString));

        List<GraphDetail> graphDetails = new ArrayList<>();
        GraphDetail graphDetail = new GraphDetail(pointId, leftYLabelUnits, 1, "left", false, ChartColorsEnum.GREEN_FILL, interval, point.getPointName());
        // Set minimum value for Y-axis
        graphDetail.setyMin(yMin);
        graphDetail.setConverterType(converterType);
        graphDetails.add(graphDetail);
        
        if (isTemperatureChecked && temperaturePointId != null) {
            String rightYLabelUnits = messageSourceAccessor.getMessage("yukon.common.chart.yLabel.temperature");
            
            /*
             * These strings for min and max temperature are displayed in the tooltip for temperature point. 
             * We are explicitly setting the text to 'Minimum Temperature' and 'Maximum Temperature' because the point
             * name for both of them in 'Temperature'. Setting explicit text is help user understand is the temperature
             * point is on Min temperature line graph or max temperature line graph.
             * */
            String maxTemperatureTxt = messageSourceAccessor.getMessage("yukon.common.chart.label.maxTemperature");
            String temperatureTxt = org.apache.commons.lang3.StringUtils.EMPTY;

            boolean isMaxTemperatureSet = false;
            if (temperatureChartInterval.getMillis() >= ChartInterval.DAY.getMillis()) {
                // Add graph detail for max temperature trend
                GraphDetail maxTemperatureGraphDetail = new GraphDetail(temperaturePointId, rightYLabelUnits, 2, "right", false,
                        ChartColorsEnum.LIGHT_RED, temperatureChartInterval, maxTemperatureTxt);
                graphDetails.add(maxTemperatureGraphDetail);
                isMaxTemperatureSet = true;
            }
            if (isMaxTemperatureSet) {
                temperatureTxt = messageSourceAccessor.getMessage("yukon.common.chart.label.minTemperature");
            } else {
                temperatureTxt = messageSourceAccessor.getMessage("yukon.common.chart.yLabel.temperature");
            }
            // Add graph detail for min temperature trend
            GraphDetail minTemperatureGraphDetail = new GraphDetail(temperaturePointId, rightYLabelUnits, 2, "right", true,
                    ChartColorsEnum.LIGHT_BLUE, temperatureChartInterval, temperatureTxt);
            graphDetails.add(minTemperatureGraphDetail);

        }

        Instant start = new DateTime(startDate).withTimeAtStartOfDay().toInstant();
        Instant stop = new DateTime(endDate).withTimeAtStartOfDay().plusDays(1).toInstant();

        Map<String, Object> graphAsJSON = highChartService.getMeterGraphData(graphDetails, start, stop, yMin, yMax, graphType,
                userContext);
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
