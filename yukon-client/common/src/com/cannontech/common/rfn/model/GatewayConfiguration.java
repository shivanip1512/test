package com.cannontech.common.rfn.model;

public class GatewayConfiguration {

    private Integer id;
    private String ipv6Prefix;
    
    @Override
    public String toString() {
        return String.format(
            "GatewayConfiguration [id=%s, ipv6Prefix=%s]", id, ipv6Prefix);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipv6Prefix == null) ? 0 : ipv6Prefix.hashCode());
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
        GatewayConfiguration other = (GatewayConfiguration) obj;
        if (ipv6Prefix == null) {
            if (other.ipv6Prefix != null) {
                return false;
            }
        } else if (!ipv6Prefix.equals(other.ipv6Prefix)) {
            return false;
        }
        return true;
    }

    public String getIpv6Prefix() {
        return ipv6Prefix;
    }

    public void setIpv6Prefix(String ipv6Prefix) {
        this.ipv6Prefix = ipv6Prefix;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}