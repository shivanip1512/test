package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName = "deviceId", paoTypes = { PaoType.HONEYWELL_9000, PaoType.HONEYWELL_FOCUSPRO,
    PaoType.HONEYWELL_VISIONPRO_8000, PaoType.HONEYWELL_THERMOSTAT })
public class CompleteHoneywellWifiThermostat extends CompleteDevice {
    private String macAddress;
    private Integer userId;

    @YukonPaoField
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @YukonPaoField
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), macAddress, userId);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteHoneywellWifiThermostat) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteHoneywellWifiThermostat that = (CompleteHoneywellWifiThermostat) object;
            return Objects.equal(macAddress, that.macAddress) && Objects.equal(userId, that.userId);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteHoneywellWifiThermostat [ macAddress=" + macAddress + ", userId=" + userId
            + "]";
    }
}
