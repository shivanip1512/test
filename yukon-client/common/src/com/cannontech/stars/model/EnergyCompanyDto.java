package com.cannontech.stars.model;

public class EnergyCompanyDto {
    
    private String name;
    private String email;
    private Integer primaryOperatorUserGroupId;
    private String operatorUserGroupIds;
    private String residentialUserGroupIds;
    private String adminUsername;
    private String adminPassword1;
    private String adminPassword2;
    private int defaultRouteId;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getOperatorUserGroupIds() {
        return operatorUserGroupIds;
    }
    public void setOperatorUserGroupIds(String operatorUserGroupIds) {
        this.operatorUserGroupIds = operatorUserGroupIds;
    }

    public String getResidentialUserGroupIds() {
        return residentialUserGroupIds;
    }
    public void setResidentialUserGroupIds(String residentialUserGroupIds) {
        this.residentialUserGroupIds = residentialUserGroupIds;
    }

    public String getAdminUsername() {
        return adminUsername;
    }
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
    
    public String getAdminPassword1() {
        return adminPassword1;
    }
    public void setAdminPassword1(String adminPassword1) {
        this.adminPassword1 = adminPassword1;
    }
    
    public String getAdminPassword2() {
        return adminPassword2;
    }
    public void setAdminPassword2(String adminPassword2) {
        this.adminPassword2 = adminPassword2;
    }

    public int getDefaultRouteId() {
        return defaultRouteId;
    }
    public void setDefaultRouteId(int defaultRouteId) {
        this.defaultRouteId = defaultRouteId;
    }
    
    public Integer getPrimaryOperatorUserGroupId() {
        return primaryOperatorUserGroupId;
    }
    public void setPrimaryOperatorUserGroupId(Integer primaryOperatorUserGroupId) {
        this.primaryOperatorUserGroupId = primaryOperatorUserGroupId;
    }
}