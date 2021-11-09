package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;

/**
 * This object specifies everything that is needed to assemble a smart notification message.
 */
public class SmartNotificationMessageParameters implements Serializable {

    public enum ProcessingType {
        START_UP,
        DIGEST,
        IMMEDIATE,
        ON_INTERVAL
    }
    
    private final static long serialVersionUID = 1L;
    private final SmartNotificationEventType type;
    private final SmartNotificationMedia media;
    private final SmartNotificationVerbosity verbosity;
    private final List<String> recipients;
    private final List<SmartNotificationEvent> events;
    private final ProcessingType processingType;
    private String duplicateRemovalDebugString;
    
    public SmartNotificationMessageParameters(SmartNotificationEventType type, SmartNotificationMedia media, 
                                              SmartNotificationVerbosity verbosity, Collection<String> recipients, 
                                              Collection<SmartNotificationEvent> events, ProcessingType processingType) {
        this.type = type;
        this.media = media;
        this.verbosity = verbosity;
        //remove duplicates, 2 identical subscriptions
        this.recipients = ImmutableList.copyOf(recipients.stream()
                .distinct()
                .collect(Collectors.toList()));
        if(this.recipients.size() != recipients.size()) {
            duplicateRemovalDebugString = "before:"+recipients+" after:"+ this.getRecipients();
        }
        this.events = ImmutableList.copyOf(events);
        this.processingType = processingType;
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
    
    /**
     * Used for logging
     */
    public ProcessingType getProcessingType() {
        return processingType;
    }   
    
    @Override
    public String toString() {
        ToStringBuilder tsb =  getLogMsg();
        tsb.append("events", events);
        return tsb.toString();
    }
    
    public String loggingString(Level level) {
        if (level.isMoreSpecificThan(Level.INFO)) {
            return toString();
        } else {
            return getLogMsg().toString();
        }
    }
    
    private ToStringBuilder getLogMsg() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("type", type);
        tsb.append("media", media);
        tsb.append("verbosity", verbosity);
        tsb.append("recipients", recipients);
        tsb.append("events total", events.size());
        tsb.append("processing type", processingType);
        if (duplicateRemovalDebugString != null) {
            tsb.append("duplicate email removal", duplicateRemovalDebugString);
        }
        return tsb;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        result = prime * result + ((media == null) ? 0 : media.hashCode());
        result = prime * result + ((processingType == null) ? 0 : processingType.hashCode());
        result = prime * result + ((recipients == null) ? 0 : recipients.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((verbosity == null) ? 0 : verbosity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SmartNotificationMessageParameters other = (SmartNotificationMessageParameters) obj;
        if (events == null) {
            if (other.events != null)
                return false;
        } else if (!events.equals(other.events))
            return false;
        if (media != other.media)
            return false;
        if (processingType != other.processingType)
            return false;
        if (recipients == null) {
            if (other.recipients != null)
                return false;
        } else if (!recipients.equals(other.recipients))
            return false;
        if (type != other.type)
            return false;
        if (verbosity != other.verbosity)
            return false;
        return true;
    }
}
