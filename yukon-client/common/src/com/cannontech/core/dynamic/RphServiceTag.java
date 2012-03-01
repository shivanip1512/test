package com.cannontech.core.dynamic;

public class RphServiceTag {

    private int rphServiceTagId;
    private int changeId;
    private String serviceName;
    private String serviceNameRef;
    
    public RphServiceTag() {}
    public RphServiceTag(int changeId, String serviceName, String serviceNameRef) {
        this.changeId = changeId;
        this.serviceName = serviceName;
        this.serviceNameRef = serviceNameRef;
    }
    
    public int getRphServiceTagId() {
        return rphServiceTagId;
    }
    public void setRphServiceTagId(int rphServiceTagId) {
        this.rphServiceTagId = rphServiceTagId;
    }
    
    public int getChangeId() {
        return changeId;
    }
    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getServiceNameRef() {
        return serviceNameRef;
    }
    public void setServiceNameRef(String serviceNameRef) {
        this.serviceNameRef = serviceNameRef;
    }
}