package com.cannontech.services.systemDataPublisher.service.model;

public class DataCompletenessSummary {

    private Integer recordCount = 0;
    private Integer paoCount = 0;

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public Integer getPaoCount() {
        return paoCount;
    }

    public void setPaoCount(Integer paoCount) {
        this.paoCount = paoCount;
    }

    public void add(DataCompletenessSummary addData) {
        setRecordCount(this.recordCount + addData.getRecordCount());
        setPaoCount(this.paoCount + addData.getPaoCount());
    }

}
