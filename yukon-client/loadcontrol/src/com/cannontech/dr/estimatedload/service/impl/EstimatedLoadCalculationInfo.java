package com.cannontech.dr.estimatedload.service.impl;

import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public final class EstimatedLoadCalculationInfo {

    private final Integer acId;
    private final Double averageKwLoad;
    private final Integer gearId;
    private final Formula applianceCategoryFormula;
    private final Formula gearFormula;

    private final boolean isError;
    private final YukonMessageSourceResolvable errorMessage;

    public EstimatedLoadCalculationInfo(Integer acId, Double averageKwLoad, Integer gearId,
            Formula applianceCategoryFormula, Formula gearFormula) {
        this.acId = acId;
        this.averageKwLoad = averageKwLoad;
        this.gearId = gearId;
        this.applianceCategoryFormula = applianceCategoryFormula;
        this.gearFormula = gearFormula;

        this.isError = false;
        this.errorMessage = null;
    }

    public EstimatedLoadCalculationInfo(YukonMessageSourceResolvable errorMessage) {
        this.acId = null;
        this.averageKwLoad = null;
        this.gearId = null;
        this.applianceCategoryFormula = null;
        this.gearFormula = null;

        this.isError = true;
        this.errorMessage = errorMessage;
    }

    public Integer getAcId() {
        return acId;
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

    public boolean isError() {
        return isError;
    }

    public YukonMessageSourceResolvable getErrorMessage() {
        return errorMessage;
    }

}
