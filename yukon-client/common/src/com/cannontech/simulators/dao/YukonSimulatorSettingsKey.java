package com.cannontech.simulators.dao;

import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;

public enum YukonSimulatorSettingsKey {
    AMR_CREATION_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),
    
    DATA_STREAMING_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),
    
    GATEWAY_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),
    
    RFN_LCR_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),
    
    RFN_METER_SIMULATOR_METER_TYPE("ALL RFN Type", InputTypeFactory.stringType()), 
    RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE(10, InputTypeFactory.integerType()),
    RFN_METER_SIMULATOR_REPORTING_INTERVAL("REPORTING_INTERVAL_1_HOURS", InputTypeFactory.stringType()),
    RFN_METER_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),
        
    RFN_NETWORK_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),
    ;
    
    private final Object defaultValue;
    private final InputType<?> inputType;

    private YukonSimulatorSettingsKey(Object defaultValue, InputType<?> inputType) {
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