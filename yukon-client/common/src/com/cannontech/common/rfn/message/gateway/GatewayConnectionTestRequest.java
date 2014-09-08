package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

public class GatewayConnectionTestRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
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
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("GatewayConnectionTestRequest [ipAddress=%s]", ipAddress);
    }
}