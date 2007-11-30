package com.cannontech.stars.dr.hardware.model;

import java.util.Date;

import com.cannontech.user.UserUtils;

public class LMHardwareControlGroup {
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
    private int userId;
    
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
        this.userId = UserUtils.USER_DEFAULT_ID;
    }
    
    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type, int userId) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = 0;
        this.userId = userId;
    }
    
    public LMHardwareControlGroup(int inventoryId, int loadGroupId, int accountId, int type, int relay, int userId) { 
        this.inventoryId = inventoryId;
        this.lmGroupId = loadGroupId;
        this.accountId = accountId;
        this.type = type;
        this.relay = relay;
        this.userId = userId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getLMGroupId() {
        return lmGroupId;
    }

    public void setLMGroupId(int groupId) {
        lmGroupId = groupId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
