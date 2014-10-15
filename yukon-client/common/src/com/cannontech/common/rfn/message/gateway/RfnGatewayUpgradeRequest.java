package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name example: yukon.qr.obj.common.rfn.GatewayUpgradeRequest
 */
public class RfnGatewayUpgradeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String upgradeID;
    private Set<RfnIdentifier> rfnIdentifiers; // when null or empty, all Gateways will be upgraded
    private byte[] upgradeData;
    
    public String getUpgradeID()
    {
        return upgradeID;
    }
    public void setUpgradeID(String upgradeID)
    {
        this.upgradeID = upgradeID;
    }
    public Set<RfnIdentifier> getRfnIdentifiers()
    {
        return rfnIdentifiers;
    }
    public void setRfnIdentifiers(Set<RfnIdentifier> rfnIdentifiers)
    {
        this.rfnIdentifiers = rfnIdentifiers;
    }
    public byte[] getUpgradeData()
    {
        return upgradeData;
    }
    public void setUpgradeData(byte[] upgradeData)
    {
        this.upgradeData = upgradeData;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((rfnIdentifiers == null) ? 0 : rfnIdentifiers.hashCode());
        result = prime * result + Arrays.hashCode(upgradeData);
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
        RfnGatewayUpgradeRequest other = (RfnGatewayUpgradeRequest) obj;
        if (rfnIdentifiers == null) {
            if (other.rfnIdentifiers != null)
                return false;
        } else if (!rfnIdentifiers.equals(other.rfnIdentifiers))
            return false;
        if (!Arrays.equals(upgradeData, other.upgradeData))
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
        return "RfnGatewayUpgradeRequest [upgradeID=" + upgradeID
            + ", rfnIdentifiers=" + rfnIdentifiers + ", upgradeData="
            + Arrays.toString(upgradeData) + "]";
    }
}
