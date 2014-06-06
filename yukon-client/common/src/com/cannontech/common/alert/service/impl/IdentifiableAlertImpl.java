package com.cannontech.common.alert.service.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.user.checker.UserChecker;

public class IdentifiableAlertImpl implements IdentifiableAlert {
    
    private static final AtomicInteger source = new AtomicInteger(1);
    private final Alert alert;
    private int id;
    private long addedTimestamp;

    public long getAddedTimestamp() {
        return addedTimestamp;
    }

    public void setAddedTimestamp(long addedTimestamp) {
        this.addedTimestamp = addedTimestamp;
    }

    public IdentifiableAlertImpl(Alert alert) {
        this.alert = alert;
        id = source.getAndIncrement();
    }

    @Override
    public int getId() {
        return id;
    }

    public Alert getAlert() {
        return alert;
    }
    
    @Override
    public Date getDate() {
        return alert.getDate();
    }

    @Override
    public ResolvableTemplate getMessage() {
        return alert.getMessage();
    }

    @Override
    public UserChecker getUserCheck() {
        return alert.getUserCheck();
    }
    
    @Override
    public AlertType getType() {
        return alert.getType();
    }
    
    public String getIcon() {
        if (alert.getType() == AlertType.ALARM) {
            return "icon-exclamation";
        } else if (alert.getType() == AlertType.WARNING) {
            return "icon-error";
        } else {
            return "icon-information";
        }
    }

}
