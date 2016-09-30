package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(tableBacked = false, idColumnName = "deviceId", paoTypes = { PaoType.HONEYWELL_9000, PaoType.HONEYWELL_FOCUSPRO,
    PaoType.HONEYWELL_VISIONPRO_8000, PaoType.HONEYWELL_THERMOSTAT })
public class CompleteHoneywellThermostat extends CompleteDevice {
    private String macAddress;

    @YukonPaoField
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), macAddress);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteHoneywellThermostat) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteHoneywellThermostat that = (CompleteHoneywellThermostat) object;
            return Objects.equal(macAddress, that.macAddress);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteHoneywellThermostat [ macAddress=" + macAddress + "]";
    }
}
