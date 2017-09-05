package com.cannontech.common.smartNotification.model;

public enum SmartNotificationFrequency {
    ALL, //every individual notification sent separately
    COALESCING, //Limit notifications by combining notifications of the same type that occur close together
    DAILY_DIGEST //A single notification is sent per notification type, which summarizes all notifications in the past 24 hours
    ;
}
