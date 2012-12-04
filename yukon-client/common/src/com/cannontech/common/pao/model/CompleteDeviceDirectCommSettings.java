package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.google.common.base.Objects;

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
    public int hashCode(){
        return Objects.hashCode(portId);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteDeviceDirectCommSettings) {
            CompleteDeviceDirectCommSettings that = (CompleteDeviceDirectCommSettings) object;
            return Objects.equal(this.portId, that.portId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "CompleteDeviceDirectCommSettings [portId=" + portId + "]";
    }
}
