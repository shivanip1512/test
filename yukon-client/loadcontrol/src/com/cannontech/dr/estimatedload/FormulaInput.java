package com.cannontech.dr.estimatedload;

public final class FormulaInput {

    FormulaInputType inputType;
    Integer pointId = null;

    public FormulaInput(FormulaInputType inputType, Integer pointId) {
        this.inputType = inputType;
        this.pointId = pointId;
    }

    public FormulaInputType getInputType() {
        return inputType;
    }

    public Integer getPointId() {
        return pointId;
    }
}
