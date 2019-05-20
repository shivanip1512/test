package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(tableName = "LMGROUP", idColumnName = "DeviceId", paoTypes = PaoType.LM_GROUP_METER_DISCONNECT)
public class CompleteLoadGroupBase extends CompleteDevice {

    private float kWCapacity = 0.0f;

    @YukonPaoField
    public float getkWCapacity() {
        return kWCapacity;
    }

    public void setkWCapacity(float kWCapacity) {
        this.kWCapacity = kWCapacity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Float.floatToIntBits(kWCapacity);
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
        if (Float.floatToIntBits(kWCapacity) != Float.floatToIntBits(other.kWCapacity))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteLoadGroupBase [kWCapacity=" + kWCapacity + "]";
    }
}
