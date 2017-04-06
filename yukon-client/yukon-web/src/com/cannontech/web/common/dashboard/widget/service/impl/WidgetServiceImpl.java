package com.cannontech.web.common.dashboard.widget.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.stream.StreamUtils;
import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetCategory;
import com.cannontech.web.common.dashboard.model.WidgetParameter;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class WidgetServiceImpl implements WidgetService {

    @Override
    public Multimap<WidgetCategory, WidgetType> getTypesByCategory() {
        return WidgetType.stream()
                         .collect(StreamUtils.groupingBy(WidgetType::getCategory));
    }

    @Override
    public Widget createWidget(WidgetType type, Map<String, Object> parameters) 
            throws WidgetMissingParameterException, WidgetParameterValidationException {
        
        Map<String, String> validParameters = validateParameters(type, parameters);
        
        Widget widget = new Widget();
        widget.setType(type);
        widget.setParameters(validParameters);
        
        return widget;
    }

    private Map<String, String> validateParameters(WidgetType type, Map<String, Object> parameters) 
            throws WidgetMissingParameterException, WidgetParameterValidationException {

        // Check that all parameter names match parameter names in definition
        // We will have to revisit this if we need optional parameters someday
        Set<String> missingParameterNames = Sets.difference(parameters.keySet(), type.getParameterNames());
        if (missingParameterNames.size() > 0) {
            throw new WidgetMissingParameterException("Unable to create widget, missing parameters.", missingParameterNames);
        }
        
        // Validate each parameter against its type-specific validator
        for (WidgetParameter definitionParameter : type.getParameters()) {
            String parameterName = definitionParameter.getName();
            Object suppliedParameter = parameters.get(parameterName);
            definitionParameter.getValidator().validate(parameterName, suppliedParameter);
        }
        
        return parameters.entrySet()
                         .stream()
                         .collect(Collectors.toMap(entry -> entry.getKey(), 
                                                   entry -> entry.getValue().toString()));
    }
}