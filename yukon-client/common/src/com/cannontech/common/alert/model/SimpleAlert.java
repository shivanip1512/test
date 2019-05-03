package com.cannontech.common.alert.model;

import java.io.Serializable;
import java.util.Date;

import com.cannontech.common.util.ResolvableTemplate;

public class SimpleAlert extends BaseAlert implements Serializable {

    private AlertType alertType;
    
    public SimpleAlert(AlertType alertType, ResolvableTemplate message) {
        super(new Date(), message);
        this.alertType = alertType;
    }
    
    public SimpleAlert(AlertType alertType, Date date, ResolvableTemplate message) {
        super(date, message);
        this.alertType = alertType;
    }
    
    @Override
    public AlertType getType() {
        return this.alertType;
    }
}
