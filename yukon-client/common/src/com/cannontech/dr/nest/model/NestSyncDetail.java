package com.cannontech.dr.nest.model;

import java.util.HashMap;
import java.util.Map;

public class NestSyncDetail {
    private int id;
    private int syncId;
    private NestSyncType type;
    private NestSyncI18nKey reasonKey;
    private NestSyncI18nKey actionKey;
    private Map<NestSyncI18nValue, String> values = new HashMap<>();
    
    public NestSyncDetail(int id, int syncId, NestSyncType type, NestSyncI18nKey reasonKey, NestSyncI18nKey actionKey) {
        this.id = id;
        this.syncId = syncId;
        this.type = type;
        this.reasonKey = reasonKey;
        this.actionKey = actionKey;
    }
    
    public NestSyncDetail() {
 
    }
    
    public void setActionKey(NestSyncI18nKey actionKey) {
        this.actionKey = actionKey;
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
    public NestSyncI18nKey getActionKey() {
        return actionKey;
    }

    public Map<NestSyncI18nValue, String> getValues() {
        return values;
    }

    public void addValue(NestSyncI18nValue type, String value) {
        values.put(type, value);
    }
}
