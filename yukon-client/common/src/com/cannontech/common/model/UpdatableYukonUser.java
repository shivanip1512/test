package com.cannontech.common.model;

import com.cannontech.database.data.lite.LiteYukonUser;

public class UpdatableYukonUser extends LiteYukonUser{

    private String password;
    private String confirmPassword;
    private Integer energyCompanyId;
    
    public UpdatableYukonUser() {}
    
    public UpdatableYukonUser(LiteYukonUser user) {
        setUserID(user.getUserID());
        setUsername(user.getUsername());
        setAuthType(user.getAuthType());
        setLoginStatus(user.getLoginStatus());
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }

}