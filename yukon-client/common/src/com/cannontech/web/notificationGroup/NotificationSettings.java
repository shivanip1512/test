package com.cannontech.web.notificationGroup;

public class NotificationSettings {
    private int id;
    private boolean emailEnabled;
    private boolean phoneCallEnabled;
    private boolean selected;

    public NotificationSettings() {
        super();
    }

    public NotificationSettings(int id, boolean emailEnabled, boolean phoneCallEnabled, boolean selected) {
        super();
        this.id = id;
        this.emailEnabled = emailEnabled;
        this.phoneCallEnabled = phoneCallEnabled;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
