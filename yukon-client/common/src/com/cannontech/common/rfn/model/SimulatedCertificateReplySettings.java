package com.cannontech.common.rfn.model;

import static com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus.*;

import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;

public class SimulatedCertificateReplySettings {
    RfnGatewayUpgradeRequestAckType ackType;
    GatewayCertificateUpdateStatus deviceUpdateStatus;
    
    public void setAckType(RfnGatewayUpgradeRequestAckType ackType) {
        this.ackType = ackType;
    }
    
    public RfnGatewayUpgradeRequestAckType getAckType() {
        return ackType;
    }
    
    public void setDeviceUpdateStatus(GatewayCertificateUpdateStatus deviceUpdateStatus) {
        if (deviceUpdateStatus == REQUEST_ACCEPTED || deviceUpdateStatus == INVALID_RFN_ID ||
                deviceUpdateStatus == INVALID_SUPER_ADMIN_PASSWORD || deviceUpdateStatus == ALREADY_IN_PROGRESS) {
            this.deviceUpdateStatus = deviceUpdateStatus;
        } else {
            throw new IllegalArgumentException("Invalid value: " + deviceUpdateStatus + ". Only REQUEST_ACCEPTED, "
                    + "INVALID_RFN_ID, INVALID_SUPER_ADMIN_PASSWORD, ALREADY_IN_PROGRESS are permitted.");
        }
    }
    
    public GatewayCertificateUpdateStatus getDeviceUpdateStatus() {
        return deviceUpdateStatus;
    }
}
