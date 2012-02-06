package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(tableName="DeviceCbc", idColumnName="deviceId")
public class CompleteCbcBase extends CompleteDevice {
    private int serialNumber = 0;
    private int routeId = 0;
    
    @YukonPaoField
    public int getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    @YukonPaoField
    public int getRouteId() {
        return routeId;
    }
    
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + routeId;
        result = prime * result + serialNumber;
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
        CompleteCbcBase other = (CompleteCbcBase) obj;
        if (routeId != other.routeId)
            return false;
        if (serialNumber != other.serialNumber)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteDeviceCbc [serialNumber=" + serialNumber + ", routeId=" + routeId + "]";
    }
    
}
