package com.cannontech.core.dao.impl;

import org.joda.time.Instant;
import com.cannontech.core.dao.PaoDao.InfoKey;;

public class DynamicPaoInfo {
    private int paoId;
    private String owner;
    private InfoKey infoKey;
    private String value;
    private Instant timestamp;

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public InfoKey getInfoKey() {
        return infoKey;
    }

    public void setInfoKey(InfoKey infoKey) {
        this.infoKey = infoKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
