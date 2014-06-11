package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName = "AreaId", paoTypes = PaoType.CAP_CONTROL_SPECIAL_AREA)
public class CompleteCapControlSpecialArea extends CompleteYukonPao {
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
        return Objects.hashCode(super.hashCode(), voltReductionPointId);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteCapControlSpecialArea) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteCapControlSpecialArea that = (CompleteCapControlSpecialArea) object;
            return voltReductionPointId == that.voltReductionPointId;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteCapControlSpecialArea [voltReductionPointId=" + voltReductionPointId + "]";
    }
}
