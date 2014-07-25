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
    
    /**
     * @param drName Name of the event to send to ecobee. Must be less than 12 characters.
     */
    public DutyCycleDrRequest(String setName, String drName, String eventDisplayMessage, int dutyCyclePercentage, Instant startDate, 
            boolean randomizeStartTime, Instant endDate, boolean randomizeEndTime) {
        selection = new Selection(SelectionType.MANAGEMENT_SET, "/" + setName);
        demandResponse = new DutyCycleDr(drName, eventDisplayMessage, dutyCyclePercentage, startDate, randomizeStartTime, endDate,
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((demandResponse == null) ? 0 : demandResponse.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((selection == null) ? 0 : selection.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DutyCycleDrRequest other = (DutyCycleDrRequest) obj;
        if (demandResponse == null) {
            if (other.demandResponse != null) {
                return false;
            }
        } else if (!demandResponse.equals(other.demandResponse)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (selection == null) {
            if (other.selection != null) {
                return false;
            }
        } else if (!selection.equals(other.selection)) {
            return false;
        }
        return true;
    }
}
