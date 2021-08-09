package com.cannontech.common.rfn.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

public class RfnModelChange {
    private Integer deviceId;
    private String oldModel;
    private String newModel;
    private Instant dataTimestamp;
    public Integer getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }
    public String getOldModel() {
        return oldModel;
    }
    public void setOldModel(String oldModel) {
        this.oldModel = oldModel;
    }
    public String getNewModel() {
        return newModel;
    }
    public void setNewModel(String newModel) {
        this.newModel = newModel;
    }
    public Instant getDataTimestamp() {
        return dataTimestamp;
    }
    public void setDataTimestamp(Instant dataTimestamp) {
        this.dataTimestamp = dataTimestamp;
    } 
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
