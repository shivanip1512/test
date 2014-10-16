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

    private String upgradeId;
    private Set<RfnIdentifier> rfnIdentifiers; // when null or empty, all Gateways will be upgraded
    private byte[] upgradeData;
    
    public String getUpgradeId() {
        return upgradeId;
    }
    
    public void setUpgradeId(String upgradeId) {
        this.upgradeId = upgradeId;
    }
    
    public Set<RfnIdentifier> getRfnIdentifiers() {
        return rfnIdentifiers;
    }
    
    public void setRfnIdentifiers(Set<RfnIdentifier> rfnIdentifiers) {
        this.rfnIdentifiers = rfnIdentifiers;
    }
    
    public byte[] getUpgradeData() {
        return upgradeData;
    }
    
    public void setUpgradeData(byte[] upgradeData) {
        this.upgradeData = upgradeData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifiers == null) ? 0 : rfnIdentifiers.hashCode());
        result = prime * result + Arrays.hashCode(upgradeData);
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
        RfnGatewayUpgradeRequest other = (RfnGatewayUpgradeRequest) obj;
        if (rfnIdentifiers == null) {
            if (other.rfnIdentifiers != null)
                return false;
        } else if (!rfnIdentifiers.equals(other.rfnIdentifiers))
            return false;
        if (!Arrays.equals(upgradeData, other.upgradeData))
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
            .format("RfnGatewayUpgradeRequest [upgradeId=%s, rfnIdentifiers=%s, upgradeData=%s]",
                    upgradeId,
                    rfnIdentifiers,
                    Arrays.toString(upgradeData));
    }
}
