package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * Message sent from Yukon to NM to request a gateway configuration.
 * 
 * JMS queue name: yukon.qr.obj.common.rfn.GatewaySetConfigRequest
 */
public class GatewaySetConfigRequest implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    
    private int routeColor; 

    private String ipv6Prefix; // ipv6 prefix as string in the format of "FD30:0000:0000:1822::/64"
    // Add future config items here
    
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public int getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(int routeColor) {
        this.routeColor = routeColor;
    }

    public String getIpv6Prefix() {
        return ipv6Prefix;
    }

    public void setIpv6Prefix(String ipv6Prefix) {
        this.ipv6Prefix = ipv6Prefix;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipv6Prefix == null) ? 0 : ipv6Prefix.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + routeColor;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GatewaySetConfigRequest other = (GatewaySetConfigRequest) obj;
        if (ipv6Prefix == null) {
            if (other.ipv6Prefix != null) {
                return false;
            }
        } else if (!ipv6Prefix.equals(other.ipv6Prefix)) {
            return false;
        }
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null) {
                return false;
            }
        } else if (!rfnIdentifier.equals(other.rfnIdentifier)) {
            return false;
        }
        if (routeColor != other.routeColor) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GatewaySetConfigRequest [rfnIdentifier=" + rfnIdentifier + ", routeColor="
               + routeColor + ", ipv6Prefix=" + ipv6Prefix + "]";
    }

}