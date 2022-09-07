package com.cannontech.web.api.passwordPolicy;

import com.cannontech.core.authentication.model.PasswordPolicy;

public class ChangePasswordResponse {
    private PasswordPolicy passwordPolicy;
    private Integer userId;
    private String uuid;

    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
