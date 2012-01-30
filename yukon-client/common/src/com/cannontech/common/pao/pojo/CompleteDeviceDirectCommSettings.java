package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;

@YukonPaoPart(idColumnName="deviceId")
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
        final int prime = 31;
        int result = 1;
        result = prime * result + portId;
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
        CompleteDeviceDirectCommSettings other = (CompleteDeviceDirectCommSettings) obj;
        if (portId != other.portId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteDeviceDirectCommSettings [portId=" + portId + "]";
    }
}
