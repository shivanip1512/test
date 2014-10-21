package com.cannontech.common.rfn.model;


public class GatewaySettings {
    
    private Integer id;
    private String name;
    private String ipAddress;
    private String adminUsername;
    private String adminPassword;
    private String superAdminUsername;
    private String superAdminPassword;
    private boolean adminDefault = true;
    private Double latitude;
    private Double longitude;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getSuperAdminUsername() {
        return superAdminUsername;
    }

    public void setSuperAdminUsername(String superAdminUsername) {
        this.superAdminUsername = superAdminUsername;
    }

    public String getSuperAdminPassword() {
        return superAdminPassword;
    }

    public void setSuperAdminPassword(String superAdminPassword) {
        this.superAdminPassword = superAdminPassword;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public boolean isAdminDefault() {
        return adminDefault;
    }
    
    public void setAdminDefault(boolean adminDefault) {
        this.adminDefault = adminDefault;
    }
    
}