package com.cannontech.web.editor.point;

import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;
import com.cannontech.web.tools.points.model.AlarmState;

public class AlarmTableEntry {

    private AlarmState condition ;
    private String category ;
    private AlarmNotificationTypes notify ;

    public AlarmTableEntry() {}

    public AlarmTableEntry(AlarmState condition) {
        this.condition = condition;
    }

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

}
