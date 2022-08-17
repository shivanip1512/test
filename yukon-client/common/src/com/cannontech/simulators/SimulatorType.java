package com.cannontech.simulators;

/**
 * Types of simulators run in the Yukon simulator service.
 * SIMULATOR_STARTUP is not a type of simulator, but it is used to differentiate
 * SimulatorStartupSettings requests from other simulator requests. Do not try to upload settings
 * for the SIMULATOR_STARTUP type to the YukonSimulatorSettings table. This will not work because
 * there are no entries for of type SIMULATOR_STARTUP in the YukonSimulatorSettingsKey enum.
 */
public enum SimulatorType {
    AMR_CREATION,
    DATA_STREAMING,
    GATEWAY,
    RFN_LCR,
    RFN_METER,
    RFN_NETWORK,
    RFN_METER_READ_CONTROL,
    IVVC,
    SIMULATOR_STARTUP,
    SMART_NOTIFICATION,
    STATUS_ARCHIVE,
    DEVICE_ARCHIVE,
    NEST
    ;
}
