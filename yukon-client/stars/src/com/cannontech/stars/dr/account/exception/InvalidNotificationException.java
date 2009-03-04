package com.cannontech.stars.dr.account.exception;

public class InvalidNotificationException extends IllegalArgumentException {
    private String notifCategory;
    private String notificationText;

    public InvalidNotificationException(String notifCategory,
            String notificationText) {
        this.notifCategory = notifCategory;
        this.notificationText = notificationText;
    }

    public String getNotifCategory() {
        return notifCategory;
    }

    public String getNotificationText() {
        return notificationText;
    }
}