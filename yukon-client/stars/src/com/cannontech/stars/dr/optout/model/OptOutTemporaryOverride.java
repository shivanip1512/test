package com.cannontech.stars.dr.optout.model;

import org.joda.time.Instant;

import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideType;

public class OptOutTemporaryOverride {
 
    private int optOutTemporaryOverrideId;
    private int userId;
    private int energyCompanyId;
    private OptOutTemporaryOverrideType optOutType;
    private Instant startDate;
    private Instant stopDate;
    private int optOutValue;
    private Integer assignedProgramId;  // This value can be null;
    
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
    
    public OptOutTemporaryOverrideType getOptOutType() {
        return optOutType;
    }
    public void setOptOutType(OptOutTemporaryOverrideType optOutType) {
        this.optOutType = optOutType;
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
    
    public int getOptOutValue() {
        return optOutValue;
    }
    public void setOptOutValue(int optOutValue) {
        this.optOutValue = optOutValue;
    }
    
    public OptOutEnabled getOptOutEnabled() {
        if (OptOutTemporaryOverrideType.ENABLED == optOutType) {
            return OptOutEnabled.valueOf(optOutValue);
        }
        return null;
    }
    
    public Integer getAssignedProgramId() {
        return assignedProgramId;
    }
    public void setAssignedProgramId(Integer assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
    }
    
}