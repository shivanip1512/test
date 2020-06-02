package com.cannontech.web.tools.points.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.web.editor.point.AlarmTableEntry;

public class PointAlarming {

    private Integer notificationGroupId;
    private Boolean notifyOnAck;
    private Boolean notifyOnClear;
    private List<AlarmTableEntry> alarmTableList;

    public Integer getNotificationGroupId() {
        return notificationGroupId;
    }

    public void setNotificationGroupId(Integer notificationGroupId) {
        this.notificationGroupId = notificationGroupId;
    }

    public Boolean getNotifyOnAck() {
        return notifyOnAck;
    }

    public void setNotifyOnAck(Boolean notifyOnAck) {
        this.notifyOnAck = notifyOnAck;
    }

    public Boolean getNotifyOnClear() {
        return notifyOnClear;
    }

    public void setNotifyOnClear(Boolean notifyOnClear) {
        this.notifyOnClear = notifyOnClear;
    }

    public List<AlarmTableEntry> getAlarmTableList() {
        if (alarmTableList == null) {
            alarmTableList = new ArrayList<AlarmTableEntry>();
        }
        return alarmTableList;
    }

    public void setAlarmTableList(List<AlarmTableEntry> alarmTableList) {
        this.alarmTableList = alarmTableList;
    }

}
