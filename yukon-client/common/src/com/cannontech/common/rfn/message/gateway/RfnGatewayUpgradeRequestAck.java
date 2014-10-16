package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name example: the temporary (reply) queue for yukon.qr.obj.common.rfn.GatewayUpgradeRequest
 */
public class RfnGatewayUpgradeRequestAck implements Serializable {
    private static final long serialVersionUID = 1L;

    private RfnGatewayUpgradeRequestAckType requestAckType;
    private String upgradeId;
    private Set<RfnIdentifier> beingUpgradedRfnIdentifiers;
    private Map<RfnIdentifier, String> invalidRfnIdentifiers;
    private Map<RfnIdentifier, String> invalidSuperAdminPasswordRfnIdentifiers;
    private Map<RfnIdentifier, String> lastUpgradeInProcessRfnIdentifiers;
    private String requestAckMessage;
    
    public RfnGatewayUpgradeRequestAckType getRequestAckType() {
        return requestAckType;
    }
    
    public void setRequestAckType(RfnGatewayUpgradeRequestAckType requestAckType) {
        this.requestAckType = requestAckType;
    }
    
    public String getUpgradeId() {
        return upgradeId;
    }
    
    public void setUpgradeId(String upgradeId) {
        this.upgradeId = upgradeId;
    }
    
    public Set<RfnIdentifier> getBeingUpgradedRfnIdentifiers() {
        return beingUpgradedRfnIdentifiers;
    }
    
    public void setBeingUpgradedRfnIdentifiers(Set<RfnIdentifier> beingUpgradedRfnIdentifiers) {
        this.beingUpgradedRfnIdentifiers = beingUpgradedRfnIdentifiers;
    }
    
    public Map<RfnIdentifier, String> getInvalidRfnIdentifiers() {
        return invalidRfnIdentifiers;
    }
    
    public void setInvalidRfnIdentifiers(Map<RfnIdentifier, String> invalidRfnIdentifiers) {
        this.invalidRfnIdentifiers = invalidRfnIdentifiers;
    }
    
    public Map<RfnIdentifier, String> getInvalidSuperAdminPasswordRfnIdentifiers() {
        return invalidSuperAdminPasswordRfnIdentifiers;
    }
    
    public void setInvalidSuperAdminPasswordRfnIdentifiers(Map<RfnIdentifier, String> invalidSuperAdminPasswordRfnIdentifiers) {
        this.invalidSuperAdminPasswordRfnIdentifiers = invalidSuperAdminPasswordRfnIdentifiers;
    }
    
    public Map<RfnIdentifier, String> getLastUpgradeInProcessRfnIdentifiers() {
        return lastUpgradeInProcessRfnIdentifiers;
    }
    
    public void setLastUpgradeInProcessRfnIdentifiers(Map<RfnIdentifier, String> lastUpgradeInProcessRfnIdentifiers) {
        this.lastUpgradeInProcessRfnIdentifiers = lastUpgradeInProcessRfnIdentifiers;
    }
    
    public String getRequestAckMessage() {
        return requestAckMessage;
    }
    
    public void setRequestAckMessage(String requestAckMessage) {
        this.requestAckMessage = requestAckMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime
                    * result
                    + ((beingUpgradedRfnIdentifiers == null) ? 0 : beingUpgradedRfnIdentifiers
                        .hashCode());
        result =
            prime * result
                    + ((invalidRfnIdentifiers == null) ? 0 : invalidRfnIdentifiers.hashCode());
        result =
            prime
                    * result
                    + ((invalidSuperAdminPasswordRfnIdentifiers == null) ? 0
                            : invalidSuperAdminPasswordRfnIdentifiers.hashCode());
        result =
            prime
                    * result
                    + ((lastUpgradeInProcessRfnIdentifiers == null) ? 0
                            : lastUpgradeInProcessRfnIdentifiers.hashCode());
        result = prime * result + ((requestAckMessage == null) ? 0 : requestAckMessage.hashCode());
        result = prime * result + ((requestAckType == null) ? 0 : requestAckType.hashCode());
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
        RfnGatewayUpgradeRequestAck other = (RfnGatewayUpgradeRequestAck) obj;
        if (beingUpgradedRfnIdentifiers == null) {
            if (other.beingUpgradedRfnIdentifiers != null)
                return false;
        } else if (!beingUpgradedRfnIdentifiers.equals(other.beingUpgradedRfnIdentifiers))
            return false;
        if (invalidRfnIdentifiers == null) {
            if (other.invalidRfnIdentifiers != null)
                return false;
        } else if (!invalidRfnIdentifiers.equals(other.invalidRfnIdentifiers))
            return false;
        if (invalidSuperAdminPasswordRfnIdentifiers == null) {
            if (other.invalidSuperAdminPasswordRfnIdentifiers != null)
                return false;
        } else if (!invalidSuperAdminPasswordRfnIdentifiers
            .equals(other.invalidSuperAdminPasswordRfnIdentifiers))
            return false;
        if (lastUpgradeInProcessRfnIdentifiers == null) {
            if (other.lastUpgradeInProcessRfnIdentifiers != null)
                return false;
        } else if (!lastUpgradeInProcessRfnIdentifiers
            .equals(other.lastUpgradeInProcessRfnIdentifiers))
            return false;
        if (requestAckMessage == null) {
            if (other.requestAckMessage != null)
                return false;
        } else if (!requestAckMessage.equals(other.requestAckMessage))
            return false;
        if (requestAckType != other.requestAckType)
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
            .format("RfnGatewayUpgradeRequestAck [requestAckType=%s, upgradeId=%s, beingUpgradedRfnIdentifiers=%s, invalidRfnIdentifiers=%s, invalidSuperAdminPasswordRfnIdentifiers=%s, lastUpgradeInProcessRfnIdentifiers=%s, requestAckMessage=%s]",
                    requestAckType,
                    upgradeId,
                    beingUpgradedRfnIdentifiers,
                    invalidRfnIdentifiers,
                    invalidSuperAdminPasswordRfnIdentifiers,
                    lastUpgradeInProcessRfnIdentifiers,
                    requestAckMessage);
    }
}