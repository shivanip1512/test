package com.cannontech.cbc.cyme.model;

import com.cannontech.core.dao.NotFoundException;

public enum CymeSimulationStatus{
    QUEUED("Queued"),
    ACTIVE("Active"),
    COMPELTED("Completed"),
    ERRORED("Errored");
    
    private String cymeValue;
    
    private CymeSimulationStatus(String cymeValue) {
        this.cymeValue = cymeValue;
    }
    
    public String getCymeValue() {
        return cymeValue;
    }
    
    public static CymeSimulationStatus getFromCymeValue(String cymeValue) {
        for(CymeSimulationStatus status : values()) {
            if(status.getCymeValue().equals(cymeValue)) {
                return status;
            }
        }
        throw new NotFoundException("CymeSimulationStatus was not found to match: " + cymeValue);
    }
}
