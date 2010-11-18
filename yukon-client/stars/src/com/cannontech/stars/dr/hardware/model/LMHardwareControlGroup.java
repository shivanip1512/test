package com.cannontech.stars.dr.hardware.model;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.util.OpenInterval;
import com.cannontech.user.UserUtils;

public class LMHardwareControlGroup implements Cloneable{
    private Integer controlEntryId;
    private int inventoryId;
    private int lmGroupId;
    private int accountId;
    private Instant groupEnrollStart;
    private Instant groupEnrollStop;
    private Instant optOutStart;
    private Instant optOutStop;
    private int type;
    private int relay;
    private int userIdFirstAction;
    private int userIdSecondAction;
    private int programId;
    
    public static final int ENROLLMENT_ENTRY = 1;
    public static final int OPT_OUT_ENTRY = 2;
    
    public LMHardwareControlGroup() { 
    }
    
    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type, int programId) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = 0;
        this.programId = programId;
        this.userIdFirstAction = UserUtils.USER_DEFAULT_ID;
        this.userIdSecondAction = UserUtils.USER_DEFAULT_ID;
    }

    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type, int programId, int userId) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = 0;
        this.programId = programId;
        this.userIdFirstAction = userId;
        this.userIdSecondAction = UserUtils.USER_DEFAULT_ID;
    }
    
    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type, int relay, int programId, int userId) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = relay;
        this.programId = programId;
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
        hardwareControlGroup.setGroupEnrollStart(this.groupEnrollStart);
      	hardwareControlGroup.setGroupEnrollStop(this.groupEnrollStop);
        hardwareControlGroup.setOptOutStart(this.optOutStart);
        hardwareControlGroup.setOptOutStop(this.optOutStop);
        hardwareControlGroup.setType(this.type);
        hardwareControlGroup.setRelay(this.relay);
        hardwareControlGroup.setUserIdFirstAction(this.userIdFirstAction);
        hardwareControlGroup.setUserIdSecondAction(this.userIdSecondAction);
        hardwareControlGroup.setProgramId(this.programId);
        return hardwareControlGroup;
    }
    
    public OpenInterval getEnrollmentInterval() {
        if (groupEnrollStop != null) {
            return OpenInterval.createClosed(groupEnrollStart, groupEnrollStop);
        } else {
            return OpenInterval.createOpenEnd(groupEnrollStart);
        }
    }
    
    public Duration getEnrollmentDuration() {
        // Creating interval to represent any time prior to now and intersecting it
        // with the enrollment to find the current enrollment interval
        OpenInterval enrollmentInterval = getEnrollmentInterval();
        OpenInterval openStartToNow = OpenInterval.createOpenStart(new Instant());
        OpenInterval enrollmentIntervalUntilNow = enrollmentInterval.overlap(openStartToNow);
        
        return enrollmentIntervalUntilNow.toClosedInterval().toDuration();
    }
    
    public OpenInterval getOptOutInterval() {
        if (optOutStop != null) {
            return OpenInterval.createClosed(optOutStart, optOutStop);
        } else {
            return OpenInterval.createOpenEnd(optOutStart);
        }
    }
    
    public Duration getOptOutDuration() {
        OpenInterval optOutInterval = getOptOutInterval();

        if (optOutInterval.isOpenEnd()) {
            return optOutInterval.withCurrentEnd().toClosedInterval().toDuration();
        } else {
            return optOutInterval.toClosedInterval().toDuration();
        }
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

    public Instant getGroupEnrollStart() {
        return groupEnrollStart;
    }

    public void setGroupEnrollStart(Instant groupEnrollStart) {
        this.groupEnrollStart = groupEnrollStart;
    }

    public Instant getGroupEnrollStop() {
        return groupEnrollStop;
    }

    public void setGroupEnrollStop(Instant groupEnrollStop) {
        this.groupEnrollStop = groupEnrollStop;
    }

    public int getLmGroupId() {
        return lmGroupId;
    }

    public void setLmGroupId(int lmGroupId) {
        this.lmGroupId = lmGroupId;
    }

    public Instant getOptOutStart() {
        return optOutStart;
    }

    public void setOptOutStart(Instant optOutStart) {
        this.optOutStart = optOutStart;
    }

    public Instant getOptOutStop() {
        return optOutStop;
    }

    public void setOptOutStop(Instant optOutStop) {
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
