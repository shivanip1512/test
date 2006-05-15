package com.cannontech.database.db.customer;

public enum CICustomerPointType {
    // original types
    Demand(""),
    Settlement(""),
    Baseline(""),
    Curtailable(""),
    
    // isoc types
    ContractFrmDmd("kW"),
    AdvBuyThroughKw("kW"),
    AdvBuyThrough$("$/kW"),
    InterruptHours("hours"),
    MinimumNotice("minutes"),
    ContractIntLoad("kW"),
    CurrentLoad("kW");
    
    private final String unit;

    private CICustomerPointType(String unit) {
        this.unit = unit;
    }
    
    public String getUnit() {
        return unit;
    }
}
