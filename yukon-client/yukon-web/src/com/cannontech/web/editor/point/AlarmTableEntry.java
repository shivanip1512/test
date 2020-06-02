package com.cannontech.web.editor.point;

import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;
import com.cannontech.web.tools.points.model.AlarmState;

public class AlarmTableEntry {

    private AlarmState condition ;
    private String category ;
    private AlarmNotificationTypes notify ;

    public AlarmState getCondition() {
        return condition;
    }

    public AlarmNotificationTypes getNotify() {
        return notify;
    }

    public String getCategory() {
        return category;
    }

    public void setCondition(AlarmState string) {
        condition = string;
    }

    public void setNotify(AlarmNotificationTypes string) {
        notify = string;
    }

    public void setCategory(String string) {
        category = string;
    }
    
    public static AlarmTableEntry getDefaultAlarmTableEntry(AlarmState condition) {
        AlarmTableEntry alarmEntry = new AlarmTableEntry();
        alarmEntry.setCondition(condition);
        alarmEntry.setCategory("(none)");
        alarmEntry.setNotify(AlarmNotificationTypes.NONE);
        return alarmEntry;
    }

}
