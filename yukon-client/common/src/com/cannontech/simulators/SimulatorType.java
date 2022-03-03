package com.cannontech.simulators;

/**
 * Types of simulators run in the Yukon simulator service, with two exceptions:
 * <ul>
 * <li>SIMULATOR_STARTUP is not a type of simulator, but it is used to differentiate SimulatorStartupSettings requests from other simulator
 * requests. Do not try to upload settings for the SIMULATOR_STARTUP type to the YukonSimulatorSettings table. This will not work because
 * there are no entries for of type SIMULATOR_STARTUP in the YukonSimulatorSettingsKey enum.</li>
 * 
 * <li>FIELD_SIMULATOR is also not a simulator run inside the Yukon simulator service, but transfers messages to the C++ Field Simulator 
 * service to provide it with Web UI control.</li>
 * </ul>
 */
public enum SimulatorType {
    AMR_CREATION,
    DATA_STREAMING,
    FIELD_SIMULATOR,
    GATEWAY,
    RFN_DEVICE_DELETE,
    RFN_LCR,
    RFN_METER,
    RFN_NETWORK,
    RFN_METER_READ_CONTROL,
    IVVC,
    SIMULATOR_STARTUP,
    SMART_NOTIFICATION,
    STATUS_ARCHIVE,
    DEVICE_ARCHIVE,
    NEST,
    POINT_DATA_CACHE_CORRELATION,
    EATON_CLOUD
    ;
}
