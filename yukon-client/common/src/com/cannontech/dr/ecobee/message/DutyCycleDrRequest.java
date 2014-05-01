package com.cannontech.dr.ecobee.message;

public class DutyCycleDrRequest {
    private final String operation = "create";
    private final Selection selection;
    private final DutyCycleDr demandResponse;
    
    public DutyCycleDrRequest(String setName, String drName, int dutyCyclePercentage, String startDate, 
            String startTime, boolean randomizeStartTime, String endDate, String endTime, boolean randomizeEndTime) {
        this.selection = new Selection("managementSet", "/" + setName);
        this.demandResponse = new DutyCycleDr(drName, "", dutyCyclePercentage, startDate, startTime, 
                                                          randomizeStartTime, endDate, endTime, randomizeEndTime);
    }
    
    public Selection getSelection() {
        return selection;
    }
    
    public DutyCycleDr getDemandResponse() {
        return demandResponse;
    }
    
    public String getOperation() {
        return operation;
    }
}
