package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * JMS Queue name example: yukon.qr.obj.common.rfn.GatewayUpgradeResponse
 */
public class RfnGatewayUpgradeResponse implements RfnIdentifyingMessage, Serializable {
    private static final long serialVersionUID = 1L;

    private String upgradeID;
    private RfnIdentifier rfnIdentifier;
    private RfnGatewayUpgradeResponseType responseType;
    private short gatewayMessageType;
    private byte gatewayMessageStatus;
    private int fragmentID;
    public String getUpgradeID()
    {
        return upgradeID;
    }
    public void setUpgradeID(String upgradeID)
    {
        this.upgradeID = upgradeID;
    }
    @Override
    public RfnIdentifier getRfnIdentifier()
    {
        return rfnIdentifier;
    }
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier)
    {
        this.rfnIdentifier = rfnIdentifier;
    }
    public RfnGatewayUpgradeResponseType getResponseType()
    {
        return responseType;
    }
    public void setResponseType(RfnGatewayUpgradeResponseType responseType)
    {
        this.responseType = responseType;
    }
    public short getGatewayMessageType()
    {
        return gatewayMessageType;
    }
    public void setGatewayMessageType(short gatewayMessageType)
    {
        this.gatewayMessageType = gatewayMessageType;
    }
    public byte getGatewayMessageStatus()
    {
        return gatewayMessageStatus;
    }
    public void setGatewayMessageStatus(byte gatewayMessageStatus)
    {
        this.gatewayMessageStatus = gatewayMessageStatus;
    }
    public int getFragmentID()
    {
        return fragmentID;
    }
    public void setFragmentID(int fragmentID)
    {
        this.fragmentID = fragmentID;
    }
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + fragmentID;
        result = prime * result + gatewayMessageStatus;
        result = prime * result + gatewayMessageType;
        result =
            prime * result
                + ((responseType == null) ? 0 : responseType.hashCode());
        result =
            prime * result
                + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result =
            prime * result + ((upgradeID == null) ? 0 : upgradeID.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RfnGatewayUpgradeResponse other = (RfnGatewayUpgradeResponse) obj;
        if (fragmentID != other.fragmentID)
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
        if (upgradeID == null) {
            if (other.upgradeID != null)
                return false;
        } else if (!upgradeID.equals(other.upgradeID))
            return false;
        return true;
    }
    @Override
    public String toString()
    {
        return "RfnGatewayUpgradeResponse [upgradeID=" + upgradeID
            + ", rfnIdentifier=" + rfnIdentifier + ", responseType="
            + responseType + ", gatewayMessageType=" + gatewayMessageType
            + ", gatewayMessageStatus=" + gatewayMessageStatus
            + ", fragmentID=" + fragmentID + "]";
    }
}
