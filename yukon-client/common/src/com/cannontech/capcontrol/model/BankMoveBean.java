package com.cannontech.capcontrol.model;

public class BankMoveBean {

    int bankId;
    int oldFeederId; 
    int newFeederId;
    double displayOrder;
    double tripOrder;
    double closeOrder;
    
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
    
    public double getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(double displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public double getTripOrder() {
        return tripOrder;
    }
    
    public void setTripOrder(double tripOrder) {
        this.tripOrder = tripOrder;
    }
    
    public double getCloseOrder() {
        return closeOrder;
    }
    
    public void setCloseOrder(double closeOrder) {
        this.closeOrder = closeOrder;
    }
    
}