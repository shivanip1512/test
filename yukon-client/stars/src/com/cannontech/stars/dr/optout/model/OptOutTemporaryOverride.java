package com.cannontech.stars.dr.optout.model;

import org.joda.time.Instant;

public abstract class OptOutTemporaryOverride {
 
    private int optOutTemporaryOverrideId;
    private int userId;
    private int energyCompanyId;
    private Instant startDate;
    private Instant stopDate;
    private Integer assignedProgramId;  // This value can be null;
    
    public OptOutTemporaryOverride() {
    }
    
    public int getOptOutTemporaryOverrideId() {
        return optOutTemporaryOverrideId;
    }
    public void setOptOutTemporaryOverrideId(int optOutTemporaryOverrideId) {
        this.optOutTemporaryOverrideId = optOutTemporaryOverrideId;
    }
    
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getEnergyCompanyId() {
        return energyCompanyId;
    }
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public Instant getStartDate() {
        return startDate;
    }
    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }
    
    public Instant getStopDate() {
        return stopDate;
    }
    public void setStopDate(Instant stopDate) {
        this.stopDate = stopDate;
    }

    
    public Integer getAssignedProgramId() {
        return assignedProgramId;
    }
    public void setAssignedProgramId(Integer assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
    }
    
}