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
    private Short gatewayMessageType;
    private Byte gatewayMessageStatus;
    private Integer fragmentId;
    
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
    
    public Short getGatewayMessageType() {
        return gatewayMessageType;
    }
    
    public void setGatewayMessageType(Short gatewayMessageType) {
        this.gatewayMessageType = gatewayMessageType;
    }
    
    public Byte getGatewayMessageStatus() {
        return gatewayMessageStatus;
    }
    
    public void setGatewayMessageStatus(Byte gatewayMessageStatus) {
        this.gatewayMessageStatus = gatewayMessageStatus;
    }
    
    public Integer getFragmentId() {
        return fragmentId;
    }
    
    public void setFragmentId(Integer fragmentId) {
        this.fragmentId = fragmentId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fragmentId == null) ? 0 : fragmentId.hashCode());
        result =
            prime * result + ((gatewayMessageStatus == null) ? 0 : gatewayMessageStatus.hashCode());
        result =
            prime * result + ((gatewayMessageType == null) ? 0 : gatewayMessageType.hashCode());
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
        if (fragmentId == null) {
            if (other.fragmentId != null)
                return false;
        } else if (!fragmentId.equals(other.fragmentId))
            return false;
        if (gatewayMessageStatus == null) {
            if (other.gatewayMessageStatus != null)
                return false;
        } else if (!gatewayMessageStatus.equals(other.gatewayMessageStatus))
            return false;
        if (gatewayMessageType == null) {
            if (other.gatewayMessageType != null)
                return false;
        } else if (!gatewayMessageType.equals(other.gatewayMessageType))
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
