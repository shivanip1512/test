package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(tableName = "LMGROUP", idColumnName = "DeviceId", paoTypes = PaoType.LM_GROUP_METER_DISCONNECT)
public class CompleteLoadGroupBase extends CompleteDevice {

    private double kWCapacity = 0.0;

    @YukonPaoField
    public double getkWCapacity() {
        return kWCapacity;
    }

    public void setkWCapacity(double kWCapacity) {
        this.kWCapacity = kWCapacity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(kWCapacity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        CompleteLoadGroupBase other = (CompleteLoadGroupBase) obj;
        if (Double.doubleToLongBits(kWCapacity) != Double.doubleToLongBits(other.kWCapacity))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteLoadGroupBase [kWCapacity=" + kWCapacity + "]";
    }
}
