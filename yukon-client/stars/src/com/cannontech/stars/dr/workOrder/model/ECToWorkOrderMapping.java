package com.cannontech.stars.dr.workOrder.model;

public class ECToWorkOrderMapping {
    private int energyCompanyId;
    private int workOrderId;
    
    public int getEnergyCompanyId() {
        return energyCompanyId;
    }
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public int getWorkOrderId() {
        return workOrderId;
    }
    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }
}