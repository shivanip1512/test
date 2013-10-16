package com.cannontech.dr.estimatedload;

public final class EstimatedLoadApplianceCategoryInfo {
    private final int applianceCategoryId;
    private final double avgKwLoad;

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
