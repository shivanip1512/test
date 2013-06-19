package com.cannontech.core.users.model;

import java.io.Serializable;

public class YukonUserPreference implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private Integer userId;
    private YukonUserPreferenceName name;
    private String value;

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public YukonUserPreferenceName getName() {
        return name;
    }
    
    public void setName(YukonUserPreferenceName name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

}