package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName="DeviceId")
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
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), alarmInhibit, controlInhibit);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteDevice) {
            if (!super.equals(object)) 
                return false;
            CompleteDevice that = (CompleteDevice) object;
            return Objects.equal(this.alarmInhibit, that.alarmInhibit)
                && Objects.equal(this.controlInhibit, that.controlInhibit);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteDevice [alarmInhibit=" + alarmInhibit + ", controlInhibit="
               + controlInhibit + "]";
    }
}