package com.cannontech.dr.estimatedload;

public final class EstimatedLoadCalculationInfo {

    private final int applianceCategoryId;
    private final double averageKwLoad;
    private final int gearId;
    private final Formula applianceCategoryFormula;
    private final Formula gearFormula;

    public EstimatedLoadCalculationInfo(Integer acId, Double averageKwLoad, Integer gearId,
            Formula applianceCategoryFormula, Formula gearFormula) {
        this.applianceCategoryId = acId;
        this.averageKwLoad = averageKwLoad;
        this.gearId = gearId;
        this.applianceCategoryFormula = applianceCategoryFormula;
        this.gearFormula = gearFormula;
    }

    public Integer getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public Double getAverageKwLoad() {
        return averageKwLoad;
    }

    public Integer getGearId() {
        return gearId;
    }

    public Formula getApplianceCategoryFormula() {
        return applianceCategoryFormula;
    }

    public Formula getGearFormula() {
        return gearFormula;
    }
}
