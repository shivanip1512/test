package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(idColumnName="DeviceId")
public class CompleteZbGateway extends CompleteDevice {
    private String firmwareVersion;
    private String macAddress;

    @YukonPaoField
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    @YukonPaoField
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        return "CompleteZbGateway [firmwareVersion=" + firmwareVersion + ", macAddress="
               + macAddress + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((firmwareVersion == null) ? 0 : firmwareVersion.hashCode());
        result = prime * result + ((macAddress == null) ? 0 : macAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompleteZbGateway other = (CompleteZbGateway) obj;
        if (firmwareVersion == null) {
            if (other.firmwareVersion != null)
                return false;
        } else if (!firmwareVersion.equals(other.firmwareVersion))
            return false;
        if (macAddress == null) {
            if (other.macAddress != null)
                return false;
        } else if (!macAddress.equals(other.macAddress))
            return false;
        return true;
    }
}
