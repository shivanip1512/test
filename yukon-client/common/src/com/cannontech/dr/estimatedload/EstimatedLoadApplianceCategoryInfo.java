package com.cannontech.dr.estimatedload;

public final class EstimatedLoadApplianceCategoryInfo {
    private final int applianceCategoryId;
    private final Double avgKwLoad;

    public EstimatedLoadApplianceCategoryInfo(int applianceCategoryId, Double avgKwLoad) {
        this.applianceCategoryId = applianceCategoryId;
        this.avgKwLoad = avgKwLoad;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public Double getAvgKwLoad() {
        return avgKwLoad;
    }

}
