package com.cannontech.common.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public class DesignationCodeDto implements DatabaseRepresentationSource {

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
    
    @Override
    public Object getDatabaseRepresentation() {
        return id;
    }
}
