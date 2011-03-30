package com.cannontech.web.stars.dr.operator.model;

import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LoginBackingBean {
    
    private int userId;
    private String loginGroupName;
    private boolean loginEnabled;
    private String username;
    private String password1;
    private String password2;
    private AuthType authType;
    
    public String getLoginGroupName() {
        return loginGroupName;
    }
    
    public void setLoginGroupName(String loginGroupName) {
        this.loginGroupName = loginGroupName;
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

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public AuthType getAuthType() {
        return authType;
    }
    
}