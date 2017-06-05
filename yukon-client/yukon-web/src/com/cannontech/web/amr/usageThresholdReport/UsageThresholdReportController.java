package com.cannontech.web.amr.usageThresholdReport;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/usageThresholdReport/*")
public class UsageThresholdReportController {

    @RequestMapping(value="report", method = RequestMethod.GET)
    public String report() {
        return "usageThresholdReport/report.jsp";
    }
}