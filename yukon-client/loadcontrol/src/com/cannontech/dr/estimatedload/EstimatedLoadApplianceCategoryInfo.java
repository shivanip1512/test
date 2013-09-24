package com.cannontech.dr.estimatedload;

public class EstimatedLoadApplianceCategoryInfo {
    int applianceCategoryId;
    double avgKwLoad;

    public EstimatedLoadApplianceCategoryInfo(int applianceCategoryId, double avgKwLoad) {
        this.applianceCategoryId = applianceCategoryId;
        this.avgKwLoad = avgKwLoad;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public double getAvgKwLoad() {
        return avgKwLoad;
    }

}
