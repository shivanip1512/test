package com.cannontech.common.rfn.model;

/**
 * This is a Yukon-centric distilation of possible responses from RfnGatewayUpgradeRequestAckType and
 * RfnGatewayUpgradeResponseType.
 */
public enum GatewayCertificateUpdateStatus {
    STARTED, //Default, process initiated in Yukon
    REQUEST_ACCEPTED, //Network manager received request
    INVALID_UPGRADE_ID,
    INVALID_DATA,
    ALREADY_IN_PROGRESS,
    ABORTED,
    NM_ERROR, //Generic catch-all
    COMPLETED, //Success!
    ;
}
