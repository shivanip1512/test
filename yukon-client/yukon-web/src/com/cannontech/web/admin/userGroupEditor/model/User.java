package com.cannontech.web.admin.userGroupEditor.model;

import org.apache.commons.lang.Validate;

import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public class User {
    private int userId;
    private String username;
    private Password password = new Password();
    private AuthenticationCategory authCategory;
    private LoginStatusEnum loginStatus;
    private int userGroupId;
    private boolean authenticationChanged;

    public User() {
    }

    /**
     * Create an instance for editing.
     * 
     * Use of default scope is intentional.
     */
    public User(LiteYukonUser yukonUser) {
        userId = yukonUser.getUserID();
        username = yukonUser.getUsername();
        authCategory = AuthenticationCategory.getByAuthType(yukonUser.getAuthType());
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
    public void updateForSave(LiteYukonUser yukonUser) {
        // This page doesn't allow creation so the user ids have to match.
        Validate.isTrue(userId == yukonUser.getUserID());
        yukonUser.setUsername(username);
        AuthenticationCategory oldAuthCategory = AuthenticationCategory.getByAuthType(yukonUser.getAuthType());
        authenticationChanged = authCategory != oldAuthCategory;
        if (authenticationChanged) {
            yukonUser.setAuthType(authCategory.getSupportingAuthType());
        }
        yukonUser.setLoginStatus(loginStatus);
        yukonUser.setUserGroupId(userGroupId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(int userGroupId) {
        this.userGroupId = userGroupId;
    }

    public boolean isAuthenticationChanged() {
        return authenticationChanged;
    }
}