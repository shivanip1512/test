package com.cannontech.common.inventory;

import java.util.Date;

/* History Wrapper */
public class HardwareHistory {
    private String action;
    private Date date;
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
}