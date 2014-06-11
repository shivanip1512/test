package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName = "DeviceId")
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
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), firmwareVersion, macAddress);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteZbGateway) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteZbGateway that = (CompleteZbGateway) object;
            return Objects.equal(firmwareVersion, that.firmwareVersion) && Objects.equal(macAddress, that.macAddress);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteZbGateway [firmwareVersion=" + firmwareVersion + ", macAddress="
            + macAddress + "]";
    }
}
