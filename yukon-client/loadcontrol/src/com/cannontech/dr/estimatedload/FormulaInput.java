package com.cannontech.dr.estimatedload;

import com.cannontech.dr.estimatedload.enumeration.FormulaInputType;

public final class FormulaInput {

    private final FormulaInputType inputType;
    private final double min;
    private final double max;
    private final Integer pointId;
    

    public FormulaInput(FormulaInputType inputType, double min, double max, Integer pointId) {
        this.inputType = inputType;
        this.min = min;
        this.max = max;
        this.pointId = pointId;
    }

    public FormulaInputType getInputType() {
        return inputType;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public Integer getPointId() {
        return pointId;
    }
}
