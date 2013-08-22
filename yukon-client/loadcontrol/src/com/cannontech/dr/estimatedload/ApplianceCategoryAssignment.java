package com.cannontech.dr.estimatedload;

import com.cannontech.stars.dr.appliance.model.ApplianceCategory;

public class ApplianceCategoryAssignment {

    private ApplianceCategory applianceCategory;
    private Integer formulaId; // null if not assigned


    public ApplianceCategoryAssignment(ApplianceCategory applianceCategory, Integer formulaId) {
        this.applianceCategory = applianceCategory;
        this.formulaId = formulaId;
    }

    public Integer getFormulaId() {
        return formulaId;
    }

    public void setFormulaId(Integer formulaId) {
        this.formulaId = formulaId;
    }

    public ApplianceCategory getApplianceCategory() {
        return applianceCategory;
    }

    public void setApplianceCategory(ApplianceCategory applianceCategory) {
        this.applianceCategory = applianceCategory;
    }
}
