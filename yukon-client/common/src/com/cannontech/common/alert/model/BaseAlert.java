package com.cannontech.common.alert.model;

import java.io.Serializable;
import java.util.Date;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;

public abstract class BaseAlert implements Alert, Serializable {

    private Date date;
    private UserChecker userChecker = NullUserChecker.getInstance();
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
