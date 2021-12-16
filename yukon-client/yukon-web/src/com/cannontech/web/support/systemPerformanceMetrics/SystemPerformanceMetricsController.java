package com.cannontech.web.support.systemPerformanceMetrics;

import java.util.Date;

import org.jfree.util.Log;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/systemPerformanceMetrics/*")
public class SystemPerformanceMetricsController {

    @RequestMapping("/view")
    public String home(ModelMap model, @RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate) {

        // If start date is not provided, start date will be current date minus two months
        if (startDate == null) {
            DateTime earliestStartDate = new DateTime().withTimeAtStartOfDay().minus(Months.TWO);
            startDate = earliestStartDate.toDate();
        }

        if (endDate == null) {
            endDate = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
        }

        // TODO: Remove later
        Log.info("Start Date:" + startDate);
        Log.info("End Date:" + endDate);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "systemPerformanceMetrics.jsp";
    }

}
