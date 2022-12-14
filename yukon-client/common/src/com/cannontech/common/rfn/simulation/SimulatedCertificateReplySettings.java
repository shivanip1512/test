package com.cannontech.common.rfn.simulation;

import static com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus.*;

import java.io.Serializable;
import java.util.List;

import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.google.common.collect.Lists;

public class SimulatedCertificateReplySettings implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final List<GatewayCertificateUpdateStatus> acceptedUpdateStatusTypes = 
            Lists.newArrayList(REQUEST_ACCEPTED,
                               INVALID_RFN_ID,
                               INVALID_SUPER_ADMIN_PASSWORD,
                               ALREADY_IN_PROGRESS);
    
    private RfnGatewayUpgradeRequestAckType ackType;
    private GatewayCertificateUpdateStatus deviceUpdateStatus;
    
    public void setAckType(RfnGatewayUpgradeRequestAckType ackType) {
        this.ackType = ackType;
    }
    
    public RfnGatewayUpgradeRequestAckType getAckType() {
        return ackType;
    }
    
    public void setDeviceUpdateStatus(GatewayCertificateUpdateStatus deviceUpdateStatus) {
        if (acceptedUpdateStatusTypes.contains(deviceUpdateStatus)) {
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
