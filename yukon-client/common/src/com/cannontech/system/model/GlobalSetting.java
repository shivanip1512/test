package com.cannontech.system.model;

import org.joda.time.Instant;

import com.cannontech.system.GlobalSettingType;

public class GlobalSetting {

    private Integer id;  // Used Integer versus int since settings can be missing from db
    private GlobalSettingType type;
    private Object value;
    private String comments;
    private Instant lastChanged;
    private Boolean decryptValueFailed;

    public GlobalSetting(GlobalSetting other) {
        this.comments = other.getComments();
        this.id = other.getId();
        this.lastChanged = other.getLastChanged();
        this.type = other.getType();
        this.value = other.getValue();
        this.decryptValueFailed = other.getDecryptValueFailed();
    }

    public GlobalSetting(GlobalSettingType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public GlobalSettingType getType() {
        return type;
    }
    
    public void setType(GlobalSettingType type) {
        this.type = type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public Instant getLastChanged() {
        return lastChanged;
    }
    
    public void setLastChanged(Instant lastChanged) {
        this.lastChanged = lastChanged;
    }
    
    public boolean isNonDefault() {
        if (value == null) {
            return type.getDefaultValue() != null;
        }
        return !value.equals(type.getDefaultValue());
    }

    public boolean isSensitiveInformation() {
        return type.isSensitiveInformation();
    }

    public boolean isNonViewableSensitiveInformation() {
        return type.isNonViewableSensitiveInformation();
    }

    public Boolean getDecryptValueFailed() {
        return decryptValueFailed;
    }

    public void setDecryptValueFailed(Boolean decryptValueFailed) {
        this.decryptValueFailed = decryptValueFailed;
    }

    @Override
    public String toString() {
        return "GlobalSetting [type=" + type + ", value=" + value + ", comments=" + comments + ", decryptValueFailed=" + decryptValueFailed + "]";
    }
}