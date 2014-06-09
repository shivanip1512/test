package com.cannontech.dr.ecobee.message;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.message.partial.DutyCycleDr;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DutyCycleDrRequest {
    private final String operation = "create";
    private final Selection selection;
    private final DutyCycleDr demandResponse;

    public DutyCycleDrRequest(String setName, String drName, int dutyCyclePercentage, Instant startDate, 
            boolean randomizeStartTime, Instant endDate, boolean randomizeEndTime) {
        selection = new Selection(SelectionType.MANAGEMENT_SET, "/" + setName);
        demandResponse = new DutyCycleDr(drName, "", dutyCyclePercentage, startDate, randomizeStartTime, endDate,
                                              randomizeEndTime);
    }

    @JsonCreator
    public DutyCycleDrRequest(@JsonProperty("selection") Selection selection,
            @JsonProperty("demandResponse") DutyCycleDr demandResponse) {
        this.selection = selection;
        this.demandResponse = demandResponse;
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
