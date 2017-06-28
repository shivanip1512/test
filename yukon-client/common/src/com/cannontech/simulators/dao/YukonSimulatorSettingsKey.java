package com.cannontech.simulators.dao;

import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;

public enum YukonSimulatorSettingsKey {
    AMR_CREATION_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),

    DATA_STREAMING_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),

    //SimulatedGatewayDataSettings
    GATEWAY_SIMULATOR_DATA_STREAMING_LOADING(50, InputTypeFactory.longType()),
    GATEWAY_SIMULATOR_RETURN_GWY800_MODEL(false, InputTypeFactory.booleanType()),
    GATEWAY_SIMULATOR_NUM_NOT_READY_NODES(500, InputTypeFactory.integerType()),
    GATEWAY_SIMULATOR_NUM_READY_NODES(1000, InputTypeFactory.integerType()),
    GATEWAY_SIMULATOR_FAILSAFE_MODE(false, InputTypeFactory.booleanType()),
    GATEWAY_SIMULATOR_CONNECTION_STATUS("CONNECTED", InputTypeFactory.stringType()),
    //SimulatedUpdateReplySettings
    GATEWAY_SIMULATOR_UPDATE_CREATE_RESULT("SUCCESSFUL", InputTypeFactory.stringType()),
    GATEWAY_SIMULATOR_UPDATE_EDIT_RESULT("SUCCESSFUL", InputTypeFactory.stringType()),
    GATEWAY_SIMULATOR_UPDATE_DELETE_RESULT("SUCCESSFUL", InputTypeFactory.stringType()),
    //SimulatedCertificateReplySettings
    GATEWAY_SIMULATOR_UPDATE_ACK_TYPE("ACCEPTED_FULLY", InputTypeFactory.stringType()),
    GATEWAY_SIMULATOR_UPDATE_STATUS_TYPE("REQUEST_ACCEPTED", InputTypeFactory.stringType()),
    //SimulatedFirmwareVersionReplySettings
    GATEWAY_SIMULATOR_FIRWARE_REPLY_TYPE("SUCCESS", InputTypeFactory.stringType()),
    GATEWAY_SIMULATOR_FIRWARE_VERSION("6.1.0", InputTypeFactory.stringType()),
    //SimulatedFirwareReplySettings
    GATEWAY_SIMULATOR_FIRWARE_RESULT_TYPE("ACCEPTED", InputTypeFactory.stringType()),
    //Run on startup for Gateway
    GATEWAY_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),

    RFN_LCR_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),

    RFN_METER_SIMULATOR_METER_TYPE("ALL RFN Type", InputTypeFactory.stringType()),
    RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE(10, InputTypeFactory.integerType()),
    RFN_METER_SIMULATOR_REPORTING_INTERVAL("REPORTING_INTERVAL_1_HOURS", InputTypeFactory
        .stringType()),
    RFN_METER_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),

    RFN_NETWORK_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),

    IVVC_SIMULATOR_INCREASED_SPEED_MODE(false, InputTypeFactory.booleanType()),
    IVVC_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType()),
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