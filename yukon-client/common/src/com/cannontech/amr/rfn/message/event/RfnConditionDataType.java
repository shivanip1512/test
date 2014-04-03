package com.cannontech.amr.rfn.message.event;


/**
 * Type of extra data in events and alarms
 */
public enum RfnConditionDataType {
    
    CLEARED(Boolean.class), // True = Cleared, False = Active
    COUNT(Long.class),
    DIRECTION(Direction.class),
    MEASURED_VALUE(Double.class),
    EVENT_START_TIME(Long.class),  // milliseconds
    THRESHOLD_VALUE(Double.class),
    UOM(String.class),
    UOM_MODIFIERS(RfnUomModifierSet.class),
    OLD_DNP3_ADDRESS(Integer.class),
    NEW_DNP3_ADDRESS(Integer.class),
    PORT_TYPE(Short.class),
    PORT_LOCKED_MINUTES(Long.class)
    ;
    
    private Class<?> objectType;
    
    private RfnConditionDataType(Class<?> objectType) {
        this.objectType = objectType;
    }
    
    public Class<?> getObjectType() {
        return objectType;
    }
    
}