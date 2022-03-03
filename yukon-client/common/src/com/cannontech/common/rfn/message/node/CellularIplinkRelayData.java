package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

public class CellularIplinkRelayData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean modemEnabled;
    
    private Boolean simCardPresent;
    
    private String imei;
    
    private String imsi;
    
    private String iccid;
    
    private String firmwareVersion;

    private String apn;
    
    // A string represents an ipv6 address, e.x., "FD30:0000:0000:0001:0214:08FF:FE0A:BF91"
    // Yukon UI can display it directly without adding colons.
    private String virtualGwIpv6Addr;

    public Boolean getModemEnabled() {
        return modemEnabled;
    }

    public void setModemEnabled(Boolean modemEnabled) {
        this.modemEnabled = modemEnabled;
    }

    public Boolean getSimCardPresent() {
        return simCardPresent;
    }

    public void setSimCardPresent(Boolean simCardPresent) {
        this.simCardPresent = simCardPresent;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
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
        result = prime * result + ((apn == null) ? 0 : apn.hashCode());
        result =
            prime * result + ((modemEnabled == null) ? 0 : modemEnabled.hashCode());
        result = prime * result + ((firmwareVersion == null) ? 0 : firmwareVersion.hashCode());
        result = prime * result + ((iccid == null) ? 0 : iccid.hashCode());
        result = prime * result + ((imei == null) ? 0 : imei.hashCode());
        result = prime * result + ((imsi == null) ? 0 : imsi.hashCode());
        result = prime * result + ((simCardPresent == null) ? 0 : simCardPresent.hashCode());
        result = prime * result + ((virtualGwIpv6Addr == null) ? 0 : virtualGwIpv6Addr.hashCode());
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
        CellularIplinkRelayData other = (CellularIplinkRelayData) obj;
        if (apn == null) {
            if (other.apn != null)
                return false;
        } else if (!apn.equals(other.apn))
            return false;
        if (modemEnabled == null) {
            if (other.modemEnabled != null)
                return false;
        } else if (!modemEnabled.equals(other.modemEnabled))
            return false;
        if (firmwareVersion == null) {
            if (other.firmwareVersion != null)
                return false;
        } else if (!firmwareVersion.equals(other.firmwareVersion))
            return false;
        if (iccid == null) {
            if (other.iccid != null)
                return false;
        } else if (!iccid.equals(other.iccid))
            return false;
        if (imei == null) {
            if (other.imei != null)
                return false;
        } else if (!imei.equals(other.imei))
            return false;
        if (imsi == null) {
            if (other.imsi != null)
                return false;
        } else if (!imsi.equals(other.imsi))
            return false;
        if (simCardPresent == null) {
            if (other.simCardPresent != null)
                return false;
        } else if (!simCardPresent.equals(other.simCardPresent))
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
        return "CellularIplinkRelayData [modemEnabled=" + modemEnabled
            + ", simCardPresent=" + simCardPresent + ", imei=" + imei + ", imsi=" + imsi
            + ", iccid=" + iccid + ", firmwareVersion=" + firmwareVersion + ", apn=" + apn
            + ", virtualGwIpv6Addr=" + virtualGwIpv6Addr + "]";
    }
}