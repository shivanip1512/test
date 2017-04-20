package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;
import com.cannontech.web.widget.support.WidgetControllerBase;

@Controller
@RequestMapping("/systemHealthWidget")
public class SystemHealthWidget extends WidgetControllerBase {

    @Autowired private SystemHealthService systemHealthService;
    
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("systemHealthWidget/render.jsp");

        List<SystemHealthMetric> extendedQueueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE_EXTENDED);
        mav.addObject("extendedQueueData", extendedQueueData);
        
        List<SystemHealthMetric> queueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE);
        mav.addObject("queueData", queueData);
        return mav;
    }

}
