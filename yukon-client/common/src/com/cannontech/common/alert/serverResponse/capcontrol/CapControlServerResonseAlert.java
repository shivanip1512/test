package com.cannontech.common.alert.serverResponse.capcontrol;

import java.util.Date;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.user.checker.UserChecker;

public class CapControlServerResonseAlert implements Alert {
    private Date date;
    private ResolvableTemplate message;
    private UserChecker userChecker;

    public CapControlServerResonseAlert(Date date, ResolvableTemplate message) {
        super();
        this.date = date;
        this.message = message;
    }

    @Override
    public AlertType getType() {
        return AlertType.CAPCONTROL_SERVER_RESPONSE;
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
    
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("date", getDate());
        tsc.append("message", getMessage());
        return tsc.toString();
    }

}
