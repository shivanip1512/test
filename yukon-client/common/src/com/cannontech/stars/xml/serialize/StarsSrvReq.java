package com.cannontech.stars.xml.serialize;

import java.util.Date;

public abstract class StarsSrvReq {
    private int _orderID;
    private boolean _has_orderID;
    private int _accountID;
    private boolean _has_accountID;
    private java.lang.String _orderNumber;
    private ServiceType _serviceType;
    private java.util.Date _dateReported;
    private ServiceCompany _serviceCompany;
    private java.lang.String _orderedBy;
    private java.lang.String _description;
    private CurrentState _currentState;
    private java.util.Date _dateScheduled;
    private java.util.Date _dateCompleted;
    private java.lang.String _actionTaken;
    private String _addtlOrderNumber;

    public StarsSrvReq() {
        
    }

    public void deleteAccountID() {
        this._has_accountID = false;
    } 

    public void deleteOrderID() {
        this._has_orderID = false;
    } 

    public int getAccountID() {
        return this._accountID;
    }

    public String getActionTaken() {
        return this._actionTaken;
    }

    public CurrentState getCurrentState() {
        return this._currentState;
    } 

    public Date getDateCompleted() {
        return this._dateCompleted;
    }

    public Date getDateReported() {
        return this._dateReported;
    }

    public Date getDateScheduled() {
        return this._dateScheduled;
    }

    public String getDescription() {
        return this._description;
    }

    public int getOrderID() {
        return this._orderID;
    }

    public String getOrderNumber() {
        return this._orderNumber;
    }

    public String getAddtlOrderNumber() {
        return this._addtlOrderNumber;
    }

    public String getOrderedBy() {
        return this._orderedBy;
    }

    public ServiceCompany getServiceCompany() {
        return this._serviceCompany;
    } 

    public ServiceType getServiceType() {
        return this._serviceType;
    } 

    public boolean hasAccountID() {
        return this._has_accountID;
    }

    public boolean hasOrderID() {
        return this._has_orderID;
    }

    public void setAccountID(int accountID) {
        this._accountID = accountID;
        this._has_accountID = true;
    }

    public void setActionTaken(String actionTaken) {
        this._actionTaken = actionTaken;
    }

    public void setCurrentState(CurrentState currentState) {
        this._currentState = currentState;
    } 

    public void setDateCompleted(Date dateCompleted) {
        this._dateCompleted = dateCompleted;
    }

    public void setDateReported(Date dateReported) {
        this._dateReported = dateReported;
    }

    public void setDateScheduled(Date dateScheduled) {
        this._dateScheduled = dateScheduled;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public void setOrderID(int orderID) {
        this._orderID = orderID;
        this._has_orderID = true;
    }

    public void setOrderNumber(String orderNumber) {
        this._orderNumber = orderNumber;
    }

    public void setAddtlOrderNumber(String addtlOrderNumber) {
        this._addtlOrderNumber = addtlOrderNumber;
    }
    
    public void setOrderedBy(String orderedBy) {
        this._orderedBy = orderedBy;
    } 

    public void setServiceCompany(ServiceCompany serviceCompany) {
        this._serviceCompany = serviceCompany;
    } 

    public void setServiceType(ServiceType serviceType) {
        this._serviceType = serviceType;
    }

}
