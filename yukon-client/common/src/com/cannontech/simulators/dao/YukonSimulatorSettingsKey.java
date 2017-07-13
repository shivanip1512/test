package com.cannontech.simulators.dao;

import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.web.input.type.InputType;

public enum YukonSimulatorSettingsKey {
    AMR_CREATION_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.AMR_CREATION),
    
    //SimulatedDataStreamingSettings
    DATA_STREAMING_SIMULATOR_OVERLOAD_VER(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_DEV_ERR_VER("GATEWAY_OVERLOADED", InputTypeFactory.stringType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_DEV_ERR_VER_ENABLED(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_FAIL_VER(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_NUM_DEV_ERR_VER(0, InputTypeFactory.integerType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_OVERLOAD_CON(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_DEV_ERR_CON("GATEWAY_OVERLOADED", InputTypeFactory.stringType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_DEV_ERR_CON_ENABLED(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_FAIL_CON(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_NUM_DEV_ERR_CON(0, InputTypeFactory.integerType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_ACCEPTED_ERR(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),
    DATA_STREAMING_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.DATA_STREAMING),

    //Gateway Data Simulator
    //SimulatedGatewayDataSettings
    GATEWAY_SIMULATOR_DATA_STREAMING_LOADING(50.0, InputTypeFactory.doubleType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_RETURN_GWY800_MODEL(false, InputTypeFactory.booleanType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_NUM_NOT_READY_NODES(500, InputTypeFactory.integerType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_NUM_READY_NODES(1000, InputTypeFactory.integerType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_FAILSAFE_MODE(false, InputTypeFactory.booleanType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_CONNECTION_STATUS("CONNECTED", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    //SimulatedUpdateReplySettings
    GATEWAY_SIMULATOR_UPDATE_CREATE_RESULT("SUCCESSFUL", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_UPDATE_EDIT_RESULT("SUCCESSFUL", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_UPDATE_DELETE_RESULT("SUCCESSFUL", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    //SimulatedCertificateReplySettings
    GATEWAY_SIMULATOR_UPDATE_ACK_TYPE("ACCEPTED_FULLY", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_UPDATE_STATUS_TYPE("REQUEST_ACCEPTED", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    //SimulatedFirmwareVersionReplySettings
    GATEWAY_SIMULATOR_FIRWARE_REPLY_TYPE("SUCCESS", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    GATEWAY_SIMULATOR_FIRWARE_VERSION("6.1.0", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    //SimulatedFirwareReplySettings
    GATEWAY_SIMULATOR_FIRWARE_RESULT_TYPE("ACCEPTED", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
    //Run on startup for Gateway
    GATEWAY_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.GATEWAY),

    //LCR Meter Simulator Settings (SimulatorSettings)
    RFN_LCR_SIMULATOR_6200_SERIAL_FROM(100000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6200_SERIAL_TO(200000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6600_SERIAL_FROM(300000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6600_SERIAL_TO(320000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_DUPLICATE_PERCENTAGE(10, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.RFN_LCR),

    //RFN Meter Simulator Settings (SimulatorSettings)
    RFN_METER_SIMULATOR_METER_TYPE("ALL RFN Type", InputTypeFactory.stringType(), SimulatorType.RFN_METER),
    RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE(10, InputTypeFactory.integerType(), SimulatorType.RFN_METER),
    RFN_METER_SIMULATOR_REPORTING_INTERVAL("REPORTING_INTERVAL_1_HOURS", InputTypeFactory.stringType(), SimulatorType.RFN_METER),
    RFN_METER_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.RFN_METER),

    //RFN Network Simulator (SimulatorSettings)
    RFN_NETWORK_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),

    //IvvcSimulatorSettings
    IVVC_SIMULATOR_INCREASED_SPEED_MODE(false, InputTypeFactory.booleanType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.IVVC),
    ;

    private final Object defaultValue;
    private final InputType<?> inputType;
    private final SimulatorType simulatorType;

    private YukonSimulatorSettingsKey(Object defaultValue, InputType<?> inputType, SimulatorType simulatorType) {
        this.defaultValue = defaultValue;
        this.inputType = inputType;
        this.simulatorType = simulatorType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public InputType<?> getInputType() {
        return inputType;
    }
    
    public SimulatorType getSimulatorType() {
        return simulatorType;
    }
}