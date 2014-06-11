package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.google.common.base.Objects;

@YukonPaoPart(idColumnName = "deviceId")
public class CompleteDeviceDirectCommSettings {
    private int portId = 0;

    @YukonPaoField
    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(portId);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != CompleteDeviceDirectCommSettings.class) {
            return false;
        }
        CompleteDeviceDirectCommSettings that = (CompleteDeviceDirectCommSettings) other;
        return portId == that.portId;
    }

    @Override
    public String toString() {
        return "CompleteDeviceDirectCommSettings [portId=" + portId + "]";
    }
}
