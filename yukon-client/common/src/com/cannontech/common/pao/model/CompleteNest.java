package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName = "deviceId", paoTypes = { PaoType.NEST })
public class CompleteNest extends CompleteDevice {
    private String serialNumber;

    @YukonPaoField
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), serialNumber);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteNest) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteNest that = (CompleteNest) object;
            return Objects.equal(serialNumber, that.serialNumber);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteNest [ serialNumber=" + serialNumber + "]";
    }
}
