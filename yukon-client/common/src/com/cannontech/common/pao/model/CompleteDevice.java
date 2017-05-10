package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName = "DeviceId", paoTypes = { PaoType.ECOBEE_SMART_SI, PaoType.ECOBEE_3, PaoType.ECOBEE_SMART, PaoType.ECOBEE_3_LITE, PaoType.VIRTUAL_SYSTEM })
public class CompleteDevice extends CompleteYukonPao {
    private boolean alarmInhibit = false;
    private boolean controlInhibit = false;

    @YukonPaoField
    public boolean isAlarmInhibit() {
        return alarmInhibit;
    }

    public void setAlarmInhibit(boolean alarmInhibit) {
        this.alarmInhibit = alarmInhibit;
    }

    @YukonPaoField
    public boolean isControlInhibit() {
        return controlInhibit;
    }

    public void setControlInhibit(boolean controlInhibit) {
        this.controlInhibit = controlInhibit;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), alarmInhibit, controlInhibit);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteDevice) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteDevice that = (CompleteDevice) object;
            return alarmInhibit == that.alarmInhibit && controlInhibit == that.controlInhibit;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteDevice [alarmInhibit=" + alarmInhibit + ", controlInhibit="
            + controlInhibit + "]";
    }
}
