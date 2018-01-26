package com.cannontech.database.db.capcontrol;

public class DmvTest {

    private Integer dmvTestId;
    private String name;
    private Integer pollingInterval = 30;
    private Integer dataGatheringDuration = 10;
    private Double stepSize = 1.5;
    private Integer commSuccPercentage = 100;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPollingInterval() {
        return pollingInterval;
    }
    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
    public Double getStepSize() {
        return stepSize;
    }
    public void setStepSize(Double stepSize) {
        this.stepSize = stepSize;
    }
    public Integer getDataGatheringDuration() {
        return dataGatheringDuration;
    }
    public void setDataGatheringDuration(Integer dataGatheringDuration) {
        this.dataGatheringDuration = dataGatheringDuration;
    }
    public Integer getCommSuccPercentage() {
        return commSuccPercentage;
    }
    public void setCommSuccPercentage(Integer commSuccPercentage) {
        this.commSuccPercentage = commSuccPercentage;
    }
    public Integer getDmvTestId() {
        return dmvTestId;
    }
    public void setDmvTestId(Integer dmvTestId) {
        this.dmvTestId = dmvTestId;
    }
}
