package com.cannontech.web.dr.model;

import java.util.List;

public class RfPerformanceSettings {
    
    private int time;
    private boolean email;
    private List<Integer> notifGroupIds;
    
    public int getTime() {
        return time;
    }
    
    public void setTime(int time) {
        this.time = time;
    }
    
    public boolean isEmail() {
        return email;
    }
    
    public void setEmail(boolean email) {
        this.email = email;
    }
    
    public List<Integer> getNotifGroupIds() {
        return notifGroupIds;
    }
    
    public void setNotifGroupIds(List<Integer> notifGroupIds) {
        this.notifGroupIds = notifGroupIds;
    }
    
    @Override
    public String toString() {
        return String.format("RfBroadcastPerformanceSettings [time=%s, email=%s, notifGroupIds=%s]", time, email, notifGroupIds);
    }
    
}