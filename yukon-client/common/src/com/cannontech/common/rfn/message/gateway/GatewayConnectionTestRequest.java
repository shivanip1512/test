package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

public class GatewayConnectionTestRequest implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    private String ipAddress;
    private Authentication admin;
    private Authentication superAdmin;
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Authentication getAdmin() {
        return admin;
    }
    
    public void setAdmin(Authentication admin) {
        this.admin = admin;
    }
    
    public Authentication getSuperAdmin() {
        return superAdmin;
    }
    
    public void setSuperAdmin(Authentication superAdmin) {
        this.superAdmin = superAdmin;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((admin == null) ? 0 : admin.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + ((superAdmin == null) ? 0 : superAdmin.hashCode());
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
        GatewayConnectionTestRequest other = (GatewayConnectionTestRequest) obj;
        if (admin == null) {
            if (other.admin != null)
                return false;
        } else if (!admin.equals(other.admin))
            return false;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (superAdmin == null) {
            if (other.superAdmin != null)
                return false;
        } else if (!superAdmin.equals(other.superAdmin))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("GatewayConnectionTestRequest [rfnIdentifier=%s, ipAddress=%s, admin=%s, superAdmin=%s]", rfnIdentifier, ipAddress, admin, superAdmin);
    }
    
}