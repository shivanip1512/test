package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName="SubstationId", paoTypes=PaoType.CAP_CONTROL_SUBSTATION)
public class CompleteCapControlSubstation extends CompleteYukonPao {
    private int voltReductionPointId = 0;
    private String mapLocationId = "0";

    @YukonPaoField
    public int getVoltReductionPointId() {
        return voltReductionPointId;
    }

    public void setVoltReductionPointId(int voltReductionPointId) {
        this.voltReductionPointId = voltReductionPointId;
    }

    @YukonPaoField
    public String getMapLocationId() {
        return mapLocationId;
    }

    public void setMapLocationId(String mapLocationId) {
        this.mapLocationId = mapLocationId;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), voltReductionPointId, mapLocationId);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteCapControlSubstation) {
            if (!super.equals(object)) 
                return false;
            CompleteCapControlSubstation that = (CompleteCapControlSubstation) object;
            return Objects.equal(this.voltReductionPointId, that.voltReductionPointId)
                && Objects.equal(this.mapLocationId, that.mapLocationId);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteCapControlSubstation [voltReductionPointId=" + voltReductionPointId
               + ", mapLocationId=" + mapLocationId + "]";
    }
}
