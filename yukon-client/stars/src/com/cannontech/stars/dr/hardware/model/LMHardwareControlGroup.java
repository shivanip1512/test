package com.cannontech.stars.dr.hardware.model;

import java.util.Date;

import com.cannontech.user.UserUtils;

public class LMHardwareControlGroup implements Cloneable{
    private Integer controlEntryId;
    private int inventoryId;
    private int lmGroupId;
    private int accountId;
    private Date groupEnrollStart;
    private Date groupEnrollStop;
    private Date optOutStart;
    private Date optOutStop;
    private int type;
    private int relay;
    private int userIdFirstAction;
    private int userIdSecondAction;
    private int programId;
    
    public static final int ENROLLMENT_ENTRY = 1;
    public static final int OPT_OUT_ENTRY = 2;
    
    public LMHardwareControlGroup() { 
    }
    
    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = 0;
        this.userIdFirstAction = UserUtils.USER_DEFAULT_ID;
        this.userIdSecondAction = UserUtils.USER_DEFAULT_ID;
    }

    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type, int userId) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = 0;
        this.userIdFirstAction = userId;
        this.userIdSecondAction = UserUtils.USER_DEFAULT_ID;
    }
    
    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type, int relay, int userId) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = relay;
        this.userIdFirstAction = userId;
        this.userIdSecondAction = UserUtils.USER_DEFAULT_ID;
    }

    public LMHardwareControlGroup clone(){
        LMHardwareControlGroup hardwareControlGroup = new LMHardwareControlGroup();
        if(this.controlEntryId != null)
            hardwareControlGroup.setControlEntryId(this.controlEntryId);
        hardwareControlGroup.setInventoryId(this.inventoryId);
        hardwareControlGroup.setLmGroupId(this.lmGroupId);
        hardwareControlGroup.setAccountId(this.accountId);
        if(this.groupEnrollStart != null)
            hardwareControlGroup.setGroupEnrollStart((Date)this.groupEnrollStart.clone());
        if(this.groupEnrollStop != null)
        	hardwareControlGroup.setGroupEnrollStop((Date)this.groupEnrollStop.clone());
        if(this.optOutStart != null)
            hardwareControlGroup.setOptOutStart((Date)this.optOutStart.clone());
        if(this.optOutStop != null)
            hardwareControlGroup.setOptOutStop((Date)this.optOutStop.clone());
        hardwareControlGroup.setType(this.type);
        hardwareControlGroup.setRelay(this.relay);
        hardwareControlGroup.setUserIdFirstAction(this.userIdFirstAction);
        hardwareControlGroup.setUserIdSecondAction(this.userIdSecondAction);
        hardwareControlGroup.setProgramId(this.programId);
        return hardwareControlGroup;
    }
    
    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Integer getControlEntryId() {
        return controlEntryId;
    }

    public void setControlEntryId(Integer controlEntryId) {
        this.controlEntryId = controlEntryId;
    }

    public Date getGroupEnrollStart() {
        return groupEnrollStart;
    }

    public void setGroupEnrollStart(Date groupEnrollStart) {
        this.groupEnrollStart = groupEnrollStart;
    }

    public Date getGroupEnrollStop() {
        return groupEnrollStop;
    }

    public void setGroupEnrollStop(Date groupEnrollStop) {
        this.groupEnrollStop = groupEnrollStop;
    }

    public int getLmGroupId() {
        return lmGroupId;
    }

    public void setLmGroupId(int lmGroupId) {
        this.lmGroupId = lmGroupId;
    }

    public Date getOptOutStart() {
        return optOutStart;
    }

    public void setOptOutStart(Date optOutStart) {
        this.optOutStart = optOutStart;
    }

    public Date getOptOutStop() {
        return optOutStop;
    }

    public void setOptOutStop(Date optOutStop) {
        this.optOutStop = optOutStop;
    }

    public int getProgramId() {
        return programId;
    }
    public void setProgramId(int programId) {
        this.programId = programId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LMHardwareControlGroup other = (LMHardwareControlGroup) obj;
        if (controlEntryId != other.controlEntryId)
            return false;
        return true;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    public int getUserIdFirstAction() {
        return userIdFirstAction;
    }

    public void setUserIdFirstAction(int userId) {
        this.userIdFirstAction = userId;
    }

    public int getUserIdSecondAction() {
        return userIdSecondAction;
    }

    public void setUserIdSecondAction(int userIdSecondAction) {
        this.userIdSecondAction = userIdSecondAction;
    }
    
    public boolean isActiveEnrollment(){
    	if(this.getGroupEnrollStart() != null &&
    	   this.getGroupEnrollStop() == null){
    		return true;
    	}
    	
    	return false;
    }
    
}
