package com.cannontech.core.users.model;

import java.io.Serializable;

public class UserPreference implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private Integer userId;
    private UserPreferenceName name;
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
    
    public UserPreferenceName getName() {
        return name;
    }
    
    public void setName(UserPreferenceName name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

}