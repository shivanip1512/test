package com.cannontech.common.rfn.message.gateway;

public enum RfnGatewayUpgradeRequestAckType {
    ACCEPTED_FULLY,
    ACCEPTED_PARTIALLY,
    REJECTED_INVALID_UPGRADE_ID,
    REJECTED_NO_GATEWAY_BEING_UPGRADED,
    REJECTED_INVALID_UPGRADE_DATA,
    REJECTED_NETWORK_MANAGER_ERROR,
    ;
}
