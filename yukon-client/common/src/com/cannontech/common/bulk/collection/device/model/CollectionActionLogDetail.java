package com.cannontech.common.bulk.collection.device.model;

import org.joda.time.Instant;

import com.cannontech.common.pao.YukonPao;

public class CollectionActionLogDetail {
    private YukonPao pao;
    private CollectionActionDetail detail;
    private Instant time;
    private String executionExceptionText;
    private String deviceErrorText;
    
    public CollectionActionLogDetail(YukonPao pao, CollectionActionDetail detail) {
        this.pao = pao;
        this.detail = detail;
    }

    public CollectionActionDetail getDetail() {
        return detail;
    }

    public void setDetail(CollectionActionDetail detail) {
        this.detail = detail;
    }

    public YukonPao getPao() {
        return pao;
    }

    public void setPao(YukonPao pao) {
        this.pao = pao;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getExecutionExceptionText() {
        return executionExceptionText;
    }

    public void setExecutionExceptionText(String executionExceptionText) {
        this.executionExceptionText = executionExceptionText;
    }

    public String getDeviceErrorText() {
        return deviceErrorText;
    }

    public void setDeviceErrorText(String deviceErrorText) {
        this.deviceErrorText = deviceErrorText;
    }    
}
