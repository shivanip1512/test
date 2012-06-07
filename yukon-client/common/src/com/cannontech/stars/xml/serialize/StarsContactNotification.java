package com.cannontech.stars.xml.serialize;

public abstract class StarsContactNotification {
    private int notifCatID;
    private boolean hasNotifCatID;
    private boolean disabled;
    private boolean hasDisabled;
    private String notification;

    public StarsContactNotification() {
    
    }

    public void deleteDisabled() {
        this.hasDisabled = false;
    } 

    public void deleteNotifCatID() {
        this.hasNotifCatID = false;
    } 

    public boolean getDisabled() {
        return this.disabled;
    } 

    public int getNotifCatID() {
        return this.notifCatID;
    } 

    public String getNotification() {
        return this.notification;
    } 

    public boolean hasDisabled() {
        return this.hasDisabled;
    } 

    public boolean hasNotifCatID() {
        return this.hasNotifCatID;
    } 

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        this.hasDisabled = true;
    } 

    public void setNotifCatID(int notifCatID) {
        this.notifCatID = notifCatID;
        this.hasNotifCatID = true;
    } 

    public void setNotification(java.lang.String notification) {
        this.notification = notification;
    } 

}
