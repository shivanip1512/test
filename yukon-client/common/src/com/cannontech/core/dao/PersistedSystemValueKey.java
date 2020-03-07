package com.cannontech.core.dao;

import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.type.InstantType;

public enum PersistedSystemValueKey {
	
    VALIDATION_ENGINE_LAST_CHANGE_ID(-1L, InputTypeFactory.longType()),
    MSP_SUBSTATION_DEVICE_GROUP_SYNC_LAST_COMPLETED(null, new InstantType()),
    MSP_BILLING_CYCLE_DEVICE_GROUP_SYNC_LAST_COMPLETED(null, new InstantType()),
    CMEP_BILLING_FILE_LAST_CHANGE_ID(1L, InputTypeFactory.longType()),
    DATA_COLLECTION_LAST_CHANGE_ID(-1L, InputTypeFactory.longType()),
    DATA_COLLECTION_TIME(null, new InstantType()),
    DATA_COLLECTION_RECALC_TIME(null, new InstantType()),
    INFRASTRUCTURE_WARNINGS_LAST_RUN_TIME(null, new InstantType()),
    NEST_SYNC_TIME(null, new InstantType()), 
    ITRON_DATA_LAST_RECORD_ID(-1L, InputTypeFactory.longType()),
    ITRON_READ_GROUP_ID(-1L, InputTypeFactory.longType()),
    MSP_ENROLLMENT_SYNC_LAST_COMPLETED(null, new InstantType()),
    //Last Network Tree time regeneration (by NM)
    DYNAMIC_RFN_DEVICE_DATA_LAST_UPDATE_TIME(null, new InstantType()),
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
    
    /**
     * Check to see if the specified value matches the key default. The specified value's type must match the default's
     * type, so be aware of comparing int vs. long, etc.
     */
    public boolean isDefaultValue(Object value) {
        return value.equals(defaultValue);
    }
    
    public InputType<?> getInputType() {
        return inputType;
    }
}
