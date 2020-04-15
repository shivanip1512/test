package com.cannontech.web.support;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.rfn.service.NmSyncService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.widgets.service.PorterQueueCountsWidgetService;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;

@Controller
@RequestMapping("/systemHealth/*")
public class SystemHealthController {
    private static final Logger log = YukonLogManager.getLogger(SystemHealthController.class);
    private static Instant lastResync;
    
    @Autowired private SystemHealthService systemHealthService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PorterQueueCountsWidgetService porterQueueCountsWidgetService;
    @Autowired private NmSyncService nmSyncService;
    

    @RequestMapping("/home")
    public String home(ModelMap model, LiteYukonUser user) throws MessageConversionException, JMSException {
        
        List<SystemHealthMetric> extendedQueueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE_EXTENDED);
        model.addAttribute("extendedQueueData", extendedQueueData);
        
        List<SystemHealthMetric> queueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE);
        model.addAttribute("queueData", queueData);
        
        Map<Integer, LiteYukonPAObject> portPointIdToPaoMap = porterQueueCountsWidgetService.getPointIdToPaoMapForAllPorts();
        model.addAttribute("portData", getPortJson(portPointIdToPaoMap));
        
        return "systemHealth.jsp";
    }

    @RequestMapping("dataUpdate")
    public @ResponseBody Map<SystemHealthMetricType, List<SystemHealthMetric>> dataUpdate() {
        
        Map<SystemHealthMetricType, List<SystemHealthMetric>> metricTypeToMetrics = new HashMap<>();
        
        for (SystemHealthMetricType metricType : SystemHealthMetricType.values()) {
            List<SystemHealthMetric> metrics = systemHealthService.getMetricsByType(metricType);
            metricTypeToMetrics.put(metricType, metrics);
        }
        
        return metricTypeToMetrics;
    }
    
    @RequestMapping("{metric}/detail")
    public String metricDetail(ModelMap model, @PathVariable SystemHealthMetricIdentifier metric, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        //Put the i18ned status labels into the DOM for JS to use when updating status
        Map<MetricStatus, String> metricStatusLabels = new HashMap<>();
        for(MetricStatus status : MetricStatus.values()) {
            metricStatusLabels.put(status, accessor.getMessage(status));
        }
        model.addAttribute("metricStatusLabels", metricStatusLabels);
        
        String pageTitle = accessor.getMessage(metric);
        model.addAttribute("title", pageTitle);
        
        model.addAttribute("metric", systemHealthService.getMetric(metric));
        model.addAttribute("queue", systemHealthService.getMetric(metric));
        model.addAttribute("pertinentCriteria", systemHealthService.getPertinentCriteria(metric));//systemHealthService.getPertinentCriteria(metric);
        
        return "systemHealthDetail.jsp";
    }
    
    @RequestMapping("sync")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public @ResponseBody Map<String, String> resync(ModelMap model, HttpServletResponse resp, FlashScope flash, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Logger rfnCommsLog = YukonLogManager.getRfnLogger();
        
        Map<String, String> json = new HashMap<>();
        
        // Make sure a sync hasn't been sent in the last 5 minutes 
        if (lastResync == null) {
            lastResync = new Instant();
        } else {
            Instant now = new Instant();
            if (lastResync.isAfter(now.minus(Duration.standardMinutes(5)))) {
                resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                json.put("message", accessor.getMessage("yukon.web.modules.support.systemHealth.sync.throttle"));
                return json;
            }
            lastResync = now;
        }
        
        // Take care of webserver log and RFN comms log
        log.info("User: " + userContext.getYukonUser().getUsername() + " initiated a sync of RF data.");
        if (rfnCommsLog.isDebugEnabled()) {
            rfnCommsLog.debug("Initiated a sync of RF data.");
        }
        
        // Send the sync message
        nmSyncService.sendSyncRequest();
        
        resp.setStatus(HttpStatus.OK.value());
        json.put("message", accessor.getMessage("yukon.web.modules.support.systemHealth.sync.success"));
        return json;
    }
    
    private List<Map<String, Object>> getPortJson(Map<Integer, LiteYukonPAObject> portPointIdToPaoMap) {
        List<Map<String, Object>> portData = new ArrayList<>();
        portPointIdToPaoMap.keySet().forEach(pointId -> {
            Map<String, Object> portMap = new HashMap<>();
            portMap.put("pointId", pointId);
            portMap.put("paoIdentifier", portPointIdToPaoMap.get(pointId).getPaoIdentifier());
            portMap.put("paoName", portPointIdToPaoMap.get(pointId).getPaoName());
            portData.add(portMap);
        });
        return portData;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(SystemHealthMetricIdentifier.class, new PropertyEditorSupport() {
            @Override public void setAsText(final String text) throws IllegalArgumentException {
                setValue(SystemHealthMetricIdentifier.valueOf(text));
            }
        });
    }
}
