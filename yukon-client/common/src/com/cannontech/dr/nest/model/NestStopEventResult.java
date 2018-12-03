package com.cannontech.dr.nest.model;

public class NestStopEventResult {
    private boolean isStopPossible;
    private boolean isSuccess;
    private String nestResponse;
    
    public boolean isStopPossible() {
        return isStopPossible;
    }
    public void setStopPossible(boolean isStopPossible) {
        this.isStopPossible = isStopPossible;
    }
    public boolean isSuccess() {
        return isSuccess;
    }
    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getNestResponse() {
        return nestResponse;
    }
    public void setNestResponse(String nestResponse) {
        this.nestResponse = nestResponse;
    }
}
