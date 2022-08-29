package com.cannontech.web.notificationGroup;

public class NotificationSettings {
    private Integer id;
    private String notification;
    private boolean emailEnabled;
    private boolean phoneCallEnabled;
    private boolean selected;

    public NotificationSettings() {
        super();
    }

    public NotificationSettings(int id) {
        super();
        this.id = id;
    }

    public NotificationSettings(int id, String notification, boolean emailEnabled, boolean phoneCallEnabled) {
        super();
        this.id = id;
        this.notification = notification;
        this.emailEnabled = emailEnabled;
        this.phoneCallEnabled = phoneCallEnabled;
    }

    public NotificationSettings(int id, boolean emailEnabled, boolean phoneCallEnabled) {
        super();
        this.id = id;
        this.emailEnabled = emailEnabled;
        this.phoneCallEnabled = phoneCallEnabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isPhoneCallEnabled() {
        return phoneCallEnabled;
    }

    public void setPhoneCallEnabled(boolean phoneCallEnabled) {
        this.phoneCallEnabled = phoneCallEnabled;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
