package com.cannontech.amr.rfn.message.event;


/**
 * Type of extra data in events and alarms
 */
public enum RfnConditionDataType {
    
    CLEARED(Boolean.class), // True = Cleared, False = Active
    COUNT(Long.class),
    DIRECTION(Direction.class),
    MEASURED_VALUE(Double.class),
    OUTAGE_START_TIME(Long.class),  // milliseconds
    THRESHOLD_VALUE(Double.class),
    UOM(String.class),
    UOM_MODIFIERS(RfnUomModifierSet.class),
    ;
    
    private Class<?> objectType;
    
    private RfnConditionDataType(Class<?> objectType) {
        this.objectType = objectType;
    }
    
    public Class<?> getObjectType() {
        return objectType;
    }
    
}