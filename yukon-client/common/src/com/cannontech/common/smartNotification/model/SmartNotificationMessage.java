package com.cannontech.common.smartNotification.model;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Sent by the Smart Notification assemblers to the Yukon Notification service to send an actual notification message.
 */
public class SmartNotificationMessage {
    private final SmartNotificationMedia media;
    private final String subjectText;
    private final String messageText;
    private final List<String> recipients;
    
    public SmartNotificationMessage(SmartNotificationMedia media, String subjectText, String messageText, 
                                    Collection<String> recipients) {
        this.media = media;
        this.subjectText = subjectText;
        this.messageText = messageText;
        this.recipients = ImmutableList.copyOf(recipients);
    }

    public SmartNotificationMedia getMedia() {
        return media;
    }

    public String getSubjectText() {
        return subjectText;
    }

    public String getMessageText() {
        return messageText;
    }

    public List<String> getRecipients() {
        return recipients;
    }
    
}
