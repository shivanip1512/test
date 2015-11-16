package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.List;

import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.util.LazyList;

public class AssignedHardwareConfig extends HardwareConfig {
    
    private int accountId;
    private int inventoryId;
    private String action;
    private List<ProgramEnrollmentDto> programEnrollments = LazyList.ofInstance(ProgramEnrollmentDto.class);
    
    public AssignedHardwareConfig(HardwareConfigType configType) {
        super(configType);
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public int getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public List<ProgramEnrollmentDto> getProgramEnrollments() {
        return programEnrollments;
    }
    
    public void setProgramEnrollments(List<ProgramEnrollmentDto> programEnrollments) {
        this.programEnrollments = programEnrollments;
    }

}