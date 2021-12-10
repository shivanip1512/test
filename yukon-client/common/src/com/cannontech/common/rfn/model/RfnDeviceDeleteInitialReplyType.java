package com.cannontech.common.rfn.model;

public enum RfnDeviceDeleteInitialReplyType {
    OK,
    INVALID_RFNIDENTIFIER,  // the request rfnIdentifier is a null itself or
                            // the request rfnIdentifier has blank sensorManufacturer, blank sensorModel or blank sensorSerialNumber
    NO_DEVICE,
    NM_FAILURE
}
