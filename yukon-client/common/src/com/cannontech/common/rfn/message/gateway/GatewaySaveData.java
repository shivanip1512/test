package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;


/**
 * Represents the data needed to perform a save operation (create or edit).
 * Shared by the {@link GatewayCreateRequest} and {@link GatewayEditRequest} classes.
 */
public class GatewaySaveData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String ipAddress;
    private Authentication authentication;
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Authentication getAuthentication() {
        return authentication;
    }
    
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((authentication == null) ? 0 : authentication.hashCode());
        result = prime * result
                + ((ipAddress == null) ? 0 : ipAddress.hashCode());
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
        GatewaySaveData other = (GatewaySaveData) obj;
        if (authentication == null) {
            if (other.authentication != null)
                return false;
        } else if (!authentication.equals(other.authentication))
            return false;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("GatewaySaveData [ipAddress=%s, authentication=%s]", ipAddress, authentication);
    }
    
}