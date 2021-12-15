package com.cannontech.web.support;

import javax.jms.JMSException;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/systemPerformanceMetrics/*")
public class SystemPerformanceMetricsController {
    
    @RequestMapping("/view")
    public String home(ModelMap model) throws MessageConversionException, JMSException {
        Instant now = Instant.now();
        Instant weekFromNow = new Instant(now.plus(Duration.standardDays(7)).getMillis());
        model.addAttribute("now", now);
        model.addAttribute("weekFromNow", weekFromNow);
        return "systemPerformanceMetrics.jsp";
    }

}
