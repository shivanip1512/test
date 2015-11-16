package com.cannontech.web.admin.energyCompany.general.model;

import com.cannontech.common.model.Address;

public class GeneralInfo {
    
    private int ecId;
    private String name;
    private int defaultRouteId;
    private String phone;
    private String fax;
    private String email;
    private Address address = new Address();
    private Integer parentLogin;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getDefaultRouteId() {
        return defaultRouteId;
    }
    public void setDefaultRouteId(int defaultRouteId) {
        this.defaultRouteId = defaultRouteId;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    public void setEcId(int ecId) {
        this.ecId = ecId;
    }
    public int getEcId() {
        return ecId;
    }
    public Integer getParentLogin() {
        return parentLogin;
    }
    public void setParentLogin(Integer parentLogin) {
        this.parentLogin = parentLogin;
    }
    
}