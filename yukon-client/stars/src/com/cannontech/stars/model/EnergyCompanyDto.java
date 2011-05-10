package com.cannontech.stars.model;

public class EnergyCompanyDto {
    
    private String name;
    private String email;
    private Integer primaryOperatorGroupId;
    private String operatorGroupIds;
    private String residentialGroupIds;
    private String adminUsername;
    private String adminPassword1;
    private String adminPassword2;
    private String admin2Username;
    private String admin2Password1;
    private String admin2Password2;
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
    
    public String getOperatorGroupIds() {
        return operatorGroupIds;
    }
    
    public void setOperatorGroupIds(String operatorGroupIds) {
        this.operatorGroupIds = operatorGroupIds;
    }
    
    public String getResidentialGroupIds() {
        return residentialGroupIds;
    }
    
    public void setResidentialGroupIds(String residentialGroupIds) {
        this.residentialGroupIds = residentialGroupIds;
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
    
    public String getAdmin2Username() {
        return admin2Username;
    }
    
    public void setAdmin2Username(String admin2Username) {
        this.admin2Username = admin2Username;
    }
    
    public String getAdmin2Password1() {
        return admin2Password1;
    }
    
    public void setAdmin2Password1(String admin2Password1) {
        this.admin2Password1 = admin2Password1;
    }
    
    public String getAdmin2Password2() {
        return admin2Password2;
    }
    
    public void setAdmin2Password2(String admin2Password2) {
        this.admin2Password2 = admin2Password2;
    }
    
    public int getDefaultRouteId() {
        return defaultRouteId;
    }
    
    public void setDefaultRouteId(int defaultRouteId) {
        this.defaultRouteId = defaultRouteId;
    }

    public Integer getPrimaryOperatorGroupId() {
        return primaryOperatorGroupId;
    }

    public void setPrimaryOperatorGroupId(Integer primaryOperatorGroupId) {
        this.primaryOperatorGroupId = primaryOperatorGroupId;
    }
    
}