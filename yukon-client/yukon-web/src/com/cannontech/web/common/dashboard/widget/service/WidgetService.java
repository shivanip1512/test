package com.cannontech.web.common.dashboard.widget.service;

import java.util.List;
import java.util.Map;

import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetCategory;
import com.cannontech.web.common.dashboard.model.WidgetType;

/**
 * This service handles the creation of Widget objects based on type and user-specified parameters. It does not save 
 * Widgets to the database. To save widgets, they must be assigned to a Dashboard, and the Dashboard must be saved via 
 * the DashboardService. 
 */
public interface WidgetService {
    /**
     * Get a Multimap of widget categories to types.
     * @param yukonUser 
     * @return All widget types sorted by category.
     */
    public Map<WidgetCategory, List<WidgetType>> getTypesByCategory();
    
    /**
     * Create a Widget object of the specified type, according to the parameters. This does not save the object - it
     * must be assigned to a dashboard and the dashboard must be saved to persist it.
     * 
     * @param type The type of widget to create.
     * @param parameters The user-specified inputs defining the configuration and behavior of the widget.
     * @return A new Widget object with the specified configuration.
     * @throws WidgetParameterValidationException If a parameter fails validation.
     * @throws WidgetMissingParameterException If a parameter is missing.
     */
    public Widget createWidget(WidgetType type, Map<String, String> parameters) throws WidgetMissingParameterException,
            WidgetParameterValidationException;

    /**
     * Get a Multimap of widget default paramters. See WidgetType for default values
     * 
     * @param type The type of widget.
     * @param parameters The user-specified inputs defining the configuration and behavior of the widget.
     * @return A map of parameter name/value pairs filled in with any default values.
     */
    public Map<String, String> setDefaultParameters(WidgetType type, Map<String, String> parameters);
}
