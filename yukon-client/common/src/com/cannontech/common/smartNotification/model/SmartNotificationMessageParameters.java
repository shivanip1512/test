package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.ImmutableList;

/**
 * This object specifies everything that is needed to assemble a smart notification message.
 */
public class SmartNotificationMessageParameters implements Serializable {
    private final static long serialVersionUID = 1L;
    private final SmartNotificationEventType type;
    private final SmartNotificationMedia media;
    private final SmartNotificationVerbosity verbosity;
    private final List<String> recipients;
    private final List<SmartNotificationEvent> events;
    
    public SmartNotificationMessageParameters(SmartNotificationEventType type, SmartNotificationMedia media, 
                                              SmartNotificationVerbosity verbosity, Collection<String> recipients, 
                                              Collection<SmartNotificationEvent> events, int eventPeriodMinutes) {
        this.type = type;
        this.media = media;
        this.verbosity = verbosity;
        this.recipients = ImmutableList.copyOf(recipients);
        this.events = ImmutableList.copyOf(events);
    }

    public SmartNotificationEventType getType() {
        return type;
    }

    public SmartNotificationMedia getMedia() {
        return media;
    }

    public SmartNotificationVerbosity getVerbosity() {
        return verbosity;
    }
    
    /**
     * The value of these strings will vary with the media type. (Ex. for email, these are email addresses. For SMS,
     * phone numbers.)
     */
    public List<String> getRecipients() {
        return recipients;
    }
    
    /**
     * A list of all events contributing to this notification.
     */
    public List<SmartNotificationEvent> getEvents() {
        return events;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("type", type);
        tsb.append("media", media);
        tsb.append("verbosity", verbosity);
        tsb.append("recipients", recipients);
        tsb.append("events", events);
        return tsb.toString();
    }   
}
