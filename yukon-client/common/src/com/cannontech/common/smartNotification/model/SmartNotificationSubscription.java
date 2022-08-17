package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SmartNotificationSubscription implements Serializable {
    private int id;
    private int userId;
    private SmartNotificationEventType type;
    private SmartNotificationMedia media;
    private SmartNotificationFrequency frequency;
    private String recipient;
    private SmartNotificationVerbosity verbosity;
    private Map<String, Object> parameters = new HashMap<>();
    private static final long serialVersionUID = 1L;
    
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public SmartNotificationEventType getType() {
        return type;
    }
    public void setType(SmartNotificationEventType type) {
        this.type = type;
    }
    public SmartNotificationMedia getMedia() {
        return media;
    }
    public void setMedia(SmartNotificationMedia media) {
        this.media = media;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public SmartNotificationVerbosity getVerbosity() {
        return verbosity;
    }
    public void setVerbosity(SmartNotificationVerbosity verbosity) {
        this.verbosity = verbosity;
    }
    public SmartNotificationFrequency getFrequency() {
        return frequency;
    }
    public void setFrequency(SmartNotificationFrequency frequency) {
        this.frequency = frequency;
    }
    public Map<String, Object> getParameters() {
        return parameters;
    }
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    public void addParameters(String name, Object value) {
        parameters.put(name, value);
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("id", id);
        tsb.append("userId", userId);
        tsb.append("type", type);
        tsb.append("media", media);
        tsb.append("frequency", frequency);
        tsb.append("recipient", recipient);
        tsb.append("verbosity", verbosity);
        tsb.append("parameters=", parameters);
        return tsb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SmartNotificationSubscription other = (SmartNotificationSubscription) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}
