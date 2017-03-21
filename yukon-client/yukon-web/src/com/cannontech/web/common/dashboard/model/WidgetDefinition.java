package com.cannontech.web.common.dashboard.model;

import java.util.List;

/**
 * Defines a widget by its type, and by the parameters required to create an instance of it.
 */
public class WidgetDefinition {
    private final WidgetType type;
    private final List<WidgetParameter> parameters;
    
    public WidgetDefinition(WidgetType type, List<WidgetParameter> parameters) {
        this.type = type;
        this.parameters = parameters;
    }
    
    public WidgetType getType() {
        return type;
    }
    
    /**
     * @return The list of parameters that must be supplied to instantiate an instance of this widget.
     */
    public List<WidgetParameter> getParameters() {
        return parameters;
    }
}
