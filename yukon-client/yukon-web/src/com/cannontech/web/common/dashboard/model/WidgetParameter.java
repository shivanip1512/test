package com.cannontech.web.common.dashboard.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * A description of an input to dashboard widgets. Each WidgetParameter has a name (which is part of the i18n key and 
 * the parameter's identifier), and a WidgetInputType, which describes the UI element used to render the input when
 * creating the widget.
 * 
 * The name is kept separate from the WidgetInputType in case we require multiple of the same input type in a single
 * widget, and also so that different widgets can have different labels for the same input type.
 */
public class WidgetParameter implements Displayable {
    private static final String keyBase = "yukon.web.modules.dashboard.widgetParameter.";
    
    private final String name;
    private final MessageSourceResolvable messageKey;
    private final WidgetInputType inputType;
    
    public WidgetParameter(String name, WidgetInputType inputType) {
        this.name = name;
        messageKey = new YukonMessageSourceResolvable(keyBase + name);
        this.inputType = inputType;
    }
    
    /**
     * @return The name identifying this parameter.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return The WidgetInputType to be used for this parameter.
     */
    public WidgetInputType getInputType() {
        return inputType;
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return messageKey;
    }
}
