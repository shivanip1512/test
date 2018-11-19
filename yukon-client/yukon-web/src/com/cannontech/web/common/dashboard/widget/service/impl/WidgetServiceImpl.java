package com.cannontech.web.common.dashboard.widget.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetCategory;
import com.cannontech.web.common.dashboard.model.WidgetParameter;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class WidgetServiceImpl implements WidgetService {
    
    @Autowired RoleAndPropertyDescriptionService roleAndPropertyDescriptionService;
    
    /**
     * If any WidgetType has accessControl specified, that WidgetType will be included in the map 
     * returned only if at-least one of the enum values in accessControl is true (OR condition is checked 
     * between enum values in accessControl). 
     */
    @Override
    public Map<WidgetCategory, List<WidgetType>> getTypesByCategory(LiteYukonUser user) {
        LinkedHashMap<WidgetCategory, List<WidgetType>> map = new LinkedHashMap<>();
        List<WidgetCategory> widgetCategories = Lists.newArrayList(WidgetCategory.values());
        Collections.sort(widgetCategories);
        for (WidgetCategory category : widgetCategories) {
            map.put(category, new ArrayList<>());
        }
        List<WidgetType> widgetTypes =  Stream.of(WidgetType.values())
                .filter(widgetType -> StringUtils.isBlank(widgetType.getAccessControl()) ||
                                        roleAndPropertyDescriptionService.checkIfAtLeastOneExists(widgetType.getAccessControl(), user))
                .collect(Collectors.toList());
        Collections.sort(widgetTypes, (type1, type2) -> type1.name().compareTo(type2.name()));
        for (WidgetType type : widgetTypes) {
            map.get(type.getCategory()).add(type);
        }
        return map;
    }

    @Override
    public Widget createWidget(WidgetType type, Map<String, String> parameters) 
            throws WidgetMissingParameterException, WidgetParameterValidationException {
        
        Map<String, String> validParameters = validateParameters(type, parameters);
        
        Widget widget = new Widget();
        widget.setType(type);
        widget.setParameters(validParameters);
        
        return widget;
    }

    private Map<String, String> validateParameters(WidgetType type, Map<String, String> parameters) 
            throws WidgetMissingParameterException, WidgetParameterValidationException {

        // Check that all parameter names match parameter names in definition
        // We will have to revisit this if we need optional parameters someday
        Set<String> missingParameterNames = Sets.difference(parameters.keySet(), type.getParameterNames());
        if (missingParameterNames.size() > 0) {
            throw new WidgetMissingParameterException("Unable to create widget, missing parameters.", missingParameterNames);
        }
        // Validate each parameter against its type-specific validator
        for (WidgetParameter definitionParameter : type.getParameters()) {
            if (definitionParameter.getValidator() != null) {
                String parameterName = definitionParameter.getName();
                Object suppliedParameter = parameters.get(parameterName);
                definitionParameter.getValidator().validate(parameterName, suppliedParameter,
                    definitionParameter.getInputType());
            }
        }
        
        return parameters.entrySet()
                         .stream()
                         .collect(Collectors.toMap(entry -> entry.getKey(), 
                                                   entry -> entry.getValue().toString()));
    }

    public Map<String, String> setDefaultParameters(WidgetType type, Map<String, String> parameters) {
        for (WidgetParameter definitionParameter : type.getParameters()) {
            if (StringUtils.isEmpty(parameters.get(definitionParameter.getName()))
                && definitionParameter.getDefaultValue() != null) {
                parameters.put(definitionParameter.getName(), definitionParameter.getDefaultValue());
            }
        }
        return parameters;
    }
}