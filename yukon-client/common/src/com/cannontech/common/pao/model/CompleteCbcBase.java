package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

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
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), serialNumber, routeId);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteCbcBase) {
            if (!super.equals(object)) 
                return false;
            CompleteCbcBase that = (CompleteCbcBase) object;
            return Objects.equal(this.serialNumber, that.serialNumber)
                && Objects.equal(this.routeId, that.routeId);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "CompleteCbcBase [serialNumber=" + serialNumber + ", routeId=" + routeId + "]";
    }
}
