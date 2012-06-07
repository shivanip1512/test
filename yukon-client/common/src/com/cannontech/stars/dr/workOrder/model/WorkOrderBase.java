package com.cannontech.stars.dr.workOrder.model;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

public class WorkOrderBase{

    private int accountId;
    private int energyCompanyId;
    private int orderId;
    private String orderNumber = "";
    private int workTypeId;
    private int currentStateId;
    private int serviceCompanyId;
    private String orderedBy = "";
    private String description = "";
    private Instant dateReported;
    private Instant dateScheduled;
    private Instant dateCompleted;
    private String actionTaken = "";
    private String additionalOrderNumber = "";

    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public int getWorkTypeId() {
        return workTypeId;
    }
    public void setWorkTypeId(int workTypeId) {
        this.workTypeId = workTypeId;
    }
    
    public int getCurrentStateId() {
        return currentStateId;
    }
    public void setCurrentStateId(int currentStateId) {
        this.currentStateId = currentStateId;
    }
    
    public int getServiceCompanyId() {
        return serviceCompanyId;
    }
    public void setServiceCompanyId(int serviceCompanyId) {
        this.serviceCompanyId = serviceCompanyId;
    }
    
    public Instant getDateReported() {
        return dateReported;
    }
    public void setDateReported(Instant dateReported) {
        this.dateReported = dateReported;
    }
    
    public String getOrderedBy() {
        return orderedBy;
    }
    public void setOrderedBy(String orderedBy) {
        this.orderedBy = orderedBy;
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Instant getDateScheduled() {
        return dateScheduled;
    }
    public void setDateScheduled(ReadableInstant dateScheduled) {
        if (dateScheduled != null)
            this.dateScheduled = dateScheduled.toInstant();
    }

    public Instant getDateCompleted() {
        return dateCompleted;
    }
    public void setDateCompleted(ReadableInstant dateCompleted) {
        if (dateScheduled != null)
            this.dateCompleted = dateCompleted.toInstant();
    }

    public String getActionTaken() {
        return actionTaken;
    }
    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public int getAccountId() {
        return accountId;
    }
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAdditionalOrderNumber() {
        return additionalOrderNumber;
    }
    public void setAdditionalOrderNumber(String additionalOrderNumber) {
        this.additionalOrderNumber = additionalOrderNumber;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
}