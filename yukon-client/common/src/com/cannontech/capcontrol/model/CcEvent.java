package com.cannontech.capcontrol.model;

import java.util.Date;

public class CcEvent {
    
    private Date dateTime;
    private String text;
    
    public CcEvent() {
        
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
