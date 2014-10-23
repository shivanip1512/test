package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * JMS Queue name example: yukon.qr.obj.common.rfn.GatewayUpgradeResponse
 */
public class RfnGatewayUpgradeResponse implements RfnIdentifyingMessage, Serializable {
    private static final long serialVersionUID = 1L;

    private String upgradeId;
    private RfnIdentifier rfnIdentifier;
    private RfnGatewayUpgradeResponseType responseType = RfnGatewayUpgradeResponseType.UNKNOWN;
    private short gatewayMessageType = -1;
    private byte gatewayMessageStatus = -1;
    private int fragmentId = -1;
    
    public String getUpgradeId() {
        return upgradeId;
    }
    
    public void setUpgradeId(String upgradeId) {
        this.upgradeId = upgradeId;
    }
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public RfnGatewayUpgradeResponseType getResponseType() {
        return responseType;
    }
    
    public void setResponseType(RfnGatewayUpgradeResponseType responseType) {
        this.responseType = responseType;
    }
    
    public short getGatewayMessageType() {
        return gatewayMessageType;
    }
    
    public void setGatewayMessageType(short gatewayMessageType) {
        this.gatewayMessageType = gatewayMessageType;
    }
    
    public byte getGatewayMessageStatus() {
        return gatewayMessageStatus;
    }
    
    public void setGatewayMessageStatus(byte gatewayMessageStatus) {
        this.gatewayMessageStatus = gatewayMessageStatus;
    }
    
    public int getFragmentId() {
        return fragmentId;
    }
    
    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fragmentId;
        result = prime * result + gatewayMessageStatus;
        result = prime * result + gatewayMessageType;
        result = prime * result + ((responseType == null) ? 0 : responseType.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + ((upgradeId == null) ? 0 : upgradeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RfnGatewayUpgradeResponse other = (RfnGatewayUpgradeResponse) obj;
        if (fragmentId != other.fragmentId)
            return false;
        if (gatewayMessageStatus != other.gatewayMessageStatus)
            return false;
        if (gatewayMessageType != other.gatewayMessageType)
            return false;
        if (responseType != other.responseType)
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (upgradeId == null) {
            if (other.upgradeId != null)
                return false;
        } else if (!upgradeId.equals(other.upgradeId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RfnGatewayUpgradeResponse [upgradeId=%s, rfnIdentifier=%s, responseType=%s, gatewayMessageType=%s, gatewayMessageStatus=%s, fragmentId=%s]",
                    upgradeId,
                    rfnIdentifier,
                    responseType,
                    gatewayMessageType,
                    gatewayMessageStatus,
                    fragmentId);
    }
}
