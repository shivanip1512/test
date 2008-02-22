package com.cannontech.common.alert.model;

import java.util.Date;

public class UserAlert {
    private AlertType type;
    private Date date;
    private String message;

    public UserAlert(AlertType type, Date date, String message) {
        this.type = type;
        this.date = date;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    public Date getDate() {
        return date;
    }
    
    public AlertType getType() {
        return type;
    }
}
