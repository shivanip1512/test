package com.cannontech.database.db.capcontrol;

public class DmvTest {

    private int dmvTestId;
    private String name;
    private int dataArchivingInterval = 30;
    private int intervalDataGatheringDuration = 10;
    private double stepSize = 1.5;
    private int commSuccPercentage = 100;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getDataArchivingInterval() {
        return dataArchivingInterval;
    }
    public void setDataArchivingInterval(int dataArchivingInterval) {
        this.dataArchivingInterval = dataArchivingInterval;
    }
    public double getStepSize() {
        return stepSize;
    }
    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }
    public int getIntervalDataGatheringDuration() {
        return intervalDataGatheringDuration;
    }
    public void setIntervalDataGatheringDuration(int intervalDataGatheringDuration) {
        this.intervalDataGatheringDuration = intervalDataGatheringDuration;
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
