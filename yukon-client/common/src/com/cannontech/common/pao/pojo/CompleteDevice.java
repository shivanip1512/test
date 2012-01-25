package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(tableName="Device", idColumnName="DeviceId")
public class CompleteDevice extends CompleteYukonPaObject {
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
    public String toString() {
        return "CompleteDevice [alarmInhibit=" + alarmInhibit + ", controlInhibit="
               + controlInhibit + "]";
    }
}
