package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

public class WifiSuperMeterData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer channelNum;
    private Double rssi;
    private String apBssid;
    private String apSsid;
    private WifiSecurityType securityType;

    public Integer getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(Integer channelNum) {
        this.channelNum = channelNum;
    }

    public Double getRssi() {
        return rssi;
    }

    public void setRssi(Double rssi) {
        this.rssi = rssi;
    }

    public String getApBssid() {
        return apBssid;
    }

    public void setApBssid(String apBssid) {
        this.apBssid = apBssid;
    }

    public String getApSsid() {
        return apSsid;
    }

    public void setApSsid(String apSsid) {
        this.apSsid = apSsid;
    }

    public WifiSecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(WifiSecurityType securityType) {
        this.securityType = securityType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((apBssid == null) ? 0 : apBssid.hashCode());
        result = prime * result + ((apSsid == null) ? 0 : apSsid.hashCode());
        result = prime * result + ((channelNum == null) ? 0 : channelNum.hashCode());
        result = prime * result + ((rssi == null) ? 0 : rssi.hashCode());
        result = prime * result + ((securityType == null) ? 0 : securityType.hashCode());
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
        if (apBssid == null) {
            if (other.apBssid != null)
                return false;
        } else if (!apBssid.equals(other.apBssid))
            return false;
        if (apSsid == null) {
            if (other.apSsid != null)
                return false;
        } else if (!apSsid.equals(other.apSsid))
            return false;
        if (channelNum == null) {
            if (other.channelNum != null)
                return false;
        } else if (!channelNum.equals(other.channelNum))
            return false;
        if (rssi == null) {
            if (other.rssi != null)
                return false;
        } else if (!rssi.equals(other.rssi))
            return false;
        if (securityType != other.securityType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WifiSuperMeterData [channelNum=" + channelNum + ", rssi=" + rssi + ", apBssid="
            + apBssid + ", apSsid=" + apSsid + ", securityType=" + securityType + "]";
    }

}
