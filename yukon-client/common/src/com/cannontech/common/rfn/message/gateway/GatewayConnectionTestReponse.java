package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

public class GatewayConnectionTestReponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private GatewayConnectionTestResult result;
    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public GatewayConnectionTestResult getResult() {
        return result;
    }
    
    public void setResult(GatewayConnectionTestResult result) {
        this.result = result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
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
        GatewayConnectionTestReponse other = (GatewayConnectionTestReponse) obj;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (result != other.result)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("GatewayConnectionTestReponse [result=%s, ipAddress=%s]", result, ipAddress);
    }
    
}