package com.cannontech.web.support.systemPerformanceMetrics;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.support.systemPerformanceMetrics.service.SystemPerformanceMetricsService;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointDataType;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/systemPerformanceMetrics/*")
public class SystemPerformanceMetricsController {

    @Autowired private SystemPerformanceMetricsService systemPerformanceMetricsService;

    @RequestMapping("/view")
    public String home(ModelMap model, @RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate, YukonUserContext userContext) {

        // If start date is not provided, start date will be current date minus two months
        if (startDate == null) {
            startDate = new DateTime().withTimeAtStartOfDay().minus(Months.TWO).toDate();
        }
        if (endDate == null) {
            endDate = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "systemPerformanceMetrics.jsp";
    }

    @GetMapping("getChartJson")
    public @ResponseBody Map<Integer, Object> getChartJson(@RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate, YukonUserContext userContext) {

        // If start date is not provided, start date will be current date minus two months
        if (startDate == null) {
            startDate = new DateTime().withTimeAtStartOfDay().minus(Months.TWO).toDate();
        }
        if (endDate == null) {
            endDate = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
        }

        List<LitePoint> systemPoints = systemPerformanceMetricsService.getAllSystemPoints();
        
        Map<Integer, Object> graphAsJSON = Maps.newHashMap();
        for (LitePoint systemPoint : systemPoints) {
            Map<String, Object> graphJSON = Maps.newHashMap();
            graphJSON.put("pointId", systemPoint.getPointID());
            graphJSON.put("pointName", systemPoint.getPointName());
            ChartInterval chartIntervel = YukonMetricPointDataType.getChartInterval(systemPoint.getPointOffset(),
                    PointType.getForId(systemPoint.getPointType()));
            ConverterType converterType = YukonMetricPointDataType.getConverterType(systemPoint.getPointOffset(),
                    PointType.getForId(systemPoint.getPointType()));

            List<ChartValue<Double>> pointData = systemPerformanceMetricsService.getPointData(systemPoint.getPointID(), startDate,
                    endDate, userContext, chartIntervel, converterType);
            graphJSON.put("pointData", pointData);
            graphAsJSON.put(systemPoint.getLiteID(), graphJSON);
        }
        return graphAsJSON;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            @Override
            public void setAsText(String attr) throws IllegalArgumentException {
                Date date = null;
                try {
                    date = format.parse(attr);
                } catch (ParseException e) {
                }
                setValue(date);
            }

            @Override
            public String getAsText() {
                Date date = (Date) getValue();
                if (date == null) {
                    return null;
                }
                return format.format(date);
            }
        });
    }
}
