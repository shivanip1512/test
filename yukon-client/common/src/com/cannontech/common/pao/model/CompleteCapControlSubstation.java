package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

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
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
        result = prime * result + voltReductionPointId;
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
        CompleteCapControlSubstation other = (CompleteCapControlSubstation) obj;
        if (mapLocationId == null) {
            if (other.mapLocationId != null)
                return false;
        } else if (!mapLocationId.equals(other.mapLocationId))
            return false;
        if (voltReductionPointId != other.voltReductionPointId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteCapControlSubstation [voltReductionPointId=" + voltReductionPointId
               + ", mapLocationId=" + mapLocationId + "]";
    }
}
