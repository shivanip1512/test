package com.cannontech.web.widget;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

@Controller
@RequestMapping("/systemMessagingWidget")
public class SystemMessagingWidget extends AdvancedWidgetControllerBase {

    @Autowired private SystemHealthService systemHealthService;
    
    private static final Map<String, SystemHealthMetricIdentifier> widgetParameterToMetric = 
        new ImmutableMap.Builder<String, SystemHealthMetricIdentifier>()
            .put("showRfnMeter", SystemHealthMetricIdentifier.RFN_METER)
            .put("showRfnLcr", SystemHealthMetricIdentifier.RFN_LCR)
            .put("showRfGatewayArchive", SystemHealthMetricIdentifier.RF_GATEWAY_ARCHIVE)
            .put("showRfDa", SystemHealthMetricIdentifier.RF_DA)
            .put("showRfGatewayDataRequest", SystemHealthMetricIdentifier.RF_GATEWAY_DATA_REQUEST)
            .put("showRfGatewayData", SystemHealthMetricIdentifier.RF_GATEWAY_DATA)
            .build();
    
    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) throws Exception {
        List<SystemHealthMetricIdentifier> favoriteIds = widgetParameterToMetric.entrySet()
            .stream()
            .filter(entry -> WidgetParameterHelper.getBooleanParameter(request, entry.getKey(), false))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        Multimap<SystemHealthMetricType, SystemHealthMetric> metrics = systemHealthService.getMetricsByIdentifiers(favoriteIds);
        
        Collection<SystemHealthMetric> extendedQueueData = metrics.get(SystemHealthMetricType.JMS_QUEUE_EXTENDED);
        model.addAttribute("extendedQueueData", extendedQueueData);
        Collection<SystemHealthMetric> queueData = metrics.get(SystemHealthMetricType.JMS_QUEUE);
        model.addAttribute("queueData", queueData);
        
        return "systemMessagingWidget/render.jsp";
    }

}
