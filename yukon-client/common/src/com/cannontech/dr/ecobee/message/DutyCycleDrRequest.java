package com.cannontech.dr.ecobee.message;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.message.partial.DutyCycleDr;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;

public class DutyCycleDrRequest {
    private final String operation = "create";
    private final Selection selection;
    private final DutyCycleDr demandResponse;

    public DutyCycleDrRequest(String setName, String drName, int dutyCyclePercentage, Instant startDate, 
            boolean randomizeStartTime, Instant endDate, boolean randomizeEndTime) {
        this.selection = new Selection(SelectionType.MANAGEMENT_SET, "/" + setName);
        this.demandResponse = new DutyCycleDr(drName, "", dutyCyclePercentage, startDate, randomizeStartTime, endDate,
                                              randomizeEndTime);
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
