package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(idColumnName="DeviceId", paoTypes=PaoType.DIGIGATEWAY)
public class CompleteDigiGateway extends CompleteZbGateway {
    private int digiId;
    
    @YukonPaoField
    public int getDigiId() {
        return digiId;
    }
    
    public void setDigiId(int digiId) {
        this.digiId = digiId;
    }

    @Override
    public String toString() {
        return "CompleteDigiGateway [digiId=" + digiId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + digiId;
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
        CompleteDigiGateway other = (CompleteDigiGateway) obj;
        if (digiId != other.digiId)
            return false;
        return true;
    }
}
