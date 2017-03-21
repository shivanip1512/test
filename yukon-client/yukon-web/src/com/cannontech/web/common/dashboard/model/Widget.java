package com.cannontech.web.common.dashboard.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Widget {
    private int id;
    private WidgetType type;
    private int dashboardId;
    private String helpTextKey;
    private Set<String> requiredJavascript = new HashSet<>();
    private Set<String> requiredCss = new HashSet<>();
    private Map<String, String> parameters;
    
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
        this.parameters = parameters;
    }

    public String getHelpTextKey() {
        return helpTextKey;
    }

    public void setHelpTextKey(String helpTextKey) {
        this.helpTextKey = helpTextKey;
    }

    public Set<String> getRequiredJavascript() {
        return requiredJavascript;
    }

    public void setRequiredJavascript(Set<String> requiredJavascript) {
        this.requiredJavascript = requiredJavascript;
    }

    public Set<String> getRequiredCss() {
        return requiredCss;
    }

    public void setRequiredCss(Set<String> requiredCss) {
        this.requiredCss = requiredCss;
    }
}
