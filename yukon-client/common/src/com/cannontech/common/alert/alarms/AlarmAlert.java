package com.cannontech.common.alert.alarms;

import java.util.Date;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.util.ResolvableTemplate;

public class AlarmAlert extends BaseAlert {
    private boolean unacknowledgedAlarm;
    private int pointId;
    private int condition;
    public AlarmAlert(Date date, ResolvableTemplate message) {
        super(date, message);
    }

    @Override
    public AlertType getType() {
        return AlertType.ALARM;
    }

    public void setUnacknowledgedAlarm(final boolean isUnacknowledgedAlarm) {
        this.unacknowledgedAlarm = isUnacknowledgedAlarm;
    }
    
    public boolean isUnacknowledgedAlarm() {
        return unacknowledgedAlarm;
    }
    
    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getPointId() {
        return pointId;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }
    
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("date", getDate());
        tsc.append("message", getMessage());
        tsc.append("unacknowledgedAlarm", isUnacknowledgedAlarm());
        tsc.append("pointId", getPointId());
        tsc.append("condition", getCondition());
        return tsc.toString();
    }

}
