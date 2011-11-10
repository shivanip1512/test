package com.cannontech.capcontrol.model;

public class BankMoveBean {

    int bankId;
    int oldFeederId; 
    int newFeederId;
    float displayOrder;
    float tripOrder;
    float closeOrder;
    
    public int getBankId() {
        return bankId;
    }
    
    public void setBankId(int bankId) {
        this.bankId = bankId;
    }
    
    public int getOldFeederId() {
        return oldFeederId;
    }
    
    public void setOldFeederId(int oldFeederId) {
        this.oldFeederId = oldFeederId;
    }
    
    public int getNewFeederId() {
        return newFeederId;
    }
    
    public void setNewFeederId(int newFeederId) {
        this.newFeederId = newFeederId;
    }
    
    public float getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(float displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public float getTripOrder() {
        return tripOrder;
    }
    
    public void setTripOrder(float tripOrder) {
        this.tripOrder = tripOrder;
    }
    
    public float getCloseOrder() {
        return closeOrder;
    }
    
    public void setCloseOrder(float closeOrder) {
        this.closeOrder = closeOrder;
    }
    
}