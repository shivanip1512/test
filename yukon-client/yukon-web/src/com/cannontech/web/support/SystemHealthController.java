package com.cannontech.web.support;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;

@Controller
@RequestMapping("/systemHealth/*")
public class SystemHealthController {
    private static final Logger log = YukonLogManager.getLogger(SystemHealthController.class);
    @Autowired private SystemHealthService systemHealthService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
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
