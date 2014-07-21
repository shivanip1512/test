package com.cannontech.web.widget.support;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class WidgetMultiActionController extends MultiActionController {
    private final Object widgetController;
    
    public WidgetMultiActionController(Object delegate) {
        super(delegate);
        this.widgetController = delegate;
    }
    
    public Object getWidgetController() {
        return widgetController;
    }
    
}
