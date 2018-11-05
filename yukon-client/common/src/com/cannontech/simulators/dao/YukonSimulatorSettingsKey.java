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
    GATEWAY_SIMULATOR_IPV6_PREFIX_UPDATE_RESULT("SUCCESSFUL", InputTypeFactory.stringType(), SimulatorType.GATEWAY),
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

    //RFN LCR Simulator Settings (SimulatorSettings)
    RFN_LCR_SIMULATOR_6200_SERIAL_FROM(100000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6200_SERIAL_TO(200000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6600_SERIAL_FROM(300000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6600_SERIAL_TO(320000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6700_SERIAL_FROM(340000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_6700_SERIAL_TO(380000, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_DUPLICATE_PERCENTAGE(10, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_TLV_VERSION(4, InputTypeFactory.integerType(), SimulatorType.RFN_LCR),
    RFN_LCR_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.RFN_LCR),

    //RFN Meter Simulator Settings (SimulatorSettings)
    RFN_METER_SIMULATOR_METER_TYPE("ALL RFN Type", InputTypeFactory.stringType(), SimulatorType.RFN_METER),
    RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE(10, InputTypeFactory.integerType(), SimulatorType.RFN_METER),
    RFN_METER_SIMULATOR_REPORTING_INTERVAL("REPORTING_INTERVAL_1_HOURS", InputTypeFactory.stringType(), SimulatorType.RFN_METER),
    RFN_METER_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.RFN_METER),

    //RFN Read and Control Simulator
    RFN_METER_READ_CONTROL_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.RFN_METER_READ_CONTROL),
    //Simulated Reads
    RFN_METER_READ_SIMULATOR_READ_REPLY1("OK", InputTypeFactory.stringType(), SimulatorType.RFN_METER_READ_CONTROL),
    RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_1(0 , InputTypeFactory.integerType(), SimulatorType.RFN_METER_READ_CONTROL),
    RFN_METER_READ_SIMULATOR_READ_REPLY2("OK", InputTypeFactory.stringType(), SimulatorType.RFN_METER_READ_CONTROL),
    RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_2(0 , InputTypeFactory.integerType(), SimulatorType.RFN_METER_READ_CONTROL),
    RFN_METER_READ_SIMULATOR_MODEL_MISMATCH(0 , InputTypeFactory.integerType(), SimulatorType.RFN_METER_READ_CONTROL),
    //Simulated Disconnects
    RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY1("OK", InputTypeFactory.stringType(), SimulatorType.RFN_METER_READ_CONTROL),
    RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_1(0 , InputTypeFactory.integerType(), SimulatorType.RFN_METER_READ_CONTROL),
    RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY2("SUCCESS", InputTypeFactory.stringType(), SimulatorType.RFN_METER_READ_CONTROL),
    RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_2(0 , InputTypeFactory.integerType(), SimulatorType.RFN_METER_READ_CONTROL),
    
    //RFN Network Simulator (SimulatorSettings)
    //NeighborData
    RFN_NETWORK_SIMULATOR_NEIGHB_ADDR("00:14:08:03:FA:A2", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_PRIM_FORW_ROUTE(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_PRIM_REV_ROUTE(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_SEC_ALT_GATEWAY(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_FLOAT_NEIGHB(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_IGNORED_NEIGHB(true, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_BATTERY_NEIGHB(true, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_SEC_SERV_GATEWAY(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_LINK_COST(3.0, InputTypeFactory.doubleType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_NUM_SAMPLES(1, InputTypeFactory.integerType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_EXT_BAND(3, InputTypeFactory.integerType(), SimulatorType.RFN_NETWORK), //must cast this to short when adding it to settings
    RFN_NETWORK_SIMULATOR_NEIGHB_LINK_RATE("4x", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHB_LINK_POW("125 mWatt", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_NEIGHBOR_DATA_REPLY_TYPE("OK", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),

    //RouteData
    RFN_NETWORK_SIMULATOR_ROUTE_DEST_ADDR("00:14:08:03:FA:A2", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_ROUTE_HOP_ADDR("00:14:08:03:FA:A2", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_ROUTE_COST(2, InputTypeFactory.integerType(), SimulatorType.RFN_NETWORK), //must cast this to short when adding it to settings
    RFN_NETWORK_SIMULATOR_ROUTE_HOP_COUNT(1, InputTypeFactory.integerType(), SimulatorType.RFN_NETWORK), //must cast this to short when adding it to settings
    RFN_NETWORK_SIMULATOR_PRIM_FORW_ROUTE(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PRIM_REV_ROUTE(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PRIM_BATTERY_ROUTE(true, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PRIM_START_GC(true, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PRIM_REM_UPDATE(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PRIM_IGNORED_ROUTE(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PRIM_VALID_ROUTE(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PRIM_TIMED_OUT(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_ROUTE_COLOR(1, InputTypeFactory.integerType(), SimulatorType.RFN_NETWORK), //must cast this to short when adding it to settings
    RFN_NETWORK_SIMULATOR_PRIMARY_DATA_REPLY_TYPE("OK", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),

    //ParentData
    RFN_NETWORK_SIMULATOR_PARENT_SN("123", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PARENT_MAC_ADDR("17:14:08:03:FA:A2", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_PARENT_REPLY_TYPE("OK", InputTypeFactory.stringType(), SimulatorType.RFN_NETWORK),
    RFN_NETWORK_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.RFN_NETWORK),

    //IvvcSimulatorSettings
    IVVC_SIMULATOR_INCREASED_SPEED_MODE(false, InputTypeFactory.booleanType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_SUBSTATION_BUS_KWH(3000.0, InputTypeFactory.doubleType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_AUTOGENERATE_SUBSTATION_BUS_KWH(true, InputTypeFactory.booleanType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_LOCAL_VOLTAGE_OFFSET_VAR(1200.0, InputTypeFactory.doubleType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_REMOTE_VOLTAGE_OFFSET_VAR(1200.0, InputTypeFactory.doubleType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_BLOCKED_POINTS("", InputTypeFactory.stringType(), SimulatorType.IVVC),
    IVVC_SIMULATOR_BAD_QUALITY_POINTS("", InputTypeFactory.stringType(), SimulatorType.IVVC),
    
    //SmartNotificationSimulator
    SMART_NOTIFICATION_SIM_DAILY_DIGEST_HOUR(0, InputTypeFactory.integerType(), SimulatorType.SMART_NOTIFICATION),
    SMART_NOTIFICATION_SIM_USER_GROUP_ID(-1, InputTypeFactory.integerType(), SimulatorType.SMART_NOTIFICATION),
    SMART_NOTIFICATION_GENERATE_TEST_EMAIL(false, InputTypeFactory.booleanType(), SimulatorType.SMART_NOTIFICATION),
    SMART_NOTIFICATION_SIM_EVENTS_PER_TYPE(250, InputTypeFactory.integerType(), SimulatorType.SMART_NOTIFICATION),
    SMART_NOTIFICATION_SIM_EVENTS_PER_MESSAGE(5, InputTypeFactory.integerType(), SimulatorType.SMART_NOTIFICATION),
    SMART_NOTIFICATION_SIM_WAIT_TIME_SEC(3, InputTypeFactory.integerType(), SimulatorType.SMART_NOTIFICATION),
    
    // Nest Settings
    NEST_FILE_NAME("", InputTypeFactory.stringType(), SimulatorType.NEST),
    NEST_VERSION(1, InputTypeFactory.integerType(), SimulatorType.NEST),
    NEST_SIMULATOR_RUN_ON_STARTUP(false, InputTypeFactory.booleanType(), SimulatorType.NEST)
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