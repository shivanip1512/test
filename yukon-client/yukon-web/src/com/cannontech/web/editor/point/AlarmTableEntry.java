package com.cannontech.web.editor.point;

import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;

public class AlarmTableEntry {

    private String condition ;
    private String category ;
    private AlarmNotificationTypes notify ;

    public AlarmTableEntry() {}

    public AlarmTableEntry(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public AlarmNotificationTypes getNotify() {
        return notify;
    }

    public String getCategory() {
        return category;
    }

    public void setCondition(String string) {
        condition = string;
    }

    public void setNotify(AlarmNotificationTypes string) {
        notify = string;
    }

    public void setCategory(String string) {
        category = string;
    }

}
