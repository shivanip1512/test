package com.cannontech.database.db.customer;

public enum CICustomerPointType {

    // The UNIT values are restricted to 16 characters or less (CICustomerPointData.Type field)
    // The LABEL values are restricted to 32 characters or less (CICustomerPointData.OptionalLabel field)
    
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
    CurrentLoad("kW", "Current Load"),
    MinEventDuration("minutes", "Minimum Event Duration Minutes"), //0 and 1440 both mean no limit
    InterruptHrs24Hr("hours", "Max Interrupt Hours 24Hr Period"); //maximum number of hours controlled in 24 hour period
    
    
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
