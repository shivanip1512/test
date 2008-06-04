package com.cannontech.common.alert.model;

import java.util.Date;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;

public abstract class BaseAlert implements Alert {

    private Date date;
    private UserChecker userChecker = new NullUserChecker();
    private ResolvableTemplate message;

    public BaseAlert(Date date, ResolvableTemplate message) {
        this.date = date;
        this.message = message;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public UserChecker getUserCheck() {
        return userChecker;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }

    @Override
    public ResolvableTemplate getMessage() {
        return message;
    }

}
