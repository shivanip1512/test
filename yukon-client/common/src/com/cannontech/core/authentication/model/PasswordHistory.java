package com.cannontech.core.authentication.model;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

public class PasswordHistory {
    private int passwordHistoryId;
    private int userId;
    private String password;
    private AuthType authType;
    private Instant passwordChangedDate;
    
    public PasswordHistory () {}
    public PasswordHistory(int userId, String password, AuthType authType, Instant passwordChangedDate) {
        this.userId = userId;
        this.password = password;
        this.authType = authType;
        this.passwordChangedDate = passwordChangedDate;
    }

    public int getPasswordHistoryId() {
        return passwordHistoryId;
    }
    public void setPasswordHistoryId(int passwordHistoryId) {
        this.passwordHistoryId = passwordHistoryId;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public AuthType getAuthType() {
        return authType;
    }
    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }
    
    public Instant getPasswordChangedDate() {
        return passwordChangedDate;
    }
    public void setPasswordChangedDate(Instant passwordChangedDate) {
        this.passwordChangedDate = passwordChangedDate;
    }
    public void setPasswordChangedDate(ReadableInstant passwordChangedDate) {
        this.passwordChangedDate = passwordChangedDate.toInstant();
    }
}