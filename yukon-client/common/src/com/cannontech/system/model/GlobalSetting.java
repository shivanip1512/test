package com.cannontech.system.model;

import org.joda.time.Instant;

import com.cannontech.system.GlobalSettingType;

public class GlobalSetting {

    private Integer id;  // Used Integer versus int since settings can be missing from db
    private GlobalSettingType type;
    private Object value;
    private String comments;
    private Instant lastChanged;
    
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

}