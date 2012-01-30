package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(idColumnName="AreaId", paoTypes=PaoType.CAP_CONTROL_SPECIAL_AREA)
public class CompleteCapControlSpecialArea extends CompleteYukonPaObject {
    private int voltReductionPointId;

    @YukonPaoField
    public int getVoltReductionPointId() {
        return voltReductionPointId;
    }

    public void setVoltReductionPointId(int voltReductionPointId) {
        this.voltReductionPointId = voltReductionPointId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
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
        CompleteCapControlSpecialArea other = (CompleteCapControlSpecialArea) obj;
        if (voltReductionPointId != other.voltReductionPointId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteCapControlSpecialArea [voltReductionPointId=" + voltReductionPointId + "]";
    }
}
