package com.cannontech.common.model;

public class DesignationCodeDto {

    private int id = 0; //primary key
    private String value;
    private int serviceCompanyId; //foreign key
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public int getServiceCompanyId() {
        return serviceCompanyId;
    }
    public void setServiceCompanyId(int serviceCompanyId) {
        this.serviceCompanyId = serviceCompanyId;
    }
    
    public String toString() {
        return this.id + ", '" + this.value + "', " + this.serviceCompanyId;
    }
}
