package com.cannontech.web.common.dashboard.model;

/**
 * Enum of the types of inputs that can be required to create a dashboard widget.
 */
public enum WidgetInputType {
    ANALOG_POINT_PICKER(Integer.class),
    ;
    
    private final Class<?> inputClass;
    
    private WidgetInputType(Class<?> inputClass) {
        this.inputClass = inputClass;
    }
    
    /**
     * Gets the expected class of input values for this input type. For example, a point picker input would be a point
     * ID, so the input class would be Integer.class.
     */
    public Class<?> getInputClass() {
        return inputClass;
    }
}
