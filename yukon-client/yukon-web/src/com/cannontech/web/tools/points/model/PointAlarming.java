package com.cannontech.web.tools.points.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.web.editor.point.AlarmTableEntry;

public class PointAlarming implements DBPersistentConverter<com.cannontech.database.db.point.PointAlarming> {

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

    @Override
    public void buildModel(com.cannontech.database.db.point.PointAlarming pointAlarming) {
        setNotificationGroupId(pointAlarming.getNotificationGroupID());
        setNotifyOnAck(pointAlarming.isNotifyOnAck());
        setNotifyOnClear(pointAlarming.isNotifyOnClear());
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.point.PointAlarming pointAlarming) {
        if (getNotificationGroupId() != null) {
            pointAlarming.setNotificationGroupID(getNotificationGroupId());
        }
        if (getNotifyOnAck() != null) {
            pointAlarming.setNotifyOnAck(getNotifyOnAck());
        }
        if (getNotifyOnClear() != null) {
            pointAlarming.setNotifyOnClear(getNotifyOnClear());
        }
    }
}
