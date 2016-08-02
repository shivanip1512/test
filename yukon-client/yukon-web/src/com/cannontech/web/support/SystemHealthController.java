package com.cannontech.web.support;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.YukonLogManager.RfnLogger;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
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
    private static JmsTemplate jmsTemplate;
    private static Instant lastResync;
    
    @Autowired private SystemHealthService systemHealthService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
    
    @RequestMapping("/home")
    public String home(ModelMap model, LiteYukonUser user) throws MessageConversionException, JMSException {
        
        Map<SystemHealthMetricIdentifier, Boolean> favorites = new HashMap<>();
        List<SystemHealthMetricIdentifier> favoriteIds = systemHealthService.getFavorites(user);
        for (SystemHealthMetricIdentifier favoriteId : favoriteIds) {
            favorites.put(favoriteId, true);
        }
        model.addAttribute("favorites", favorites);
        
        List<SystemHealthMetric> extendedQueueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE_EXTENDED);
        model.addAttribute("extendedQueueData", extendedQueueData);
        
        List<SystemHealthMetric> queueData = systemHealthService.getMetricsByType(SystemHealthMetricType.JMS_QUEUE);
        model.addAttribute("queueData", queueData);
        
        return "systemHealth.jsp";
    }
    
    @RequestMapping(value="{metric}/favorite", method=RequestMethod.POST)
    public @ResponseBody Map<String, String> favorite(@PathVariable SystemHealthMetricIdentifier metric, 
                                                      YukonUserContext userContext, 
                                                      HttpServletResponse response) {
        
        return setFavorite(metric, userContext, response, true);
    }
    
    @RequestMapping(value="{metric}/unfavorite", method=RequestMethod.POST)
    public @ResponseBody Map<String, String> unfavorite(@PathVariable SystemHealthMetricIdentifier metric, 
                                                        YukonUserContext userContext, 
                                                        HttpServletResponse response) {
        
        return setFavorite(metric, userContext, response, false);
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
        RfnLogger rfnCommsLog = YukonLogManager.getRfnLogger();
        
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
        RfnArchiveStartupNotification notif = new RfnArchiveStartupNotification();
        jmsTemplate.convertAndSend("yukon.notif.obj.common.rfn.ArchiveStartupNotification", notif);
        
        resp.setStatus(HttpStatus.OK.value());
        json.put("message", accessor.getMessage("yukon.web.modules.support.systemHealth.sync.success"));
        return json;
    }
    
    /**
     * Either favorites or un-favorites the specified metric for the user.
     * @param isFavorite If true, sets the metric as a favorite. Otherwise, un-favorites the metric.
     */
    private Map<String, String> setFavorite(SystemHealthMetricIdentifier metric, YukonUserContext userContext, 
                                            HttpServletResponse response, boolean isFavorite) {
        
        Map<String, String> json = new HashMap<>();
        try {
            systemHealthService.setFavorite(userContext.getYukonUser(), metric, isFavorite);
            log.debug("Added favorite " + metric + " for user " + userContext.getYukonUser());
            response.setStatus(HttpStatus.OK.value());
            return json;
        } catch (Exception e) {
            log.error("Error saving favorite settings for system health metric.", e);
            
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String message = accessor.getMessage("yukon.web.modules.support.systemHealth.favoriteError");
            json.put("message", message);
            
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return json;
        }
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
