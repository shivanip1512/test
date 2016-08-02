package com.cannontech.web.dev;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.service.SystemHealthMetricMethod;
import com.cannontech.web.dev.service.SystemHealthMetricSimulatorService;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

@Controller
@RequestMapping("/systemHealthMetricSimulator/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class SystemHealthMetricSimulatorController {
    @Autowired SystemHealthMetricSimulatorService metricSimulator;
    
    @RequestMapping("/home")
    public String home(ModelMap model, FlashScope flash) {
        
        Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> metricValues = new TreeMap<>();
        for (SystemHealthMetricIdentifier metricId : SystemHealthMetricIdentifier.values()) {
            metricValues.put(metricId, new TreeMap<SystemHealthMetricMethod, Object>());
            Collection<SystemHealthMetricMethod> methodsForType = SystemHealthMetricMethod.getMethodsForMetricType(metricId.getType());
            for (SystemHealthMetricMethod method : methodsForType) {
                metricValues.get(metricId).put(method, null);
            }
        }
        
        Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> currentMetricValues = metricSimulator.getAllValues();
        for (SystemHealthMetricIdentifier metricId : currentMetricValues.keySet()) {
            Map<SystemHealthMetricMethod, Object> metricMethods = currentMetricValues.get(metricId);
            for (SystemHealthMetricMethod method : metricMethods.keySet()) {
                Object metricMethodValue = metricMethods.get(method);
                metricValues.get(metricId).put(method, metricMethodValue);
            }
            
        }
        
        MetricSimulatorValues metricSimulatorValues = new MetricSimulatorValues();
        metricSimulatorValues.setActive(metricSimulator.isActive());
        metricSimulatorValues.setMetricValues(metricValues);
        
        model.addAttribute("metricSimulatorValues", metricSimulatorValues);
        
        return "systemMetricsSimulator.jsp";
    }
    
    @RequestMapping("setValues")
    public String setValues(@ModelAttribute MetricSimulatorValues metricSimulatorValues, FlashScope flash) {
        
        Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> allMetricValues = metricSimulatorValues.getMetricValues();
        for (SystemHealthMetricIdentifier metricId : allMetricValues.keySet()) {
            Map<SystemHealthMetricMethod, Object> metricMethodValues = allMetricValues.get(metricId);
            for (SystemHealthMetricMethod method : metricMethodValues.keySet()) {
                String value = (String) metricMethodValues.get(method);
                if (StringUtils.isBlank(value)) {
                    metricSimulator.removeValue(metricId, method);
                } else {
                    Object convertedValue = convertValue(value, method.getParameterTypes()[0]);
                    metricSimulator.setValue(metricId, method, convertedValue);
                }
            }
        }
        
        if (metricSimulatorValues.isActive()) {
            metricSimulator.start();
        } else {
            metricSimulator.stop();
        }
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.systemMetricsSimulator.updateSuccessful"));
        
        return "redirect:home";
    }
    
    private Object convertValue(String value, Class<?> clazz) {
        if (String.class == clazz) {
            return value;
        } else if (Integer.class == clazz) {
            return Integer.parseInt(value);
        } else if (Long.class == clazz) {
            return Long.parseLong(value);
        } else if (Double.class == clazz) {
            return Double.parseDouble(value);
        } else {
            throw new IllegalArgumentException("Unable to handle type: " + clazz);
        }
    }
    
}
