package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

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
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), digiId);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteDigiGateway) {
            if (!super.equals(object)) 
                return false;
            CompleteDigiGateway that = (CompleteDigiGateway) object;
            return Objects.equal(this.digiId, that.digiId);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteDigiGateway [digiId=" + digiId + "]";
    }
}
