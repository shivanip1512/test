package com.cannontech.web.support.systemPerformanceMetrics;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.support.systemPerformanceMetrics.service.SystemPerformanceMetricsService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/systemPerformanceMetrics/*")
public class SystemPerformanceMetricsController {

    @Autowired private SystemPerformanceMetricsService systemPerformanceMetricsService;

    private Logger log = YukonLogManager.getLogger(SystemPerformanceMetricsController.class);

    @RequestMapping("/view")
    public String home(ModelMap model, @RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate, YukonUserContext userContext) {

        // If start date is not provided, start date will be current date minus two months
        if (startDate == null) {
            DateTime earliestStartDate = new DateTime().withTimeAtStartOfDay().minus(Months.TWO);
            startDate = earliestStartDate.toDate();
        }

        if (endDate == null) {
            endDate = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
        }

       /* List<LitePoint> systemPoints = systemPerformanceMetricsService.getAllSystemPoints();
        //TODO: Remove the below a logic. This is specific to my db since it was returning a different point.
        systemPoints = systemPoints.stream().filter(systemPoint -> systemPoint.getPointID() < 0).collect(Collectors.toList());
        // ============================================================================
        
        model.addAttribute("systemPoints", systemPoints);
        log.info("System Points:");
        systemPoints.forEach(systemPoint -> {
            log.info("Point Id:" + systemPoint.getLiteID() + "  Point Name:" + systemPoint.getPointName());
        });*/

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
//        model.addAttribute("chartJSON", getChartJSON(startDate, endDate, userContext, systemPoints));
        return "systemPerformanceMetrics.jsp";
    }

    @GetMapping("getChartJson")
    public @ResponseBody Map<Integer, Object> getChartJson(@RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate, YukonUserContext userContext) {
        // If start date is not provided, start date will be current date minus two months
        if (startDate == null) {
            DateTime earliestStartDate = new DateTime().withTimeAtStartOfDay().minus(Months.TWO);
            startDate = earliestStartDate.toDate();
        }

        if (endDate == null) {
            endDate = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
        }

        List<LitePoint> systemPoints = systemPerformanceMetricsService.getAllSystemPoints();
        //TODO: Remove the below a logic. This is specific to my db since it was returning a different point.
        systemPoints = systemPoints.stream().filter(systemPoint -> systemPoint.getPointID() < 0).collect(Collectors.toList());
        // ============================================================================
        
        log.info("System Points:");
        systemPoints.forEach(systemPoint -> {
            log.info("Point Id:" + systemPoint.getLiteID() + "  Point Name:" + systemPoint.getPointName());
        });
        Map<Integer, Object> graphAsJSON = getChartJSON(startDate, endDate, userContext, systemPoints);

        return graphAsJSON;
    }

    private Map<Integer, Object> getChartJSON(Date startDate, Date endDate, YukonUserContext userContext, List<LitePoint> systemPoints) {
        Map<Integer, Object> allGraphsJSON = Maps.newHashMap();
        systemPoints.forEach(systemPoint -> {
            Map<String, Object> graphJSON = Maps.newHashMap();
            graphJSON.put("pointId", systemPoint.getPointID());
            graphJSON.put("pointName", systemPoint.getPointName());
            List<ChartValue<Double>> pointData = systemPerformanceMetricsService.getPointData(systemPoint.getPointID(), startDate,
                    endDate,userContext, ChartInterval.DAY, ConverterType.RAW);
            graphJSON.put("pointData", pointData);
            allGraphsJSON.put(systemPoint.getLiteID(), graphJSON);
        });
        return allGraphsJSON;
    }

}
