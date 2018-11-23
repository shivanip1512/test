package com.cannontech.web.common.dashboard.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Object representing a specific widget on a dashboard. 
 * @see WidgetType WidgetType for the generalized types of widgets.
 */
public class Widget {
    private int id;
    private WidgetType type;
    private int dashboardId;
    private Map<String, String> parameters = new HashMap<>();
    private String helpText;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public WidgetType getType() {
        return type;
    }
    
    public void setType(WidgetType type) {
        this.type = type;
    }
    
    public int getDashboardId() {
        return dashboardId;
    }
    
    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public Optional<String> getParameter(String key) {
        return Optional.ofNullable(parameters.get(key));
    }
    
    public void setParameters(Map<String, String> parameters) {
        if (parameters != null) {
            this.parameters = new HashMap<>();
            this.parameters.putAll(parameters);
        }
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
}
