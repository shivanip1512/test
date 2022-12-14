package com.cannontech.common.user;

import org.apache.commons.lang3.Validate;

import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public class User {
    
    private Integer userId;
    private String username;
    private Password password = new Password();
    private AuthenticationCategory authCategory;
    private LoginStatusEnum loginStatus;
    private Integer userGroupId;
    private boolean authenticationChanged;
    private Integer energyCompanyId;
    
    public User() { }
    
    /** Create an instance for editing. */
    public User(LiteYukonUser yukonUser, UserAuthenticationInfo userAuthenticationInfo) {
        userId = yukonUser.getUserID();
        username = yukonUser.getUsername().trim();
        authCategory = userAuthenticationInfo.getAuthenticationCategory();
        loginStatus = yukonUser.getLoginStatus();
        userGroupId = yukonUser.getUserGroupId();
    }
    
    /**
     * Update the LiteYukonUser with values entered in the form, for saving to the database.  If the authentication
     * category has changed, the password will need to be updated.  This method sets the "authenticationChanged"
     * property to indicate this.
     * 
     * @return true if the authentication category was changed and the password needs to be updated.
     */
    public void updateForSave(LiteYukonUser yukonUser, UserAuthenticationInfo userAuthenticationInfo) {
        // This page doesn't allow creation so the user ids have to match.
        Validate.isTrue(userId == yukonUser.getUserID());
        yukonUser.setUsername(username);
        AuthenticationCategory oldAuthCategory = userAuthenticationInfo.getAuthenticationCategory();
        authenticationChanged = authCategory != oldAuthCategory;
        yukonUser.setLoginStatus(loginStatus);
        yukonUser.setUserGroupId(userGroupId);
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username.trim();
    }
    
    public Password getPassword() {
        return password;
    }
    
    public void setPassword(Password password) {
        this.password = password;
    }
    
    public AuthenticationCategory getAuthCategory() {
        return authCategory;
    }
    
    public void setAuthCategory(AuthenticationCategory authCategory) {
        this.authCategory = authCategory;
    }
    
    public LoginStatusEnum getLoginStatus() {
        return loginStatus;
    }
    
    public void setLoginStatus(LoginStatusEnum loginStatus) {
        this.loginStatus = loginStatus;
    }
    
    public Integer getUserGroupId() {
        return userGroupId;
    }
    
    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }
    
    public boolean isAuthenticationChanged() {
        return authenticationChanged;
    }
    
    public boolean isEnabled() {
        return loginStatus == LoginStatusEnum.ENABLED;
    }
    
    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }
    
    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
}