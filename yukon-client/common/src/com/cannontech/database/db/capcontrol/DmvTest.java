package com.cannontech.database.db.capcontrol;

public class DmvTest {

    private int dmvTestId;
    private String name;
    private int pollingInterval = 30;
    private int dataGatheringDuration = 10;
    private double stepSize = 1.5;
    private int commSuccPercentage = 100;
    
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
    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }
    public int getDataGatheringDuration() {
        return dataGatheringDuration;
    }
    public void setDataGatheringDuration(int dataGatheringDuration) {
        this.dataGatheringDuration = dataGatheringDuration;
    }
    public int getCommSuccPercentage() {
        return commSuccPercentage;
    }
    public void setCommSuccPercentage(int commSuccPercentage) {
        this.commSuccPercentage = commSuccPercentage;
    }
    public int getDmvTestId() {
        return dmvTestId;
    }
    public void setDmvTestId(int dmvTestId) {
        this.dmvTestId = dmvTestId;
    }
}
