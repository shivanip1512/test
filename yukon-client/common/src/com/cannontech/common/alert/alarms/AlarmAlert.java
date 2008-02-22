package com.cannontech.common.alert.alarms;

import java.util.Date;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.user.checker.UserChecker;

public class AlarmAlert implements Alert {
    private Date date;
    private ResolvableTemplate message;
    private boolean unacknowledgedAlarm;
    private int pointId;
    private int condition;
    private UserChecker userChecker;

    public AlarmAlert(Date date, ResolvableTemplate message) {
        super();
        this.date = date;
        this.message = message;
    }

    @Override
    public AlertType getType() {
        return AlertType.ALARM;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public ResolvableTemplate getMessage() {
        return message;
    }

    @Override
    public UserChecker getUserCheck() {
        return userChecker;
    }
    
    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
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
