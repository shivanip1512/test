package com.cannontech.database.db.customer;

public enum CICustomerPointType {
    // original types
    Demand("","Demand"),
    Settlement("","Settlement"),
    Baseline("","Baseline"),
    Curtailable("","Curtailable"),
    
    // isoc types
    ContractFrmDmd("kW","Contract Firm Demand"),
    AdvBuyThroughKw("kW", "Advanced Buy Through kW"),
    AdvBuyThrough$("$/kW", "Advanced Buy Through Price"),
    InterruptHours("hours", "Max Interrupt Hours"),
    MinimumNotice("minutes", "Minimum Notice"),
    ContractIntLoad("kW", "Contract Interruptible Load"),
    CurrentLoad("kW", "Current Load");
    
    private final String unit;
    private final String label;

    private CICustomerPointType(String unit, String label) {
        this.unit = unit;
        this.label = label;
    }
    
    public String getUnit() {
        return unit;
    }

    public String getLabel() {
        return label;
    }
}
