package com.cannontech.common.rfn.model;

import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponseType;

/**
 * This is a Yukon-centric distilation of possible responses from RfnGatewayUpgradeRequestAckType and
 * RfnGatewayUpgradeResponseType.
 */
public enum GatewayCertificateUpdateStatus {
    STARTED, // Process initiated in Yukon, not acknowledged by NM
    REQUEST_ACCEPTED, // Network manager received request
    INVALID_RFN_ID,
    INVALID_SUPER_ADMIN_PASSWORD,
    ALREADY_IN_PROGRESS,
    ABORTED,
    NM_ERROR,
    COMPLETED, // Success!
    ;
    
    public static GatewayCertificateUpdateStatus of(RfnGatewayUpgradeResponseType responseType) {
        switch (responseType) {
            case COMPLETED:
            case ACCEPTED:
                return COMPLETED;
            case ABORTED:
                return ABORTED;
            case FAILED:
            case JOB_ALREADY_EXISTS:
            case JOB_SCHEDULE_FAILED:
            case JOB_EXECUTE_FAILED:
            default:
                return NM_ERROR;
        }
    }
}
