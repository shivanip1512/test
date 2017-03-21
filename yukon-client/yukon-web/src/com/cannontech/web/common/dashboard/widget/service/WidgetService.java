package com.cannontech.web.common.dashboard.widget.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetCategory;
import com.cannontech.web.common.dashboard.model.WidgetDefinition;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.google.common.collect.Multimap;

/**
 * This service provides WidgetDefinitions to describe widgets by their type and required parameters, and creates
 * instances of Widgets from an appropriate map of parameters.
 * 
 * This service does not save Widgets to the database. To save widgets, they must be assigned to a Dashboard, and the
 * Dashboard must be saved via the DashboardService. 
 */
public interface WidgetService {
    
    /**
     * Gets a WidgetDefinition, which describes the parameters required to instantiate this type of widget.
     * @return The WidgetDefinition for the specified WidgetType.
     */
    public WidgetDefinition getDefinition(WidgetType type);
    
    /**
     * Gets all WidgetDefinitions, which describe each widget by the parameters required to instantiate them.
     * @return All WidgetDefinitions.
     */
    public Set<WidgetDefinition> getAllDefinitions();
    
    /**
     * Get a Multimap of categories to definitions.
     * @return All widget definitions sorted by category.
     */
    public Multimap<WidgetCategory, WidgetDefinition> getDefinitionsByCategory();
    
    /**
     * Create a Widget object of the specified type, according to the parameters. This does not save the object - it
     * must be assigned to a dashboard and the dashboard must be saved to persist it.
     * 
     * @param type The type of widget to create.
     * @param parameters The user-specified inputs defining the configuration and behavior of the widget.
     * @return A new Widget object with the specified configuration.
     */
    public Widget createWidget(WidgetType type, Map<String, Object> parameters);
}
