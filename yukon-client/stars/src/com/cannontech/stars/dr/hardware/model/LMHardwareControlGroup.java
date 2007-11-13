package com.cannontech.stars.dr.hardware.model;

import java.util.Date;

public class LMHardwareControlGroup {
    private int controlEntryId;
    private int inventoryId;
    private int lmGroupId;
    private int accountId;
    private Date groupEnrollStart;
    private Date groupEnrollStop;
    private Date optOutStart;
    private Date optOutStop;
    
    public LMHardwareControlGroup() { 
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

    public int getControlEntryId() {
        return controlEntryId;
    }

    public void setControlEntryId(int controlEntryId) {
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
}
