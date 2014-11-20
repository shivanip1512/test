package com.cannontech.web.login.model;

import com.cannontech.core.dao.impl.LoginStatusEnum;

public class Login {
    
    private int userId;
    private String userGroupName;
    private boolean loginEnabled;
    private String username;
    private String password1 = "";
    private String password2 = "";
    
    public String getUserGroupName() {
        return userGroupName;
    }
    
    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }
    
    public boolean isLoginEnabled() {
        return loginEnabled;
    }
    
    public void setLoginEnabled(boolean loginEnabled) {
        this.loginEnabled = loginEnabled;
    }
    
    public void setLoginEnabled(LoginStatusEnum loginStatus) {
        
        if (loginStatus.isEnabled()) {
            this.loginEnabled = true;
            return;
        }
        
        this.loginEnabled = false;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        
        if (username.equalsIgnoreCase("(none")) {
            this.username = null;
        } else {
            this.username = username;
        }
    }
    
    public String getPassword1() {
        return password1;
    }
    public void setPassword1(String password1) {
        this.password1 = password1;
    }
    
    public String getPassword2() {
        return password2;
    }
    
    public void setPassword2(String password2) {
        this.password2 = password2;
    }
    
    public LoginStatusEnum getLoginStatus() {
        return loginEnabled ? LoginStatusEnum.ENABLED : LoginStatusEnum.DISABLED;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    @Override
    public String toString() {
        return String.format("Login [userId=%s, userGroupName=%s, loginEnabled=%s, username=%s, " 
                + "password1=%s, password2=%s]", userId, userGroupName, loginEnabled, username, password1, password2);
    }
    
}