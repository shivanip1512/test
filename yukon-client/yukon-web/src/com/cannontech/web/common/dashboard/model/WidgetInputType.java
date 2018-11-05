package com.cannontech.web.common.dashboard.model;

/**
 * Enum of the types of inputs that can be required to create a dashboard widget.
 */
public enum WidgetInputType {
    
    METER_PICKER(Integer.class, "meterPicker"),
    DEVICE_GROUP(String.class, "deviceGroupPicker"),
    CHECKBOX(Boolean.class, "checkbox"),
    MONITOR_PICKER(String.class, "monitorPicker"),
    PORT_PICKER(String.class, "portPicker"),
    CONTROL_AREA_OR_SCENARIO_OR_PROGRAM_PICKER(Integer.class, "controlAreaOrLMProgramOrScenarioPicker");
    ;
    
    private final Class<?> inputClass;
    private String jspName;

    
    private WidgetInputType(Class<?> inputClass, String jspName) {
        this.inputClass = inputClass;
        setJspName(jspName);
    }
    
    /**
     * Gets the expected class of input values for this input type. For example, a point picker input would be a point
     * ID, so the input class would be Integer.class.
     */
    public Class<?> getInputClass() {
        return inputClass;
    }

    public String getJspName() {
        return jspName;
    }

    public void setJspName(String jspName) {
        this.jspName = jspName;
    }
}
