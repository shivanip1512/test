package com.cannontech.database.db.capcontrol;

public class DmvTest {

    private Integer dmvTestId;
    private String name;
    private Integer pollingInterval = 10;
    private Integer dataGatheringDuration = 10;
    private Double stepSize = 0.5;
    private Integer commSuccPercentage = 15;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPollingInterval() {
        return pollingInterval;
    }
    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
    public double getStepSize() {
        return stepSize;
    }
    public void setStepSize(Double stepSize) {
        this.stepSize = stepSize;
    }
    public int getDataGatheringDuration() {
        return dataGatheringDuration;
    }
    public void setDataGatheringDuration(Integer dataGatheringDuration) {
        this.dataGatheringDuration = dataGatheringDuration;
    }
    public double getCommSuccPercentage() {
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
