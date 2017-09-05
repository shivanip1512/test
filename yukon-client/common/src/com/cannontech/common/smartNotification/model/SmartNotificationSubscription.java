package com.cannontech.common.smartNotification.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SmartNotificationSubscription {
    private int id;
    private int userId;
    private SmartNotificationEventType type;
    private SmartNotificationMedia media;
    private SmartNotificationFrequency frequency;
    private String recipient;
    private SmartNotificationVerbosity verbosity;
    private Map<String, Object> parameters = new HashMap<>();
    
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
}
