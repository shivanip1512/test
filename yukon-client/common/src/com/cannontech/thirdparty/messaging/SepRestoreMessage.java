package com.cannontech.thirdparty.messaging;

public class SepRestoreMessage {
    private int groupId;
    private long restoreTime;
    private int eventFlags;
    
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    public long getRestoreTime() {
        return restoreTime;
    }
    public void setRestoreTime(long restoreTime) {
        this.restoreTime = restoreTime;
    }
    public int getEventFlags() {
        return eventFlags;
    }
    public void setEventFlags(int eventFlags) {
        this.eventFlags = eventFlags;
    }
}
