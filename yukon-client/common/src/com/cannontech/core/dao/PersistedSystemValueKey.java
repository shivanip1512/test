package com.cannontech.core.dao;

import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;

public enum PersistedSystemValueKey {
	
    VALIDATION_ENGINE_LAST_CHANGE_ID(-1, InputTypeFactory.longType()),
    MSP_SUBSTATION_DEVICE_GROUP_SYNC_LAST_COMPLETED(null, InputTypeFactory.iso8601DateTimeType()),
    MSP_BILLING_CYCLE_DEVICE_GROUP_SYNC_LAST_COMPLETED(null, InputTypeFactory.iso8601DateTimeType()),
    ;
    
    private final Object defaultValue;
    private final InputType<?> inputType;

    private PersistedSystemValueKey(Object defaultValue, InputType<?> inputType) {
        this.defaultValue = defaultValue;
        this.inputType = inputType;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public InputType<?> getInputType() {
        return inputType;
    }
}
