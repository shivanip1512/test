package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName="areaId", paoTypes=PaoType.CAP_CONTROL_AREA)
public class CompleteCapControlArea extends CompleteYukonPao {
    private int voltReductionPointId;

    @YukonPaoField
    public int getVoltReductionPointId() {
        return voltReductionPointId;
    }
    
    public void setVoltReductionPointId(int voltReductionPointId) {
        this.voltReductionPointId = voltReductionPointId;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteCapControlArea [voltReductionPointId=" 
               + voltReductionPointId + "]";
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), voltReductionPointId);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteCapControlArea) {
            if (!super.equals(object)) 
                return false;
            CompleteCapControlArea that = (CompleteCapControlArea) object;
            return Objects.equal(this.voltReductionPointId, that.voltReductionPointId);
        }
        return false;
    }
}
