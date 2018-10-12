package com.cannontech.dr.nest.model;

public class NestSyncDetail {
    private int id;
    private int syncId;
    private NestSyncType type;
    private NestSyncI18nKey reasonKey;
    private String reasonValue;
    private NestSyncI18nKey actionKey;
    private String actionValue;
    
    public NestSyncDetail(int id, int syncId, NestSyncType type, NestSyncI18nKey reasonKey, String reasonValue,
            NestSyncI18nKey actionKey, String actionValue) {
        this.id = id;
        this.syncId = syncId;
        this.type = type;
        this.reasonKey = reasonKey;
        this.reasonValue = reasonValue;
        this.actionKey = actionKey;
        this.actionValue = actionValue;
    }
    
    public NestSyncDetail() {
 
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getSyncId() {
        return syncId;
    }
    public void setSyncId(int syncId) {
        this.syncId = syncId;
    }
    public NestSyncType getType() {
        return type;
    }
    public void setType(NestSyncType type) {
        this.type = type;
    }
    public NestSyncI18nKey getReasonKey() {
        return reasonKey;
    }
    public void setReasonKey(NestSyncI18nKey reasonKey) {
        this.reasonKey = reasonKey;
    }
    public String getReasonValue() {
        return reasonValue;
    }
    public void setReasonValue(String reasonValue) {
        this.reasonValue = reasonValue;
    }
    public NestSyncI18nKey getActionKey() {
        return actionKey;
    }
    public void setActionKey(NestSyncI18nKey actionKey) {
        this.actionKey = actionKey;
    }
    public String getActionValue() {
        return actionValue;
    }
    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }
}
