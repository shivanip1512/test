package com.cannontech.web.widget;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.collect.Multimap;

@Controller
@RequestMapping("/systemMessagingWidget")
public class SystemMessagingWidget extends AdvancedWidgetControllerBase {

    @Autowired private SystemHealthService systemHealthService;
    
    @RequestMapping("render")
    public String render(ModelMap model, LiteYukonUser user) throws Exception {
        
        List<SystemHealthMetricIdentifier> favoriteIds = systemHealthService.getFavorites(user);
        Multimap<SystemHealthMetricType, SystemHealthMetric> metrics = systemHealthService.getMetricsByIdentifiers(favoriteIds);
        
        if (metrics.size() > 0) {
            Collection<SystemHealthMetric> extendedQueueData = metrics.get(SystemHealthMetricType.JMS_QUEUE_EXTENDED);
            model.addAttribute("extendedQueueData", extendedQueueData);
            Collection<SystemHealthMetric> queueData = metrics.get(SystemHealthMetricType.JMS_QUEUE);
            model.addAttribute("queueData", queueData);
        } else {
            List<SystemHealthMetric> extendedQueueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE_EXTENDED);
            model.addAttribute("extendedQueueData", extendedQueueData);
            
            List<SystemHealthMetric> queueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE);
            model.addAttribute("queueData", queueData);
        }
        return "systemMessagingWidget/render.jsp";
    }

}
