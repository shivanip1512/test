package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

public class WifiSuperMeterData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apSsid;

    private String configuredApBssid;

    private String connectedApBssid;

    private WifiSecurityType securityType;

    // A string represents an ipv6 address, e.x., "FD30:0000:0000:0001:0214:08FF:FE0A:BF91"
    // Yukon UI can display it directly without adding colons.
    private String virtualGwIpv6Addr;

    public String getApSsid() {
        return apSsid;
    }

    public void setApSsid(String apSsid) {
        this.apSsid = apSsid;
    }

    public String getConfiguredApBssid() {
        return configuredApBssid;
    }

    public void setConfiguredApBssid(String configuredApBssid) {
        this.configuredApBssid = configuredApBssid;
    }

    public String getConnectedApBssid() {
        return connectedApBssid;
    }

    public void setConnectedApBssid(String connectedApBssid) {
        this.connectedApBssid = connectedApBssid;
    }

    public WifiSecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(WifiSecurityType securityType) {
        this.securityType = securityType;
    }

    public String getVirtualGwIpv6Addr() {
        return virtualGwIpv6Addr;
    }

    public void setVirtualGwIpv6Addr(String virtualGwIpv6Addr) {
        this.virtualGwIpv6Addr = virtualGwIpv6Addr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((apSsid == null) ? 0 : apSsid.hashCode());
        result = prime * result + ((configuredApBssid == null) ? 0 : configuredApBssid.hashCode());
        result = prime * result + ((connectedApBssid == null) ? 0 : connectedApBssid.hashCode());
        result = prime * result + ((securityType == null) ? 0 : securityType.hashCode());
        result = prime * result
                + ((virtualGwIpv6Addr == null) ? 0 : virtualGwIpv6Addr.hashCode());
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
        WifiSuperMeterData other = (WifiSuperMeterData) obj;
        if (apSsid == null) {
            if (other.apSsid != null)
                return false;
        } else if (!apSsid.equals(other.apSsid))
            return false;
        if (configuredApBssid == null) {
            if (other.configuredApBssid != null)
                return false;
        } else if (!configuredApBssid.equals(other.configuredApBssid))
            return false;
        if (connectedApBssid == null) {
            if (other.connectedApBssid != null)
                return false;
        } else if (!connectedApBssid.equals(other.connectedApBssid))
            return false;
        if (securityType != other.securityType)
            return false;
        if (virtualGwIpv6Addr == null) {
            if (other.virtualGwIpv6Addr != null)
                return false;
        } else if (!virtualGwIpv6Addr.equals(other.virtualGwIpv6Addr))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("WifiSuperMeterData [apSsid=%s, configuredApBssid=%s, connectedApBssid=%s, securityType=%s, virtualGwIpv6Addr=%s]",
                        apSsid,
                        configuredApBssid,
                        connectedApBssid,
                        securityType,
                        virtualGwIpv6Addr);
    }
}